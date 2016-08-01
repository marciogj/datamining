package br.udesc.dcc.bdes.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import br.udesc.dcc.bdes.datamining.cluster.density.DBScan;
import br.udesc.dcc.bdes.datamining.cluster.density.DBScanResult;
import br.udesc.dcc.bdes.google.geocoding.GeocodeAddress;
import br.udesc.dcc.bdes.google.geocoding.InverseGeocodingClient;
import br.udesc.dcc.bdes.google.places.ImportantPlace;
import br.udesc.dcc.bdes.google.places.ImportantPlacesClient;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.DriverId;
import br.udesc.dcc.bdes.model.DriverProfile;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.model.TransportType;
import br.udesc.dcc.bdes.openweather.OpenWeatherClient;
import br.udesc.dcc.bdes.openweather.dto.OpenWeatherConditionDTO;
import br.udesc.dcc.bdes.repository.memory.MemoryRepository;
import br.udesc.dcc.bdes.server.JettyServer;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrackDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectoryMapper;

public class TrackEvaluator {
	private final MemoryRepository repository = MemoryRepository.get();
	private static final long STOP_TIME_TOLERANCE =  1000 * 60 * 5;

	/**
	 * Step 1
	 * @param trajectory
	 * @return
	 */
	private Trajectory removeNoise(final Trajectory trajectory) {
		DBScan<Coordinate> dbscan = new DBScan<>();
		int minPts = 5;
		double epsDistanceMeters = 110.0;

		/*noise-eval*///Coordinate noise = new Coordinate(-26.888011,-48.990495, 0, LocalDateTime.of(2016, 05, 03, 8, 38, 24) );
		/*noise-eval*///Coordinate noise = new Coordinate(-26.888475, -48.990840, 0, LocalDateTime.of(2016, 05, 03, 8, 38, 24) );
		/*noise-eval*///trajectory.getCoordinates().add(noise);

		DBScanResult<Coordinate> dbscaneResult = dbscan.evaluate(trajectory.getCoordinates(), epsDistanceMeters, minPts, Coordinate::distance);
		Trajectory cleanTrajectory = new Trajectory();

		dbscaneResult.getClusters().forEach( cluster -> cleanTrajectory.addAll(cluster.getElements()));
		System.out.println("Cleaned Trajectory Size: " + cleanTrajectory.size());

		/*noise-eval*///for(Coordinate noiseCoord : dbscaneResult.getNoises()) {
		/*noise-eval*///noiseCoord.setNoise(true);
		/*noise-eval*///cleanTrajectory.add(noiseCoord);
		/*noise-eval*///}


		//Current clustering approach might mess up with coordinates order - so we need to sort it
		Collections.sort(cleanTrajectory.getCoordinates(), new Comparator<Coordinate>(){
			public int compare(Coordinate c1, Coordinate c2){
				return c1.getDateTime().compareTo(c2.getDateTime());
			}
		});

		return cleanTrajectory;
	}
	
	private void removeBadAccuracy(Trajectory trajectory) {
		List<Coordinate> toRemove = new LinkedList<Coordinate>();
		for (Coordinate coord : trajectory.getCoordinates()) {
			if (coord.getAccuracy() > 15) {
				toRemove.add(coord);
			}
		}
		
		trajectory.getCoordinates().removeAll(toRemove);
	}

