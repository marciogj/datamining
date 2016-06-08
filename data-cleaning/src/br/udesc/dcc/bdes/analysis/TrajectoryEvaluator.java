package br.udesc.dcc.bdes.analysis;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import br.udesc.dcc.bdes.google.GeocodeAddress;
import br.udesc.dcc.bdes.model.Acceleration;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Distance;
import br.udesc.dcc.bdes.model.Speed;
import br.udesc.dcc.bdes.model.Time;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.model.TrajectoryEvaluation;
import br.udesc.dcc.bdes.openweather.dto.OpenWeatherConditionDTO;
import br.udesc.dcc.bdes.server.rest.api.track.Alert;
import br.udesc.dcc.bdes.server.rest.api.track.SpeedIndexEval;


/**
 * 
 * 
 * Speed always return m/s (meter per second)
 * Distance is stored as meters
 * Time is stored in seconds
 * 
 * @author marciogj
 *
 */
public class TrajectoryEvaluator {
	private String id;
	private final Trajectory trajectory = new Trajectory();
	private Coordinate previousCoordinate = null;
	
	private double MAX_ALLOWED_SPEED = 13.89; //50 km/h
	
	private double totalDistance;
	private long totalTime;
	private double speedSum;
	private double maxSpeed;
	private double maxAccecelration;
	private double maxDeceleration;
	private int accecelerationCount;
	private int decelerationCount;
	private Collection<AccInterval> accelerations = new LinkedList<>();
	
	
	private int overMaxSpeedCount;
	private int overMaxAccelerationCount;
	private int overMaxDecelerationCount;
	
	private Coordinate accelerationStart = null;
	private double accDistance;
	private long accTime;
	private double accSpeedSum;
	private int accCoordinateCount;
	
	private Map<String, Double> streets = new HashMap<>();
	private List<Alert> alerts = new LinkedList<>();
	
	
	private SpeedIndexEval speedEvaluator = new SpeedIndexEval();
	
	private Distance segmentDistance = new Distance();
	private List<Double> segmentSpeedIndexes = new LinkedList<>();
	private List<Double> segmentAccelerationIndexes = new LinkedList<>();
	//private List<Double> segmentDeccelerationIndexes = new LinkedList<>();
	
	private List<Double> trajectorySpeedIndexes = new LinkedList<>();
	private List<Double> trajectoryAccelerationIndexes = new LinkedList<>();
	//private List<Double> trajectoryDeccelerationIndexes = new LinkedList<>();
	
	private AccelerationEvaluator accEvaluator = new AccelerationEvaluator();
	
	private Optional<OpenWeatherConditionDTO> currentWeather = Optional.empty(); 
	
	public TrajectoryEvaluator() {
		this.id = UUID.randomUUID().toString();
	}
	
	public String getId() {
		return id;
	}
	
	public String getMainStreet() {
		double max = 0;
		String mainStreet = "";
		for(Entry<String, Double> entry : streets.entrySet()) {
			if (entry.getValue() > max) {
				mainStreet = entry.getKey();
				max = entry.getValue();
			}
		}
		
		return mainStreet;
	}

	public TrajectoryEvaluator(double maxAllowedSpeed, double maxAcceleration, double maxDeceleration) {
		super();
		MAX_ALLOWED_SPEED = maxAllowedSpeed;
	}
	
	public void evaluate(Collection<Coordinate> coordinates, Optional<OpenWeatherConditionDTO> weather, Optional<GeocodeAddress> optAddress) {
		
		double initialDistance = totalDistance;
		for (Coordinate coordinate : coordinates) {
			evaluate(coordinate, weather);
		}
		
		double diffDistance = totalDistance - initialDistance;
		
		if(optAddress.isPresent()) {
			String streetName = optAddress.get().getStreetName();
			Double previous = streets.get(streetName);
			previous = previous == null ? 0 : previous;
			
			streets.put(streetName, previous + diffDistance);
			Speed currentSpeedLimit = SpeedLimit.getSpeedByAddress(streetName);
			MAX_ALLOWED_SPEED = currentSpeedLimit.getMs();
			speedEvaluator.changeMax(new Speed(MAX_ALLOWED_SPEED));
		}
	}
	
