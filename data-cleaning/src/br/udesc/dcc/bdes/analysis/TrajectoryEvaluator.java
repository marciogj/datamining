package br.udesc.dcc.bdes.analysis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.udesc.dcc.bdes.model.Acceleration;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Distance;
import br.udesc.dcc.bdes.model.Speed;
import br.udesc.dcc.bdes.model.Time;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.model.TrajectoryEvaluation;
import br.udesc.dcc.bdes.openweather.OpenWeatherConditionDTO;


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
	
	private AccelerationEvaluator accEvaluator = new AccelerationEvaluator();
	
	private Optional<OpenWeatherConditionDTO> currentWeather = Optional.empty(); 
	
	public TrajectoryEvaluator() {
		this.id = UUID.randomUUID().toString();
	}
	
	public String getId() {
		return id;
	}
			
	public TrajectoryEvaluator(double maxAllowedSpeed, double maxAcceleration, double maxDeceleration) {
		super();
		MAX_ALLOWED_SPEED = maxAllowedSpeed;
	}
	
	public void evaluate(Collection<Coordinate> coordinates, Optional<OpenWeatherConditionDTO> weather) {
		for (Coordinate coordinate : coordinates) {
			evaluate(coordinate, weather);
		}
	}
	
	public void evaluate(Collection<Coordinate> coordinates) {
		for (Coordinate coordinate : coordinates) {
			evaluate(coordinate, Optional.empty());
		}
	}

	public void evaluate(final Coordinate coordinate) {
		evaluate(coordinate, Optional.empty());
	}
	
	public AccelerationEvaluator getAccEvaluator() {
		return accEvaluator;
	}
	
	public void evaluate(final Coordinate coordinate, Optional<OpenWeatherConditionDTO> weather) {
		trajectory.add(coordinate);
		currentWeather = weather;
		
		accCoordinateCount++;
		if (previousCoordinate == null) {
			previousCoordinate = coordinate;
			accelerationStart = coordinate;
			return;
		}
		
		Coordinate currentCoordinate = this.updateMomentum(previousCoordinate, coordinate);
		double distanceFromPrevious = coordinate.distanceInMeters(previousCoordinate);
		double elapsedTime = (currentCoordinate.getDateTimeInMillis() - previousCoordinate.getDateTimeInMillis())/1000;
		double currentSpeed = currentCoordinate.getSpeed();
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
	}
			
	private Coordinate updateMomentum(final Coordinate previousCoordinate, final Coordinate currentCoordinate) {
		final long coordinateTime = currentCoordinate.getDateTimeInMillis()/1000;
		final long previousCoordinateTime = previousCoordinate.getDateTimeInMillis()/1000;
		final double deltaTSeconds = coordinateTime - previousCoordinateTime;
		
		final double distance = currentCoordinate.distanceInMeters(previousCoordinate);
		final double speed = deltaTSeconds != 0 ?  distance/deltaTSeconds : distance;
		
		final double deltaV = speed - (previousCoordinate.getSpeed());
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
