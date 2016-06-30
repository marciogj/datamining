package br.udesc.dcc.bdes.analysis;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.udesc.dcc.bdes.datamining.cluster.density.DBScan;
import br.udesc.dcc.bdes.datamining.cluster.density.DBScanResult;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.DriverId;
import br.udesc.dcc.bdes.model.DriverProfile;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.repository.memory.MemoryRepository;
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
	
	public void evaluateAggressiveness(TrackDTO trackDto) {
		DriverId driverId = new DriverId(trackDto.userId);
		DeviceId deviceId = new DeviceId(trackDto.deviceId);
		
		TrajectoryEvaluator trajectoryEval = new TrajectoryEvaluator(deviceId, driverId);
		Trajectory receivedTrajectory = TrajectoryMapper.fromDto(trackDto);
		System.out.println("Received Trajectory Size: " + receivedTrajectory.size());
		Trajectory cleanedTrajectory = removeNoise(receivedTrajectory);
		

		DriverProfile driverProfile = repository.loadOrCreate(driverId, deviceId);
		trajectoryEval.evaluate(cleanedTrajectory);
	
		
				
		repository.save(deviceId, trajectoryEval);	
		
		List<Trajectory> subtrajectoriesByTime = trajectoryEval.subtrajectoriesByStop(receivedTrajectory);
		for (Trajectory trajectory : subtrajectoriesByTime) {
			TrajectoryEvaluator evalSub = new TrajectoryEvaluator(deviceId, driverId);
			evalSub.evaluate(trajectory);
			repository.save(deviceId, evalSub);
		}
		
		/*
		
		List<Trajectory> trajectoriesByMeans = new ArrayList<>();
		for (Trajectory subTrajectory : subtrajectoriesByTime) {
			if (subTrajectory.isEmpty()) continue;
			trajectoriesByMeans.addAll(MeanTransportSplitter.subBySpeed(subTrajectory));
		}
		
		
		for (Trajectory subTrajectory : trajectoriesByMeans) {
			if (subTrajectory.getTransportType() == TransportType.NON_MOTORIZED) continue;
			boolean isMotorized = subTrajectory.getTransportType() == TransportType.MOTORIZED;
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
			trajectoryEval =new TrajectoryEvaluator(new DeviceId(trackDto.deviceId), new DriverId(trackDto.userId));
		}
		*/
	}
	

}
