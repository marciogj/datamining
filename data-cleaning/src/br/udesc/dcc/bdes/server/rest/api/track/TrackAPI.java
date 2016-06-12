package br.udesc.dcc.bdes.server.rest.api.track;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.google.GeocodeAddress;
import br.udesc.dcc.bdes.google.InverseGeocodingClient;
import br.udesc.dcc.bdes.io.OpenWheatherDTOFileWriter;
import br.udesc.dcc.bdes.io.TrackDTOCSVFileWriter;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.DriverProfile;
import br.udesc.dcc.bdes.model.Trajectory;
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
	@Path("/save")
    public void save(TrackDTO trackDto) {
		System.out.println("Evaluate Source: " + trackDto.deviceId + "@" + trackDto.userId+ " - Coordinates: " + trackDto.coordinates.size());
		if (trackDto.coordinates.isEmpty()) return;
		CoordinateDTO firstCoord = trackDto.coordinates.get(0);
		String datetime = removeInvalidFilenameChars(firstCoord.dateTime);
		String filename = trackDto.userId + "_" + trackDto.deviceId + "_" + datetime;
		
		TrackDTOCSVFileWriter.write(trackDto, filename + ".csv");
		
		Optional<OpenWeatherConditionDTO> optWheather = getWeather(firstCoord.latitude, firstCoord.longitude);
		if (optWheather.isPresent()) {
			OpenWheatherDTOFileWriter.write(optWheather.get(), filename + "_weather.json");
		}
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
	
	private static String removeInvalidFilenameChars(String isoDatetime) {
		return isoDatetime.replaceAll(":", "").replaceAll("+", "p").replaceAll("-", "m");
	}
	
	private void evaluateTrack(TrackDTO trackDto) {
		TrajectoryEvaluator trajectoryEval = new TrajectoryEvaluator();
		Trajectory receivedTrajectory = TrajectoryMapper.fromDto(trackDto);
				
		//--------------------------------------------------------Cleaning noises
		/*
		DBScan<Coordinate> dbscan = new DBScan<>();
		DBScanResult<Coordinate> dbscaneResult = dbscan.evaluate(receivedTrajectory.getCoordinates(), 50.0, 5, Coordinate::distance);
		Trajectory cleanTrajectory = new Trajectory();
		dbscaneResult.getClusters().forEach( cluster -> cleanTrajectory.addAll(cluster.getElements()));
		receivedTrajectory.setCoordinates(cleanTrajectory.getCoordinates());
		*/
		
		/*
		Collections.sort(receivedTrajectory.getCoordinates(), new Comparator<Coordinate>(){
			public int compare(Coordinate c1, Coordinate c2){
				return c1.getDateTime().compareTo(c2.getDateTime());
			}
		});
		*/
		
		//
		//--------------------------------------------------------
		
		UDriverId driverId = new UDriverId(trackDto.userId);
		Optional<DriverProfile> optDriverProfile = repository.loadDriverProfile(driverId);
		if (!optDriverProfile.isPresent()) {
			repository.save(new DriverProfile(driverId, new DeviceId(trackDto.deviceId)));
			optDriverProfile = repository.loadDriverProfile(driverId);
		}
		DriverProfile driverProfile = optDriverProfile.get();
		
		
		
		Optional<TrajectoryEvaluator> dbTrajectory = repository.loadLatestTrajectoryEvaluationById(new DeviceId(trackDto.deviceId));
		long timeTolerance =  1000 * 60 * 30;//30 min
		boolean isNewTrajectory = true;
		
		if (dbTrajectory.isPresent()) {
			//TODO: Break trajectories considering contextual information: stops and place
			Trajectory previousTrajectory = dbTrajectory.get().getTrajectory();
			Optional<Coordinate> latestCoodrinate = previousTrajectory.getLastestCoordinate();
			Optional<Coordinate> receivedCoordinate = receivedTrajectory.getFirstCoordintae();
			
			long lastTimePreviousCoord = receivedCoordinate.isPresent() ? receivedCoordinate.get().getDateTimeInMillis() : 0;
			long firstTimeCurrentCoord = latestCoodrinate.isPresent() ? latestCoodrinate.get().getDateTimeInMillis() : 0;
			long difference = Math.abs(lastTimePreviousCoord - firstTimeCurrentCoord);
			//Evaluates whether it is the same trajectory or a new one
			isNewTrajectory = difference > timeTolerance;
			if (!isNewTrajectory) {
				trajectoryEval = dbTrajectory.get();
			} 
		}
		
		//identificar o meio de transporte
		//acelera��o baixa
		//velocidade m�dia abaixo de 10km/h
		//velocidade m�xima abaixo de 20 km/h
		
		
		List<Trajectory> subtrajectoriesByTime = trajectoryEval.subtrajectoriesByTime(receivedTrajectory, timeTolerance);
		System.out.println("Subtrajectories: " + subtrajectoriesByTime.size());
		
		//Map<Trajectory,TransportType> subtrajectoriesByTransport = new HashMap<>();
		//for (Trajectory subTrajectory : subtrajectoriesByTime) {
		//	subtrajectoriesByTransport.putAll(trajectoryEval.subtrajectoriesByTransport(subTrajectory));
		//}
		
		
		//TODO: Avaliar se � necess�rio mesmo ordenar ap�s a clusteriza��o
//		Collections.sort(subTrajectory.getCoordinates(), new Comparator<Coordinate>(){
//			public int compare(Coordinate c1, Coordinate c2){
//				return c1.getDateTime().compareTo(c2.getDateTime());
//			}
//		});
		
		
		
		for (Trajectory subTrajectory : subtrajectoriesByTime) {
			//if (subtrajectoriesByTransport.get(subTrajectory) == TransportType.NON_MOTORIZED) {
			//	System.out.println("Ignoring trajectory with " + subTrajectory.size() + " coordinates since it is no motorized");
			//	continue;
			//}
			boolean externalData = false; 
			//boolean externalData = true;
			
			if (externalData) {
				Optional<Coordinate> optCoord = subTrajectory.getFirstCoordintae();
				if (optCoord.isPresent()) {
					Coordinate coord = optCoord.get();
					Optional<OpenWeatherConditionDTO> optWeather = getWeather(coord.getLatitude(), coord.getLongitude());
					Optional<GeocodeAddress> optAddress = getAddress(coord.getLatitude(), coord.getLongitude());
					trajectoryEval.evaluate(subTrajectory.getCoordinates(), optWeather, optAddress);
				}
				
			} else {
				trajectoryEval.evaluate(subTrajectory.getCoordinates());
			}
			
			
			if (isNewTrajectory) {
				
				//Connection conn = null;
				//try {
					repository.save(new DeviceId(trackDto.deviceId), trajectoryEval);
					
					
					
					//conn = DBPool.getConnection().orElseThrow( () -> new RuntimeException("Could not allocate db connection"));
					//TrajectoryDAO dao = new TrajectoryDAO(conn);
					//dao.add(trajectoryEval.getTrajectory());
					
					
				//} catch(Exception e) {
				//	e.printStackTrace();
				//} finally {
				//	DBPool.release(conn);
				//}
			} else {
				repository.updateLatest(new DeviceId(trackDto.deviceId), trajectoryEval);
				isNewTrajectory  = true; //the next trajectory is a new one from subtrajectories
			}
			
			driverProfile.increaseTraveledDistance(trajectoryEval.getTotalDistance());
			driverProfile.increaseTraveledTime(trajectoryEval.getTotalTime());
			driverProfile.addAggressiveIndex(trajectoryEval.getAggressiveIndex());
			driverProfile.increaseAlerts(trajectoryEval.getNewAlerts());
			trajectoryEval.resetNewAlerts();
			
			
			trajectoryEval = new TrajectoryEvaluator();
		}
		
	}
	
	private  Optional<GeocodeAddress> getAddress(double latitude, double longitude) {
		return InverseGeocodingClient.getAddresses(latitude, longitude, JettyServer.get().getGoogleMapsKey().get());
	}
	
	private Optional<OpenWeatherConditionDTO> getWeather(double latitude, double longitude) {
		Optional<String> openWetaherKey = JettyServer.get().getOpenWeatherKey();
		if (openWetaherKey.isPresent()) {
			return OpenWeatherClient.weatherByCooordinate(latitude, longitude, openWetaherKey.get());
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
	
	
	
} 