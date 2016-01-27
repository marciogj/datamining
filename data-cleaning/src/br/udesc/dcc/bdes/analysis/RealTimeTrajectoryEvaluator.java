package br.udesc.dcc.bdes.analysis;

import java.util.Collection;
import java.util.LinkedList;

import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;


/**
 * 
 * 
 * Speed will always return m/s (meter per second)
 * 
 * @author marciogj
 *
 */
public class RealTimeTrajectoryEvaluator {
	//private final EvaluatedTrajectory evaluated = new EvaluatedTrajectory();
	private final Trajectory trajectory = new Trajectory();
	private Coordinate previousCoordinate = null;
	
	private double MAX_ALLOWED_SPEED = 13.89; //50 km/h
	private double MAX_ACCELERATION = 6.95; //25 km/h
	private double MAX_DECELERATION = -4.17; //15 km/h
	
	private double totalDistance;
	private long totalTime;
	private double speedSum;
	private double maxSpeed;
	private double maxAccecelration;
	private double maxDeceleration;
	private long accecelerationCount;
	private long decelerationCount;
	private Collection<Acceleration> accelerations = new LinkedList<>();
	
	private int overMaxSpeedCount;
	private int overMaxAccelerationCount;
	private int overMaxDecelerationCount;
	
	private Coordinate accelerationStart = null;
	private double accDistance;
	private long accTime;
	private double accSpeedSum;
	private int accCoordinateCount;
	
	public RealTimeTrajectoryEvaluator() {}
			
	public RealTimeTrajectoryEvaluator(double maxAllowedSpeed, double maxAcceleration, double maxDeceleration) {
		super();
		MAX_ALLOWED_SPEED = maxAllowedSpeed;
		MAX_ACCELERATION = maxAcceleration;
		MAX_DECELERATION = maxDeceleration;
	}

	public void evaluate(final Coordinate coordinate) {
		trajectory.add(coordinate);
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
		
		if (currentSpeed > MAX_ALLOWED_SPEED) {
			overMaxSpeedCount++;
		}
		if (currentAcceleration > MAX_ACCELERATION) {
			overMaxAccelerationCount++;
		}
		if (currentAcceleration > MAX_DECELERATION) {
			overMaxDecelerationCount++;
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
			Acceleration acceleration = new Acceleration(accelerationStart, currentCoordinate);
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
			if (currentAcceleration >= 0) {
				accecelerationCount++;
			} else {
				decelerationCount++;
			}
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
	
	public double getAvgSpeed() {
		return speedSum/trajectory.size();
	}
	
	public double getCoordinateRate() {
		return trajectory.size()/totalTime;
	}

	public Trajectory getTrajectory() {
		return trajectory;
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public double getMaxAccecelration() {
		return maxAccecelration;
	}

	public double getMaxDeceleration() {
		return maxDeceleration;
	}

	public long getAccecelerationCount() {
		return accecelerationCount;
	}

	public long getDecelerationCount() {
		return decelerationCount;
	}

	public Collection<Acceleration> getAccelerations() {
		return accelerations;
	}

	public int getOverMaxSpeedCount() {
		return overMaxSpeedCount;
	}

	public int getOverMaxAccelerationCount() {
		return overMaxAccelerationCount;
	}

	public int getOverMaxDecelerationCount() {
		return overMaxDecelerationCount;
	}
}

class Acceleration {
	private Coordinate start;
	private Coordinate end;
	private double speedAvg;
	private double distance;
	private long time;
	
	public Acceleration(Coordinate accelerationStart, Coordinate accelerationEnd) {
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
