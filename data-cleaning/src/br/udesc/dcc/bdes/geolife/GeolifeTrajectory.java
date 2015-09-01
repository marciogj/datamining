package br.udesc.dcc.bdes.geolife;

import java.util.ArrayList;
import java.util.List;

public class GeolifeTrajectory {
	public static int REASONABLE_MAX_CAR_SPEED = 55; // 55 m/s -> 200km/h
	public static int STOP_DETECTION = 600; //600s -> 10 min  
	List<GeolifeCoordinate> coordinates = new ArrayList<>();
	double totalDistance;
	double totalTime;
	double maxSpeed;
	
	double maxAcceleration;
	//double maxSumAcceleration;
	
	//avaliar a proporção de aceleração por pontos
	
	public GeolifeTrajectory() {}

	public void add(GeolifeCoordinate coordinate) {
		if (coordinate != null) {
			coordinates.add(coordinate);
		}
	}

	public List<GeolifeCoordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<GeolifeCoordinate> coordinates) {
		this.coordinates = coordinates;
	}
	
	private void updateMaxAndAvgValues() {
		GeolifeCoordinate previousCoordinate = null;
		double distanceSum = 0;
		long startTime = 0;
		long endTime = 0;
		double previousSpeed = 0;
		//double previousAcceleration = 0;
		for (GeolifeCoordinate coordinate : coordinates) {
			if (previousCoordinate == null) {
				startTime = coordinate.getTimeInMillis();
			} else 	if (previousCoordinate != null) {
				double distance = coordinate.distanceInMeters(previousCoordinate);
				long coordinateTime = coordinate.getTimeInMillis();
				long previousCoordinateTime = previousCoordinate.getTimeInMillis();
				double secondsPassed = (coordinateTime - previousCoordinateTime)/1000.0;
				double currentSpeed = distance/secondsPassed;
				
				if (currentSpeed > REASONABLE_MAX_CAR_SPEED) {
					System.out.println("Max speed limit reached(" + currentSpeed + ") - Ignoring coordinate " + coordinate);
					break;
				}
				
				//TODO: Break trajectories if a stop is detected
				//if (secondsPassed > STOP_DETECTION){
				//	
				//}
				
				if (maxSpeed < currentSpeed) {
					maxSpeed = currentSpeed;
					System.out.println("MaxSpeed: " + maxSpeed + " Distance: " + distance + " time " + secondsPassed + " # " + coordinate.getLatitude() + " Previous #" + previousCoordinate.getLatitude());
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
		
		this.totalTime = endTime - startTime;
		this.totalDistance = distanceSum;
	}

	public double getDistanceMeters() {
		if (totalDistance == 0) {
			updateMaxAndAvgValues();
		}
		return totalDistance;
	}

	public double getTotalTimeSeconds() {
		if (totalTime == 0) {
			updateMaxAndAvgValues();
		}
		return totalTime/1000.0;
	}

	public double getMaxSpeedInMetersPerSecond() {
		if (maxSpeed == 0) {
			updateMaxAndAvgValues();
		}
		return maxSpeed;
	}
	
	public double getAvgSpeedInMetersPerSecond() {
		if (maxSpeed == 0) {
			updateMaxAndAvgValues();
		}
		return totalDistance/totalTime;
	}
	

}
