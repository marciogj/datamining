package br.udesc.dcc.bdes.server.rest.api.track;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import br.udesc.dcc.bdes.analysis.MeanTransportSplitter;
import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.google.GeocodeAddress;
import br.udesc.dcc.bdes.google.InverseGeocodingClient;
import br.udesc.dcc.bdes.io.GeocodeAddressDTOFileWriter;
import br.udesc.dcc.bdes.io.OpenWheatherDTOFileWriter;
import br.udesc.dcc.bdes.io.TrackDTOCSVFileWriter;
import br.udesc.dcc.bdes.io.TrackDTOv2CSVFileWriter;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.DriverProfile;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.model.TransportType;
import br.udesc.dcc.bdes.model.UDriverId;
import br.udesc.dcc.bdes.openweather.OpenWeatherClient;
import br.udesc.dcc.bdes.openweather.dto.OpenWeatherConditionDTO;
import br.udesc.dcc.bdes.repository.MemoryRepository;
import br.udesc.dcc.bdes.server.JettyServer;
import br.udesc.dcc.bdes.server.rest.APIPath;
import br.udesc.dcc.bdes.server.rest.api.track.dto.CoordinateDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrackDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectoryMapper;

import com.google.gson.Gson;

@Path(APIPath.TRACK)
public class TrackAPI {
	private final Gson gson = new Gson();
	private final MemoryRepository repository = MemoryRepository.get();
	
	/**
	 * 
	 * curl -X POST -H "Content-Type: application/json" http://localhost:9090/services/track -d 
	 *   '{ "deviceId": "aaaa", "userId": "bbb", "coordinates": [ 
	 *      { "latitude": "0", "longitude": "1" }, 
	 *      { "latitude": "3", "longitude": "4"}  
	 *    ]}'
	 * 
	 * @param trackDto
	 */
	@POST
	@Path("/evaluate")
    public void evaluate(TrackDTO trackDto) {
		System.out.println("Evaluate Source: " + trackDto.deviceId + "@" + trackDto.userId+ " - Coordinates: " + trackDto.coordinates.size());
		evaluateTrack(trackDto);
		notifyWSClients(trackDto);			
    }
	
	@POST
	@Path("/v1/save")
    public Response saveCompatibility(TrackDTO trackDto) {
		System.out.println("Evaluate Source: " + trackDto.deviceId + "@" + trackDto.userId+ " - Coordinates: " + trackDto.coordinates.size());
		if (trackDto.coordinates.isEmpty()) {
			return Response.noContent().build();
		}
		CoordinateDTO firstCoord = trackDto.coordinates.get(0);
		String datetime = "" + firstCoord.timestamp;
		String filename = trackDto.userId + "_" + trackDto.deviceId + "_" + datetime;
		TrackDTOCSVFileWriter.write(trackDto, filename + ".csv");
		saveSemanticEnrichment(firstCoord.latitude, firstCoord.longitude, filename);
		
		return Response.status(Status.CREATED).build();
    }
	
	/**
	 *  Enforces the ISO 8601 date format
	 */
	@POST
	@Path("/v2/save")
    public Response save(TrackDTO trackDto) {
		System.out.println("Evaluate Source: " + trackDto.deviceId + "@" + trackDto.userId+ " - Coordinates: " + trackDto.coordinates.size());
		if (trackDto.coordinates.isEmpty()) {
			return Response.noContent().build();
		}
		CoordinateDTO firstCoord = trackDto.coordinates.get(0);
		String datetime = removeInvalidFilenameChars(firstCoord.dateTime);
		String filename = trackDto.userId + "_" + trackDto.deviceId + "_" + datetime;
		TrackDTOv2CSVFileWriter.write(trackDto, filename + ".csv");
		saveSemanticEnrichment(firstCoord.latitude, firstCoord.longitude, filename);
		
		return Response.status(Status.CREATED).build();
    }
	
