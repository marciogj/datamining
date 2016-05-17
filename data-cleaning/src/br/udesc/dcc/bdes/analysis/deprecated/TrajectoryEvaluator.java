package br.udesc.dcc.bdes.analysis.deprecated;

import java.util.Collection;

import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Trajectory;


/**
 * 
 * 
 * Speed will always return m/s (meter per second)
 * 
 * @author marciogj
 *
 */
public class TrajectoryEvaluator {
		
	public static EvaluatedTrajectory evaluate(Trajectory trajectory) {
		EvaluatedTrajectory evaluated = new EvaluatedTrajectory(trajectory);
		Collection<Coordinate> coordinates = trajectory.getCoordinates();
		if (coordinates.isEmpty()) {
			return evaluated;
		}
		
		double maxSpeed = 0, avgSpeed = 0, totalDistance = 0, maxSlowdown = 0, maxSpeedUp = 0;
		long totalTime = 0, speedUpDownOscilations = 0;
		Coordinate previous = null, first = null, accelerationStart = null;
		for (Coordinate coordinate : coordinates) {
			if (previous == null) {
				first = coordinate;
				previous = coordinate;
				accelerationStart = coordinate;
				continue;
			}
			
			updateMomentum(previous, coordinate);
			
			totalDistance += coordinate.distanceInMeters(previous);
			avgSpeed += coordinate.getSpeed();
			
			if(coordinate.getSpeed() > maxSpeed) {
				maxSpeed = coordinate.getSpeed();
				//System.out.println(coordinate);
			}
			
			if (isAccelerationInverted(coordinate.getAcceleration(), previous.getAcceleration())) {
				speedUpDownOscilations++;
				
				double acceleration = acceleration(accelerationStart, previous);
				if (acceleration > maxSpeedUp) {
					maxSpeedUp = acceleration;
				}				
				if (acceleration < maxSlowdown) {
					maxSlowdown = acceleration;
				}
				
				//acceleration increase/decrease finished on previous coordinate
				accelerationStart = coordinate;
			}
						
			previous = coordinate;
		}
		
		avgSpeed = avgSpeed / trajectory.size();
		totalTime = (previous.getDateTimeInMillis() - first.getDateTimeInMillis())/1000;
		
		//Speed
		evaluated.setMaxSpeed(maxSpeed);
		evaluated.setAvgSpeed(avgSpeed);
		
		//Acceleration
		evaluated.setMaxSlowdown(maxSlowdown);
		evaluated.setMaxSpeedUp(maxSpeedUp);
		evaluated.setSpeedUpDownOscilations(speedUpDownOscilations);
		
		evaluated.setCoordinateRate(totalTime/coordinates.size());
		evaluated.setTotalDistance(totalDistance);
		evaluated.setTotalTime(totalTime);
		
		
		return evaluated;
	}
	
	private static double acceleration(Coordinate start, Coordinate end) {
		double deltaTSeconds = (end.getDateTimeInMillis() - start.getDateTimeInMillis())/1000;
		double deltaV = end.getSpeed() - (start.getSpeed());
		double acceleration = deltaV/deltaTSeconds;
		
		return acceleration;
	}
	
	
	private static boolean isAccelerationInverted(double current, double previous) {
		if (previous > 0) {
			return current > previous; //will return true if its still speeding up
		} else {
			return current < previous;  //will return true if its still slowing down
		}
	}
		
	private static void updateMomentum(Coordinate previousCoordinate, Coordinate currentCoordinate) {
		long coordinateTime = currentCoordinate.getDateTimeInMillis()/1000;
		long previousCoordinateTime = previousCoordinate.getDateTimeInMillis()/1000;
		double deltaTSeconds = coordinateTime - previousCoordinateTime;
		
		double distance = currentCoordinate.distanceInMeters(previousCoordinate);
		double speed = deltaTSeconds != 0 ?  distance/deltaTSeconds : distance;
		
		double deltaV = speed - (previousCoordinate.getSpeed());
		double acceleration = deltaV/deltaTSeconds;
		
		currentCoordinate.setAcceleration(acceleration);
		currentCoordinate.setSpeed(speed);
	}
	
}