	public String getTimeInfo() {
		LocalDateTime startTime = trajectory.getStart().get();
		int hour = startTime.getHour();
		if (hour > 7 && hour < 19 ) {
			return "Horário Comercial";
		}
		
		if (hour > 0 && hour < 5 ) {
			return "Madrugada";
		}
		
		return "Noite"; 
	}
	
	public String getTrafficInfo() {
		LocalDateTime startTime = trajectory.getStart().get();
		int hour = startTime.getHour();
		if (hour > 7 && hour < 19 ) {
			return "Trânsito Intenso";
		}
		
		if (hour > 0 && hour < 5 ) {
			return "Trânsito Livre";
		}
		
		return "Trânsito Tranquilo"; 
	}
	
	public void evaluate(Collection<Coordinate> coordinates) {
		
		for (Coordinate coordinate : coordinates) {
			//update coordinate with values from speed
			Coordinate updatedCoordinate = evaluate(coordinate, Optional.empty());
			coordinate.setAcceleration(updatedCoordinate.getAcceleration());

			//TODO: This update creates noise on Data with provided speed
			//coordinate.setSpeed(updatedCoordinate.getSpeed());
		}
	}

	public void evaluate(final Coordinate coordinate) {
		evaluate(coordinate, Optional.empty());
	}
	
	public AccelerationEvaluator getAccEvaluator() {
		return accEvaluator;
	}
	
	public Coordinate evaluate(final Coordinate coordinate, Optional<OpenWeatherConditionDTO> weather) {
		trajectory.add(coordinate);
		currentWeather = weather;
		
		accCoordinateCount++;
		if (previousCoordinate == null) {
			previousCoordinate = coordinate;
			accelerationStart = coordinate;
			return coordinate;
		}
		
		Coordinate currentCoordinate = this.updateMomentum(previousCoordinate, coordinate);
		double distanceFromPrevious = coordinate.distanceInMeters(previousCoordinate);
		double elapsedTime = (currentCoordinate.getDateTimeInMillis() - previousCoordinate.getDateTimeInMillis())/1000;
		double currentSpeed = currentCoordinate.getSpeed().isPresent() ? currentCoordinate.getSpeed().get() : 0;
		double currentAcceleration = currentCoordinate.getAcceleration();
		double previousAcceleration = previousCoordinate.getAcceleration();
		
		totalTime += elapsedTime;
		totalDistance += distanceFromPrevious;
		speedSum += currentSpeed;
		
		accEvaluator.evaluate(currentAcceleration);
		
		if (currentSpeed > MAX_ALLOWED_SPEED) {
			overMaxSpeedCount++;
		}
		
		if(currentSpeed > maxSpeed) {
			maxSpeed = currentSpeed;
		}
		
		if (maxSpeed > (120/3.6)) {
			System.out.println("NOISE: " + currentSpeed*3.6 + " km/h");
		}
		
		//Update max acceleration/deceleration 
		if (currentAcceleration > maxAccecelration) {
			maxAccecelration = currentAcceleration;
		} else if (currentAcceleration < maxDeceleration) {
			maxDeceleration = currentAcceleration;
		}
		
		boolean isAccelerationInverted = (previousAcceleration > 0) && (currentAcceleration < 0);
		boolean isDecelerationInverted = (previousAcceleration < 0) && (currentAcceleration > 0);
		
		if (isAccelerationInverted) {
			decelerationCount++;
		} else if (isDecelerationInverted) {
			accecelerationCount++;
		} 
		
		if (isAccelerationInverted || isDecelerationInverted) {
			AccInterval acceleration = new AccInterval(accelerationStart, currentCoordinate);
			acceleration.setDistance(accDistance);
			acceleration.setTime(accTime);
			acceleration.setSpeedAvg(accSpeedSum/accCoordinateCount);
			accelerations.add(acceleration);
			
			accelerationStart = currentCoordinate;
			accDistance = 0;
			accSpeedSum = 0;
			accTime = 0;
		} else {
			accDistance += distanceFromPrevious;
			accSpeedSum += currentAcceleration;
			accTime += elapsedTime;
			// It should not increase acc count again
			//if (currentAcceleration >= 0) {
			//	accecelerationCount++;
			//} else {
			//	decelerationCount++;
			//}
		}
		previousCoordinate = currentCoordinate;
		
		segmentDistance.increase(distanceFromPrevious);
		segmentSpeedIndexes.add(speedEvaluator.evaluate(currentSpeed));
		segmentAccelerationIndexes.add(accEvaluator.evaluate(currentAcceleration));
		if (segmentDistance.getKilometers() >= 1) {
			alerts.addAll(speedEvaluator.getAlerts());
			alerts.addAll(accEvaluator.getAlerts());
			speedEvaluator.clearAlerts();
			accEvaluator.clearAlerts();
			
			updateAggressiveIndex();
			clearSegment();
		} 
		
		return currentCoordinate;
	}
		