	@POST
	@Path("/save-evaluate")
    public void saveEvaluateTrack(TrackDTO trackDto) {
		//Save track is executed as a separated process
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			save(trackDto);
		});
		
		evaluate(trackDto);
    }
	
	private void saveSemanticEnrichment(double latitude, double longitude, String fileprefix) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			try {
				Optional<OpenWeatherConditionDTO> optWheather = getWeather(latitude, longitude);
				if (optWheather.isPresent()) {
					OpenWheatherDTOFileWriter.write(optWheather.get(), fileprefix + "_weather.json");
				}
				
				Optional<GeocodeAddress> optAddress = getAddress(latitude, longitude);
				if (optAddress.isPresent()) {
					GeocodeAddressDTOFileWriter.write(optAddress.get(), fileprefix + "_address.json");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private static String removeInvalidFilenameChars(String isoDatetime) {
		System.err.println(isoDatetime);
		return isoDatetime.replaceAll(":", "").replaceAll("+", "p").replaceAll("-", "m");
	}
	
	private void evaluateTrack(TrackDTO trackDto) {
		TrajectoryEvaluator trajectoryEval = new TrajectoryEvaluator();
		Trajectory receivedTrajectory = TrajectoryMapper.fromDto(trackDto);
				
		//noiseCleaning();
		
		UDriverId driverId = new UDriverId(trackDto.userId);
		Optional<DriverProfile> optDriverProfile = repository.loadDriverProfile(driverId);
		if (!optDriverProfile.isPresent()) {
			repository.save(new DriverProfile(driverId, new DeviceId(trackDto.deviceId)));
			optDriverProfile = repository.loadDriverProfile(driverId);
		}
		DriverProfile driverProfile = optDriverProfile.get();
		
		Optional<TrajectoryEvaluator> dbTrajectory = repository.loadLatestTrajectoryEvaluationById(new DeviceId(trackDto.deviceId));
		long timeTolerance =  1000 * 60 * 5;//5 min
		boolean isNewTrajectory = true;
		
		double initialDistance = 0;
		long initialTime = 0;
		Trajectory previousTrajectory = null;
		if (dbTrajectory.isPresent()) {	
			//TODO: Break trajectories considering contextual information: stops and place
			previousTrajectory = dbTrajectory.get().getTrajectory();
			Optional<Coordinate> latestCoodrinate = previousTrajectory.getLastestCoordinate();
			Optional<Coordinate> receivedCoordinate = receivedTrajectory.getFirstCoordintae();
			
			long lastTimePreviousCoord = receivedCoordinate.isPresent() ? receivedCoordinate.get().getDateTimeInMillis() : 0;
			long firstTimeCurrentCoord = latestCoodrinate.isPresent() ? latestCoodrinate.get().getDateTimeInMillis() : 0;
			long difference = Math.abs(lastTimePreviousCoord - firstTimeCurrentCoord);
			//Evaluates whether it is the same trajectory or a new one
			isNewTrajectory = difference > timeTolerance;
			if (!isNewTrajectory) {
				trajectoryEval = dbTrajectory.get();
				initialDistance = trajectoryEval.getTotalDistance();
				initialTime = trajectoryEval.getTotalTime();
			} 
		}
		
		List<Trajectory> subtrajectoriesByTime = trajectoryEval.subtrajectoriesByStop(receivedTrajectory);
		//List<Trajectory> subtrajectoriesByTime = trajectoryEval.subtrajectoriesByTime(receivedTrajectory, timeTolerance);
		System.out.println("Subtrajectories: " + subtrajectoriesByTime.size());
		
		//Map<Trajectory,TransportType> subtrajectoriesByTransport = new HashMap<>();
		//for (Trajectory subTrajectory : subtrajectoriesByTime) {
		//	subtrajectoriesByTransport.putAll(trajectoryEval.subtrajectoriesByTransport(subTrajectory));
		//}
		
		
		//TODO: Avaliar se e necessario mesmo ordenar apos a clusterizar
//		Collections.sort(subTrajectory.getCoordinates(), new Comparator<Coordinate>(){
//			public int compare(Coordinate c1, Coordinate c2){
//				return c1.getDateTime().compareTo(c2.getDateTime());
//			}
//		});
		
		
		List<Trajectory> trajectoriesByMeans = new ArrayList<>();
		for (Trajectory subTrajectory : subtrajectoriesByTime) {
			if (subTrajectory.isEmpty()) continue;
			//System.out.println(subTrajectory.getStart() + " - " + subTrajectory.getEnd());
			trajectoriesByMeans.addAll(MeanTransportSplitter.subBySpeed(subTrajectory));
			/*for(Entry<Trajectory, TransportType> entry : trajectoriesByMeans.entrySet()) {
				Trajectory t = entry.getKey();
				System.out.println(entry.getValue().name());
				System.out.println(t.getStart() + " - " + t.getEnd());
			}*/
		}
		
		
		//for (Trajectory subTrajectory : subtrajectoriesByTime) {
		System.out.println("Subtrajectories by Transport:" + trajectoriesByMeans.size());
		for (Trajectory subTrajectory : trajectoriesByMeans) {
			if (subTrajectory.getTransportType() == TransportType.NON_MOTORIZED) continue;
			//boolean isSameMean = subTrajectory.getTransportMean().equals(previousTrajectory != null ? previousTrajectory.getTransportMean() : null);
			boolean isMotorized = subTrajectory.getTransportType() == TransportType.MOTORIZED;
			
			
			System.out.println(subTrajectory.getStart() + " - " + subTrajectory.getEnd());
			boolean externalData = false; 
			//boolean externalData = true;
	
			if (externalData) {
				Optional<Coordinate> optCoord = subTrajectory.getFirstCoordintae();
				if (optCoord.isPresent()) {
					Coordinate coord = optCoord.get();
					Optional<OpenWeatherConditionDTO> optWeather = getWeather(coord.getLatitude(), coord.getLongitude());
					Optional<GeocodeAddress> optAddress = getAddress(coord.getLatitude(), coord.getLongitude());
					trajectoryEval.evaluate(subTrajectory, optWeather, optAddress);
				}
			} else if (isMotorized) {
				trajectoryEval.evaluate(subTrajectory);
			}
				
			if (isNewTrajectory) {			
				repository.save(new DeviceId(trackDto.deviceId), trajectoryEval);					
				driverProfile.increaseTraveledDistance(trajectoryEval.getTotalDistance());
				driverProfile.increaseTraveledTime(trajectoryEval.getTotalTime());
				driverProfile.increaseTrajectory();
			} else {
				repository.updateLatest(new DeviceId(trackDto.deviceId), trajectoryEval);
				
				//TODO: Review why it is considerede a new trajetcory
				isNewTrajectory  = true; //the next trajectory is a new one from subtrajectories
				//lastTimePreviousCoord = subTrajectory.getFirstCoordintae().get().getDateTimeInMillis();
				
				driverProfile.increaseTraveledDistance(trajectoryEval.getTotalDistance() - initialDistance);
				driverProfile.increaseTraveledTime(trajectoryEval.getTotalTime() - initialTime);
			}
			
			driverProfile.addAggressiveIndex(trajectoryEval.getAggressiveIndex());
			driverProfile.increaseAlerts(trajectoryEval.getNewAlerts());
			driverProfile.updateMaxAggressiveIndex(trajectoryEval.getAggressiveIndex());
			
			
			trajectoryEval.resetNewAlerts();
			
			//previousTrajectory = trajectoryEval.getTrajectory();
			trajectoryEval = new TrajectoryEvaluator();
		}
		
	}
	
	private  Optional<GeocodeAddress> getAddress(double latitude, double longitude) {
		try {
			return InverseGeocodingClient.getAddresses(latitude, longitude, JettyServer.get().getGoogleMapsKey().get());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	private Optional<OpenWeatherConditionDTO> getWeather(double latitude, double longitude) {
		try {
			Optional<String> openWetaherKey = JettyServer.get().getOpenWeatherKey();
			if (openWetaherKey.isPresent()) {
				return OpenWeatherClient.weatherByCooordinate(latitude, longitude, openWetaherKey.get());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}	
		return Optional.empty();
	}
	
	private void notifyWSClients(TrackDTO trackDto) {
		JettyServer server = JettyServer.get();
		List<Session> sessions = server.getRegisteredSessions();
		sessions.forEach( session -> {
			System.out.println("Sending message to WebSocket session");
			RemoteEndpoint remote = session.getRemote();
			remote.sendStringByFuture(gson.toJson(trackDto));
		});
	}
	
//	public void noiseCleaing() {
//		//--------------------------------------------------------Cleaning noises
//				
//				DBScan<Coordinate> dbscan = new DBScan<>();
//				DBScanResult<Coordinate> dbscaneResult = dbscan.evaluate(receivedTrajectory.getCoordinates(), 50.0, 5, Coordinate::distance);
//				Trajectory cleanTrajectory = new Trajectory();
//				dbscaneResult.getClusters().forEach( cluster -> cleanTrajectory.addAll(cluster.getElements()));
//				receivedTrajectory.setCoordinates(cleanTrajectory.getCoordinates());
//				
//				
//				
//				Collections.sort(receivedTrajectory.getCoordinates(), new Comparator<Coordinate>(){
//					public int compare(Coordinate c1, Coordinate c2){
//						return c1.getDateTime().compareTo(c2.getDateTime());
//					}
//				});
//				
//				
//				//
//				//--------------------------------------------------------
//	}
	
} 