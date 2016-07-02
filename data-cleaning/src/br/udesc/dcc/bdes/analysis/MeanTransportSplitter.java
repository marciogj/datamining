package br.udesc.dcc.bdes.analysis;

import java.util.LinkedList;
import java.util.List;

import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.model.TransportType;

public class MeanTransportSplitter {
	public static final double WALKING_SPEED_MS = 10/3.6; 
	public static final int TRAJECTORY_SEGMENT_SIZE = 20;
	
	public static final int WALKING_TIME = 3 * 60 * 1000;
	public static final int WALKING_TRESHOLD = 10;
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
		return subBySpeedNew(trajectory, WALKING_SPEED_MS, WALKING_TRESHOLD, MOTOR_TRESHOLD);
	}
	
	
	public static List<Trajectory> subBySpeedNew(Trajectory trajectory, double walkingSpeedMs, int walkingThreshold, int motorThreshold) {
		List<Trajectory> subTrajectories = new LinkedList<>();
		List<Coordinate> walkingCoords = new LinkedList<>();
		List<Coordinate> motorizedCoords = new LinkedList<>();
		List<Coordinate> tmpCoords = new LinkedList<>();
		double currentSpeed = 0;
		boolean isWalkTreshold = false;
		boolean isMotorTreshold = false;
		TransportType previousGuess = null;
		
		Coordinate previous = null;
		Trajectory walk = null;
		Trajectory motorized = null;
		for(Coordinate coord : trajectory.getCoordinates()) {
			tmpCoords.add(coord);
			currentSpeed = coord.getSpeed().isPresent() ? coord.getSpeed().get() : previous == null ? 0 : coord.speedFrom(previous);
			boolean isWalking = currentSpeed <= walkingSpeedMs;

			if (isWalking) {
				walkingCoords.add(coord);
			} else {
				motorizedCoords.add(coord);
			}
			
			if (previous == null) {
				previous = coord;
				continue;
			}
			
			boolean isReallyWalking = false;
			if (walkingCoords.size() > 1) {
				Coordinate first = walkingCoords.get(0);
				Coordinate last = walkingCoords.get(walkingCoords.size()-1);
				long timeDiff = last.getDateTimeInMillis() - first.getDateTimeInMillis();
				isReallyWalking = timeDiff >= WALKING_TIME;
			}
			
			isWalkTreshold = (walkingCoords.size() >= walkingThreshold) && isReallyWalking;
			isMotorTreshold = motorizedCoords.size() >= motorThreshold;
			
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
				motorized = Trajectory.sub(trajectory);
				motorizedCoords.forEach( c -> c.setTransportType(TransportType.MOTORIZED));
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
				
				//se a caminhada anterior não tinha 3 min pelo menos, era uma parada em um sinal ou algo semelhamte
				Coordinate first = walkingCoords.get(0);
				Coordinate last = walkingCoords.get(walkingCoords.size()-1);
				long timeDiff = last.getDateTimeInMillis() - first.getDateTimeInMillis();
				//if (timeDiff >= WALKING_TIME) {
					walk = Trajectory.sub(trajectory);
					walkingCoords.forEach( c -> c.setTransportType(TransportType.NON_MOTORIZED));
					walk.addAll(walkingCoords);
					walk.setTransportType(TransportType.NON_MOTORIZED);
					subTrajectories.add(walk);
					
					walkingCoords.clear();
					tmpCoords.clear();
					tmpCoords.addAll(motorizedCoords);
					previousGuess = TransportType.MOTORIZED;
				/*} else {
					//descarta a caminhada
					tmpCoords.addAll(walkingCoords);
					motorizedCoords.addAll(walkingCoords);
					
					walkingCoords.clear();
					previousGuess = TransportType.MOTORIZED;
				}*/
				
			}
						
			previous = coord;
		}

		if(isMotorTreshold || motorizedCoords.size() > walkingCoords.size()){
			boolean isNew =  motorized == null;
			motorized = isNew ? Trajectory.sub(trajectory) : motorized;
			tmpCoords.forEach( coord -> coord.setTransportType(TransportType.MOTORIZED));
			motorized.addAll(tmpCoords);	
			motorized.setTransportType(TransportType.MOTORIZED);
			if (isNew) {
				subTrajectories.add(motorized);
			}
		} else {
			boolean isNew = walk == null;
			walk = isNew ? Trajectory.sub(trajectory) : walk;
			tmpCoords.forEach( coord -> coord.setTransportType(TransportType.NON_MOTORIZED));
			walk.addAll(tmpCoords);
			walk.setTransportType(TransportType.NON_MOTORIZED);
			if (isNew) {
				subTrajectories.add(walk);
			}
		}
		
		return subTrajectories;
	}
	
	
	public static List<Trajectory> subBySpeed(Trajectory trajectory, double walkingSpeedMs, int walkingThreshold, int motorThreshold) {
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
			boolean isWalking = currentSpeed <= walkingSpeedMs;

			if (isWalking) {
				walkingCoords.add(coord);
			} else {
				motorizedCoords.add(coord);
			}
			
			if (previous == null) {
				previous = coord;
				continue;
			}
			isWalkTreshold = walkingCoords.size() >= walkingThreshold;
			isMotorTreshold = motorizedCoords.size() >= motorThreshold;
			
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
				Trajectory motorized = Trajectory.sub(trajectory);
				motorizedCoords.forEach( c -> c.setTransportType(TransportType.MOTORIZED));
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
				Trajectory walk = Trajectory.sub(trajectory);
				walkingCoords.forEach( c -> c.setTransportType(TransportType.NON_MOTORIZED));
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
			Trajectory motor = Trajectory.sub(trajectory);
			tmpCoords.forEach( coord -> coord.setTransportType(TransportType.MOTORIZED));
			motor.addAll(tmpCoords);	
			motor.setTransportType(TransportType.MOTORIZED);
			subTrajectories.add(motor);
		} else {
			Trajectory walk = Trajectory.sub(trajectory);
			tmpCoords.forEach( coord -> coord.setTransportType(TransportType.NON_MOTORIZED));
			walk.addAll(tmpCoords);
			walk.setTransportType(TransportType.NON_MOTORIZED);
			subTrajectories.add(walk);
		}
		
		return subTrajectories;
	}

}