	private double avgIndex(List<Double> list) {
		if (list.size() == 0) return 0;
		double sum = 0;
		for (Double value : list) {
			sum += value;
		}
		return sum/list.size();
	}
	
	private void updateAggressiveIndex() {
		trajectorySpeedIndexes.add(avgIndex(segmentSpeedIndexes));
		trajectoryAccelerationIndexes.add(avgIndex(segmentAccelerationIndexes));
		//trajectoryDeccelerationIndexes.add(avgIndex(segmentDeccelerationIndexes));
	}
	
	private void clearSegment() {
		segmentDistance.reset();
		segmentSpeedIndexes.clear();
		segmentAccelerationIndexes.clear();
		//segmentDeccelerationIndexes.clear();
	}
			
	private Coordinate updateMomentum(final Coordinate previousCoordinate, final Coordinate currentCoordinate) {
		final long coordinateTime = currentCoordinate.getDateTimeInMillis()/1000;
		final long previousCoordinateTime = previousCoordinate.getDateTimeInMillis()/1000;
		final double deltaTSeconds = Math.abs(coordinateTime - previousCoordinateTime);
		final double distance = Math.abs(currentCoordinate.distanceInMeters(previousCoordinate));
		
		double speed = deltaTSeconds != 0 ?  distance/deltaTSeconds : distance;

		speed = currentCoordinate.getSpeed().isPresent() ? currentCoordinate.getSpeed().get() : speed; 
		double previousSpeed = previousCoordinate.getSpeed().isPresent() ? previousCoordinate.getSpeed().get() : 0; 
		final double deltaV = speed - previousSpeed;
		final double acceleration = deltaV/deltaTSeconds;
		
		Coordinate coordinate = new Coordinate(currentCoordinate.getLatitude(), currentCoordinate.getLongitude(), currentCoordinate.getAltitude(), currentCoordinate.getDateTime());
		coordinate.setAcceleration(acceleration);
		coordinate.setSpeed(speed);
		
		return coordinate;
	}
	
	public TrajectoryEvaluation getCurrentTelemetry() {
		TrajectoryEvaluation telemetry = new TrajectoryEvaluation();
		telemetry.accCount = accecelerationCount;
		telemetry.decCount = decelerationCount;
		telemetry.avgSpeed = new Speed(speedSum/trajectory.size());
		telemetry.coordRate = totalTime == 0 ? 0 : trajectory.size() / totalTime;
		telemetry.maxAcc = new Acceleration(maxAccecelration);
		//telemetry.maxAllowedSpeed = MAX_ALLOWED_SPEED;
		//telemetry.maxSecureAcc =
		
		telemetry.maxDec = new Acceleration(maxDeceleration);
		telemetry.maxSpeed = new Speed(maxSpeed);
		telemetry.overMaxAllowedSpeedCount = overMaxSpeedCount;
		telemetry.overMaxSecureAccCount = overMaxAccelerationCount;
		telemetry.overMaxSecureDecCount = overMaxDecelerationCount;
		telemetry.trajectoryDistance = new Distance(totalDistance);
		telemetry.trajectoryTime = new Time(totalTime);
		telemetry.vehicleId = trajectory.getUserId();
		
		return telemetry;
	}
	
	public Optional<OpenWeatherConditionDTO> getCurrentWeather() {
		return currentWeather;
	}
	
	public Trajectory getTrajectory(){
		return trajectory;
	}
	