	public void evaluateAggressiveness(TrackDTO trackDto) {
		DriverId driverId = new DriverId(trackDto.userId);
		DeviceId deviceId = new DeviceId(trackDto.deviceId);

		DriverProfile driverProfile = repository.loadOrCreate(driverId, deviceId);
		
		TrajectoryEvaluator trajectoryEval = new TrajectoryEvaluator(deviceId, driverId);
		Trajectory receivedTrajectory = TrajectoryMapper.fromDto(trackDto);
		//removeBadAccuracy(receivedTrajectory);
		System.out.println("Received Trajectory Size: " + receivedTrajectory.size());

		//Trajectory cleanedTrajectory = removeNoise(receivedTrajectory);
		Trajectory cleanedTrajectory = receivedTrajectory;
		//evaluateAndSave(deviceId, driverId, cleanedTrajectory);
		//TODO: Temp just to make it fast
		
		
		
		List<Trajectory> subtrajectoriesByTime = trajectoryEval.subtrajectoriesByStop(cleanedTrajectory);
		//Save it only to create temporary results
		//evaluateAndSave(deviceId, driverId, subtrajectoriesByTime);

		List<Trajectory> trajectoriesByMeans = new ArrayList<>();
		//Trajectory tmp = Trajectory.sub(cleanedTrajectory);
		for (Trajectory subTrajectory : subtrajectoriesByTime) {
			if (subTrajectory.isEmpty()) continue;
			List<Trajectory> byMeans = MeanTransportSplitter.subBySpeed(subTrajectory);
			//System.out.println("Means Size: " + byMeans.size());
			//			int i = 1;
			//			for(Trajectory t : byMeans) {
			//				System.out.println(t.getTransportType().name() + " - " + t.getFirstCoordintae().get().getDateTime() + " - " + t.getLastestCoordinate().get().getDateTime());
			//				System.out.println();
			//				i++;
			//			}
			trajectoriesByMeans.addAll(byMeans);
			//tmp.addAll(subTrajectory.getCoordinates());
		}

		//Save it only to create temporary results
		//evaluateAndSave(deviceId, driverId, tmp);

		
		for (Trajectory subTrajectory : trajectoriesByMeans) {
			if (subTrajectory.getTransportType() == TransportType.NON_MOTORIZED) continue;
			boolean externalData = false; 
			//boolean externalData = true;

			
			if (externalData) {
				Optional<Coordinate> optCoord = subTrajectory.getFirstCoordinate();
				if (optCoord.isPresent()) {
					//Coordinate coord = optCoord.get();
					
					//Optional<OpenWeatherConditionDTO> optWeather = TrackEvaluator.getWeather(coord.getLatitude(), coord.getLongitude());
					//Optional<GeocodeAddress> optAddress = TrackEvaluator.getAddress(coord.getLatitude(), coord.getLongitude());
										
					trajectoryEval.evaluate(subTrajectory);
					repository.save(deviceId, trajectoryEval);
				}
			} else {
				//	trajectoryEval.evaluate(subTrajectory);
				evaluateAndSave(deviceId, driverId, subTrajectory);
			}
				
			
		
			driverProfile.increaseTraveledDistance(trajectoryEval.getTotalDistance());
			driverProfile.increaseTraveledTime(trajectoryEval.getTotalTime());
			driverProfile.addAggressiveIndex(trajectoryEval.getAggressiveIndex());
			driverProfile.increaseAlerts(trajectoryEval.getNewAlerts());
			driverProfile.updateMaxAggressiveIndex(trajectoryEval.getAggressiveIndex());
			
		}
		 
	}
	
	public static Optional<GeocodeAddress> getAddress(double latitude, double longitude) {
		try {
			Optional<String> googleKey =  JettyServer.get().getGoogleMapsKey();
			if (googleKey.isPresent()) {
				return InverseGeocodingClient.getAddresses(latitude, longitude, JettyServer.get().getGoogleMapsKey().get());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	private void evaluateAndSave(DeviceId deviceId, DriverId driverId, List<Trajectory> trajectories) {
		for (Trajectory trajectory : trajectories) {
			evaluateAndSave(deviceId, driverId, trajectory);
		}
	}

	private void evaluateAndSave(DeviceId deviceId, DriverId driverId, Trajectory trajectory) {
		TrajectoryEvaluator evalSub = new TrajectoryEvaluator(deviceId, driverId);
		evalSub.evaluate(trajectory);
		repository.save(deviceId, evalSub);
	}
	
	public static Optional<OpenWeatherConditionDTO> getWeather(double latitude, double longitude) {
		try {
			Optional<String> openWeatherKey = JettyServer.get().getOpenWeatherKey();
			if (openWeatherKey.isPresent()) {
				return OpenWeatherClient.weatherByCooordinate(latitude, longitude, openWeatherKey.get());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}	
		return Optional.empty();
	}
	
	public static List<ImportantPlace> getImportantPlaces(double latitude, double longitude) {
		try {
			Optional<String> googleKey =  JettyServer.get().getGoogleMapsKey();
			if (googleKey.isPresent()) {
				int radius = 500; 
				return ImportantPlacesClient.getPlaces(latitude, longitude, radius, googleKey.get());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}	
		return new ArrayList<>();
	}

}
