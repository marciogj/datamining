package br.udesc.dcc.bdes.analysis;

import java.util.LinkedList;
import java.util.List;

import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Trajectory;

public class MeanTransportSplitter {
	public static final double WALKING_SPEED_MS = 10/3.6; 
	public static final int TRAJECTORY_SEGMENT_SIZE = 20;
	
	public static final int WALKING_TRESHOLD = 15;
	public static final int MOTOR_TRESHOLD = 3;
	
	
	public static List<Trajectory> subBySpeedAvg(Trajectory trajectory) {
		List<Trajectory> subTrajectories = new LinkedList<>();
		List<Coordinate> walkingCoords = new LinkedList<>();
		List<Coordinate> motorizedCoords = new LinkedList<>();
		List<Coordinate> tmpCoords = new LinkedList<>();
		double speedSum = 0;
		int count = 0;
		
		Coordinate previous = null;
		for(Coordinate coord : trajectory.getCoordinates()) {
			count++;
			tmpCoords.add(coord);
			if (previous == null) {
				previous = coord;
				continue;
			}
			double currentSpeed = coord.getSpeed().isPresent() ? coord.getSpeed().get() : coord.speedFrom(previous);
			speedSum += currentSpeed;
			
			if (count == TRAJECTORY_SEGMENT_SIZE) {
				double speedAvg = speedSum/count;
				boolean isWalking = speedAvg <= WALKING_SPEED_MS;

				if(isWalking) {
					walkingCoords.addAll(tmpCoords);
				} else {
					motorizedCoords.addAll(tmpCoords);
				}
				tmpCoords.clear();
				speedSum = 0;
				count = 0;
			}
			previous = coord;
		}

		Trajectory motorizedTrajectory = Trajectory.sub(trajectory);
		motorizedTrajectory.addAll(motorizedCoords);
		motorizedTrajectory.setTransportMean(TransportType.MOTORIZED.name());

		Trajectory walkTrajectory = Trajectory.sub(trajectory);
		walkTrajectory.addAll(walkingCoords);
		walkTrajectory.setTransportMean(TransportType.NON_MOTORIZED.name());

		if (!motorizedTrajectory.isEmpty()) {
			subTrajectories.add(motorizedTrajectory);
		}

		if (!walkTrajectory.isEmpty()) {
			subTrajectories.add(walkTrajectory);
		}

		return subTrajectories;
	}

	
	public static List<Trajectory> subBySpeed(Trajectory trajectory) {
		List<Trajectory> subTrajectories = new LinkedList<>();
		List<Coordinate> walkingCoords = new LinkedList<>();
		List<Coordinate> motorizedCoords = new LinkedList<>();
		List<Coordinate> tmpCoords = new LinkedList<>();
		double currentSpeed = 0;
		
		boolean isWalkTreshold = false;
		boolean isMotorTreshold = false;
		
		TransportType previousGuess = null;
		
		Coordinate previous = null;
		for(Coordinate coord : trajectory.getCoordinates()) {
			tmpCoords.add(coord);
			currentSpeed = coord.getSpeed().isPresent() ? coord.getSpeed().get() : previous == null ? 0 : coord.speedFrom(previous);
			boolean isWalking = currentSpeed <= WALKING_SPEED_MS;

			if (isWalking) {
				walkingCoords.add(coord);
			} else {
				motorizedCoords.add(coord);
			}
			
			if (previous == null) {
				previous = coord;
				continue;
			}
			isWalkTreshold = walkingCoords.size() >= WALKING_TRESHOLD;
			isMotorTreshold = motorizedCoords.size() >= MOTOR_TRESHOLD;
			
			if (isWalkTreshold && previousGuess == null) {
				previousGuess = TransportType.NON_MOTORIZED;
			}
			
			if (isMotorTreshold && previousGuess == null) {
				previousGuess = TransportType.MOTORIZED;
			}
			
			//Se não está caminhando e não tem indicação de caminhada, limpa lista de walking
			//Para não perder coordenadas, adiciona todas como motorizadas
			if (!isWalking && !isWalkTreshold) {
				walkingCoords.clear();
				motorizedCoords.clear();
				motorizedCoords.addAll(tmpCoords);
			}
			
			//Atingiu indicação de caminhada, mas havia uma trajetória motorizada antes
			if (isWalkTreshold && isMotorTreshold && previousGuess == TransportType.MOTORIZED) {
				Trajectory motorized = new Trajectory();
				motorized.addAll(motorizedCoords);
				motorized.setTransportType(TransportType.MOTORIZED);
				subTrajectories.add(motorized);
				
				motorizedCoords.clear();
				tmpCoords.clear();
				tmpCoords.addAll(walkingCoords);
				previousGuess = TransportType.NON_MOTORIZED;
			}
			
			//Atingiu indicação de motorizado, mas havia uma trajetória caminhada antes
			if (isMotorTreshold && isWalkTreshold && previousGuess == TransportType.NON_MOTORIZED) {
				Trajectory walk = new Trajectory();
				walk.addAll(walkingCoords);
				walk.setTransportType(TransportType.NON_MOTORIZED);
				subTrajectories.add(walk);
				
				walkingCoords.clear();
				tmpCoords.clear();
				tmpCoords.addAll(motorizedCoords);
				previousGuess = TransportType.MOTORIZED;
			}
						
			previous = coord;
		}

		if(isMotorTreshold || motorizedCoords.size() > walkingCoords.size()){
			Trajectory motor = new Trajectory();
			motor.addAll(tmpCoords);
			motor.setTransportType(TransportType.MOTORIZED);
			subTrajectories.add(motor);
		} else {
			Trajectory walk = new Trajectory();
			walk.addAll(tmpCoords);
			walk.setTransportType(TransportType.NON_MOTORIZED);
			subTrajectories.add(walk);
		}
		
		return subTrajectories;
	}

}