	/*
	public Map<Trajectory, TransportType> subtrajectoriesByTransport(Trajectory trajectory) {
		Map<Trajectory, TransportType> trajectories = new HashMap<>();
		DBScan<Coordinate> dbscan = new DBScan<>();
		
		BiFunction<Coordinate, Coordinate, Double> distanceInSpeed = (c1,c2) -> {
			return new Double(Math.abs((c1.getSpeed() - c2.getSpeed())));
		};
		double speed = 10/3.6; //10 km/h in m/s
		DBScanResult<Coordinate> result = dbscan.evaluate(trajectory.getCoordinates(), speed, 5, distanceInSpeed);
		
		Collection<Cluster<Coordinate>> clusters = result.getClusters();
		for(Cluster<Coordinate> cluster : clusters) {
			int motorizedChance = 0;
			int nonMotorizedChance = 0;
			Trajectory t = new Trajectory();
			t.addAll(cluster.getElements());
		
			TrajectoryEvaluator evaluator = new TrajectoryEvaluator();
			evaluator.evaluate(t.getCoordinates());
			
			
			if ( evaluator.getCurrentTelemetry().maxSpeed.getKmh() > 30 ) {
				motorizedChance++;
			} else {
				nonMotorizedChance++;
			}
			
			if ( evaluator.getCurrentTelemetry().avgSpeed.getKmh() > 20 ) {
				motorizedChance++;
			} else {
				nonMotorizedChance++;
			}
			
			if ( evaluator.getCurrentTelemetry().maxAcc.getMPerSec2() > 1 ) {
				motorizedChance++;
			} else {
				nonMotorizedChance++;
			}
			
			if (motorizedChance > nonMotorizedChance) {
				trajectories.put(t, TransportType.MOTORIZED);
			} else {
				trajectories.put(t, TransportType.NON_MOTORIZED);
			}
			
		}
		
		return trajectories;
	}
	*/
	
	public List<Trajectory> subtrajectoriesByTime(Trajectory trajectory, long timeToleranceMilis) {
		Trajectory subtrajectory = new Trajectory();
		List<Trajectory> subtrajectories = new LinkedList<>();
		subtrajectories.add(subtrajectory);
		
		Coordinate previousCoord = null;
		for (Coordinate currentCoord : trajectory.getCoordinates()) {
			if(previousCoord == null) {
				previousCoord = currentCoord;
				subtrajectory.add(currentCoord);
				continue;
			}
			
			if (currentCoord.getDateTime().equals(LocalDateTime.of(2015, 11, 11, 10, 40,26)) ) {
				System.out.println(currentCoord);
				System.out.println(previousCoord);
			}
			long difference = currentCoord.getDateTimeInMillis() - previousCoord.getDateTimeInMillis();
			if (difference <= timeToleranceMilis) {
				subtrajectory.add(currentCoord);
			} else {
				subtrajectory = new Trajectory();
				subtrajectory.add(currentCoord);
				subtrajectories.add(subtrajectory);
			}
			previousCoord = currentCoord;
		}
		return subtrajectories;
	}
	
	public String getStartDate() {
		return trajectory.getStart().map(o -> o.toString()).orElse("");
	}
	
	public String getEndDate() {
		return trajectory.getEnd().map(o -> o.toString()).orElse("");
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public double getAggressiveIndex() {
		double speedIndex = avgIndex(trajectorySpeedIndexes);
		double accIndex = avgIndex(trajectoryAccelerationIndexes);
		//double decIndex = avgIndex(trajectoryDeccelerationIndexes);
		//return (speedIndex + accIndex + decIndex)/3;
		return (speedIndex + accIndex)/2;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}
	
	
}

class AccInterval {
	private Coordinate start;
	private Coordinate end;
	private double speedAvg;
	private double distance;
	private long time;
	
	public AccInterval(Coordinate accelerationStart, Coordinate accelerationEnd) {
		start = accelerationStart;
		end = accelerationEnd;
	}

	public Coordinate getStart() {
		return start;
	}

	public void setStart(Coordinate start) {
		this.start = start;
	}

	public Coordinate getEnd() {
		return end;
	}

	public void setEnd(Coordinate end) {
		this.end = end;
	}

	public double getSpeedAvg() {
		return speedAvg;
	}

	public void setSpeedAvg(double speedAvg) {
		this.speedAvg = speedAvg;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	

}
