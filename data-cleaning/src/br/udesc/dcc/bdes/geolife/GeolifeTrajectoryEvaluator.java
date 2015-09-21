package br.udesc.dcc.bdes.geolife;

import java.util.ArrayList;
import java.util.List;

public class GeolifeTrajectoryEvaluator {
	
	public static GeolifeTrajectorySummary evaluate(GeolifeTrajectory trajectory, long maxAcceptedSpeed, long gpsErrorStopDetection ) {
		GeolifeCoordinate previousCoordinate = null;
		double distanceSum = 0;
		long startTime = 0;
		long endTime = 0;
		double previousSpeed = 0;
		double maxSpeed = 0;
		double maxAcceleration = 0;
		
		long allStoppedTime = 0;
		
		List<GeolifeCoordinate> stoppedCoordinates = new ArrayList<GeolifeCoordinate>();
		
		for (GeolifeCoordinate coordinate : trajectory.getCoordinates()) {
			if (previousCoordinate == null) {
				startTime = coordinate.getTimeInMillis();
				System.out.println(">> " + coordinate + " >> "+ 0 + " km/h - Acceleration: " + 0 + " km/h - Distance: " + 0 + " m");
			} else 	if (previousCoordinate != null) {
				double distance = coordinate.distanceInMeters(previousCoordinate);
				long coordinateTime = coordinate.getTimeInMillis();
				long previousCoordinateTime = previousCoordinate.getTimeInMillis();
				double secondsPassed = (coordinateTime - previousCoordinateTime)/1000.0;
				double currentSpeed = distance/secondsPassed;
				
				double deltaV = currentSpeed - previousSpeed;
				double detltaT = secondsPassed;
				double acceleration = deltaV/detltaT;
				
				System.out.println(">> " + coordinate + " >> "+ currentSpeed*3.6 + " km/h - Acceleration: " + acceleration*3.6 + " km/h - Distance: " + distance + " m");
				
				//TODO: It might be an error data or it might be a signal fault for a long period. We should improve checking:
				// * Whether next position is like currentPosition or is it like previous  
				if (currentSpeed > maxAcceptedSpeed) {
					System.out.println("Max speed limit ( " + maxAcceptedSpeed*3.6 + " km/h) reached(" + currentSpeed*3.6 + " km/h) - Ignoring coordinate: [" + coordinate + "] Previous: [" + previousCoordinate + "]");
					break;
				}
				
				//TODO: improve this naive evaluation
				double stoppedTime = 0;
				if (distance < 5) {
					boolean isStopped = true;
					for (GeolifeCoordinate stopped : stoppedCoordinates) {
						isStopped = stopped.distanceInMeters(coordinate) < 5;
					}
					if(isStopped) {
						stoppedCoordinates.add(coordinate);
						stoppedTime = (coordinateTime - stoppedCoordinates.get(0).getTimeInMillis())/1000.0;
						allStoppedTime += stoppedTime;
					}
				} else {
					stoppedCoordinates.clear();
					stoppedCoordinates.add(coordinate);
				}
				
				if (maxSpeed < currentSpeed) {
					maxSpeed = currentSpeed;
				}
				
				double currentAcceleration = (currentSpeed - previousSpeed) / secondsPassed;
				if(maxAcceleration < currentAcceleration) {
					maxAcceleration = currentAcceleration;
				}
				
				//TODO: Register constant acceleration 
				//check if its still accelerating
				//if (previousAcceleration > 0 && currentAcceleration > previousAcceleration ) {
				//	maxSumAcceleration
				//}
				distanceSum += distance;
				previousSpeed = currentSpeed;
				//previousAcceleration = currentAcceleration;
			}
			previousCoordinate = coordinate;
			
		}
		endTime = previousCoordinate.getTimeInMillis();
		
		GeolifeTrajectorySummary summary = new GeolifeTrajectorySummary(trajectory);
		summary.setTotalTimeInSeconds(endTime - startTime);
		summary.setTotalDistanceInMeters(distanceSum);
		summary.setMaxSpeedInMetersPerSecond(maxSpeed);
		summary.setMaxAccelerationInMetersPerSecond(maxAcceleration);
		summary.setTrajectory(trajectory);
		summary.setStoppedTime(allStoppedTime);
		return summary;
	}

}
