import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.udesc.dcc.bdes.analysis.AccelerationEvaluator;
import br.udesc.dcc.bdes.analysis.AccelerationLimit;
import br.udesc.dcc.bdes.model.Speed;
import br.udesc.dcc.bdes.model.SpeedIndexEval;


public class MainIndexEval {

	public static void main(String[] args) {
		System.out.println("======= PRESS START ========\n\n");
		
		
		//System.out.println("=== Little Aggresive ===");
		//MyTrajectory littleAggressive = littleAggressiveProfile(speedLimit);
		//MyEval littleAggressiveEval = eval(littleAggressive, speedLimit);
		//print(littleAggressiveEval);
		//littleAggressiveEval.speedDist.print();
		
		aggressiveConstant();
		
		
		
		System.out.println("\n\n=======GAME OVER========");
	}
	
	public static void aggressiveConstant() {
		Speed speedLimit = Speed.fromKmh(50);
		System.out.println("=== No Aggresive ===");
		MyTrajectory noAggressive = createConstant(2001, Speed.fromKmh(61).getMs());
		MyEval noAggressiveEval = eval(noAggressive, speedLimit.getMs());
		
		print(noAggressiveEval);
		
		System.out.println("Speed Weight Eval " + noAggressiveEval.speedDist.getWeightEval());
		printAccWeightIndex(noAggressiveEval.accEval);
		
		
		print(noAggressiveEval.accEval);
		noAggressiveEval.speedDist.print();
	}
	
	public static void litleAggressiveConstant() {
		Speed speedLimit = Speed.fromKmh(50);
		System.out.println("=== No Aggresive ===");
		MyTrajectory noAggressive = createConstant(2001, Speed.fromKmh(56).getMs());
		MyEval noAggressiveEval = eval(noAggressive, speedLimit.getMs());
		
		print(noAggressiveEval);
		
		System.out.println("Speed Weight Eval " + noAggressiveEval.speedDist.getWeightEval());
		printAccWeightIndex(noAggressiveEval.accEval);
		
		
		print(noAggressiveEval.accEval);
		noAggressiveEval.speedDist.print();
	}
	
	
	public static void noAggressiveConstant() {
		Speed speedLimit = Speed.fromKmh(50);
		System.out.println("=== No Aggresive ===");
		MyTrajectory noAggressive = createConstant(2001, Speed.fromKmh(45).getMs());
		MyEval noAggressiveEval = eval(noAggressive, speedLimit.getMs());
		
		print(noAggressiveEval);
		
		System.out.println("Speed Weight Eval " + noAggressiveEval.speedDist.getWeightEval());
		printAccWeightIndex(noAggressiveEval.accEval);
		
		
		print(noAggressiveEval.accEval);
		noAggressiveEval.speedDist.print();
	}
	
	
	public static MyTrajectory safeProfile() {
		return new MyTrajectory();
	}
	
	public static  MyTrajectory slowDangerousProfile() {
		return new MyTrajectory();
	}
	
	public static  MyTrajectory littleAggressiveProfile(double speedLimit) {
		return createTrajectory(2001, speedLimit-1, speedLimit, Profile.LITTLE_AGGRESSIVE);
	}
	
	public static  MyTrajectory aggressiveProfile() {
		return new MyTrajectory();
	}
	
	public static MyTrajectory dangerousAggressiveProfile() {
		return new MyTrajectory();
	}
	
	public static MyEval eval(MyTrajectory t, double speedLimit) {
		MyEval eval = new MyEval(speedLimit);
		MyCoord prevCoord = null;
		SpeedIndexEval speedEval = new SpeedIndexEval(speedLimit);
	
		double laneExchanges = 0;
		double avgSpeed = 0;
		double avgAcc = 0;
		
		double segmentLaneExchanges = 0;
		double segmentDistance = 0;
		double segmentSpeedIndexSum = 0;
		double segmentAccIndexSum = 0;
		int segmentCoords = 0;
		int coordCount = 0;
		for(MyCoord currentCoord : t.coords) {
			//System.out.println("#" + coordCount++ + "coord(" + new Speed(currentCoord.speed).getKmh() + " - Distance: " + currentCoord.distance);
			avgSpeed += currentCoord.speed;
			if (prevCoord == null) {
				prevCoord = currentCoord;
				continue;
			}
			double currentAcc = (currentCoord.speed - prevCoord.speed) / (currentCoord.time - prevCoord.time);
			avgAcc += Math.abs(currentAcc);
			
			double currentSpeed = currentCoord.speed;
			eval.speedDist.countSpeed(currentSpeed);
			
			
			double speedIndex = speedEval.evaluate(currentSpeed);
			double accIndex = eval.accEval.evaluate(currentAcc);
			eval.accEval.count(currentAcc);
			
			laneExchanges += currentCoord.laneExchange && currentCoord.speed > speedLimit ? 1 : 0;
			if (!currentCoord.laneExchange && laneExchanges != 0) {
				segmentLaneExchanges += laneExchanges >= 5 ? 1 : 0;
				laneExchanges = 0;
			}
			
			segmentCoords += 1;
			segmentDistance += currentCoord.distance - prevCoord.distance;
			
			segmentSpeedIndexSum += speedIndex;
			segmentAccIndexSum += accIndex;
			if (segmentDistance > 1000) {
				double segmentSpeedIndex = segmentSpeedIndexSum/segmentCoords;
				double segmentAccIndex = segmentAccIndexSum/segmentCoords;
				double currentSegmentIndex = max(segmentSpeedIndex, segmentAccIndex);
				
				for(int i=0; i < segmentLaneExchanges; i++) {
					currentSegmentIndex += 5; //TODO: Avaliar o quanto a velocidade estava acima para aumentar essa soma
				}
				
				eval.segmentAccIndexes.add(segmentAccIndex);
				eval.segmentSpeedIndexes.add(segmentSpeedIndex);
				eval.segmentIndex.add(currentSegmentIndex);
								
				segmentCoords = 0;
				segmentDistance = 0;
				segmentSpeedIndexSum = 0;
				segmentAccIndexSum = 0;
				segmentLaneExchanges = 0;
			}
			prevCoord = currentCoord;
		}
		
		double segmentSum = 0;
		int count = 0;
		for(double value : eval.segmentAccIndexes) {
			segmentSum += value;
			count++;
		}
		eval.accIndex = segmentSum/count;
		
		segmentSum = 0;
		count = 0;
		for(double value : eval.segmentSpeedIndexes) {
			segmentSum += value;
			count++;
		}
		eval.speedIndex = segmentSum/count;
		eval.trajectoryIndex = max(eval.accIndex, eval.speedIndex);
		eval.avgSpeed = avgSpeed / t.coords.size();
		eval.avgAcc = avgAcc / (t.coords.size() - 1);
		return eval;
	}
	
	public static void print(MyEval eval) {
		System.out.println("Speed Index: " + eval.speedIndex);
		System.out.println("Acc Index: " + eval.accIndex);
		System.out.println("Overall: " + eval.trajectoryIndex);
		
		System.out.println("Avg Speed: " + new Speed(eval.avgSpeed).getKmh());
		System.out.println("Avg Acc: " + eval.avgAcc + " m/s²\n");
	}
	
	public static void print(AccelerationEvaluator accEval) {
		List<AccelerationLimit> limts = accEval.getAccEval();
		for (AccelerationLimit accLimit : limts) {
			System.out.println(accLimit.getDescription() + ": " + accLimit.getCount());
		}
		System.out.println();
	}
	
	public static void printAccWeightIndex(AccelerationEvaluator accEval) {
		int total = 0;
		List<AccelerationLimit> limts = accEval.getAccEval();
		for (AccelerationLimit accLimit : limts) {
			total += accLimit.getCount();
		}
		
		double decVerySecureProp =  proportion(limts.get(0).getCount(), total);
		double decSecureProp =  proportion(limts.get(1).getCount(), total);
		double decDangerousProp =  proportion(limts.get(2).getCount(), total);
		double decVeryDangerousProp =  proportion(limts.get(3).getCount(), total);
		
		double accVerySecureProp =  proportion(limts.get(4).getCount(), total);
		double accSecureProp =  proportion(limts.get(5).getCount(), total);
		double accDangerousProp =  proportion(limts.get(6).getCount(), total);
		double accVeryDangerousProp =  proportion(limts.get(7).getCount(), total);
		
		double accIndex = (decDangerousProp * 2.5) + (accDangerousProp * 1.5) + (decVeryDangerousProp * 5) + (accVeryDangerousProp * 3);
		System.out.println("Weight Acc index: " + accIndex + "\n");
		
	}
	
	public static double proportion(double value, double max) {
		return (value*100)/max ;
	}
	
	
	public static double max(double v1, double v2) {
		if (v1 > v2) return v1;
		return v2;
	}
	
	public static MyTrajectory createTrajectory(int coordinates, double speedAvg, double speedLimit, Profile profile) {
		MyTrajectory t = new MyTrajectory();
		long time = 0;
		double distanceMeters = 1;
		boolean laneExchange = false;
		double currentSpeed = speedAvg;
		for(int i=0; i < coordinates; i++) {
			currentSpeed = speedChange(currentSpeed - 5.0, currentSpeed + 5.0);
			if (profile == Profile.LITTLE_AGGRESSIVE && currentSpeed < 45.0/3.6) {
				currentSpeed = 45/3.6;
			}
			t.coords.add(new MyCoord(currentSpeed, distanceMeters, time, laneExchange));
			time++;
			distanceMeters++;
		}
		return t;
	}
	
	public static MyTrajectory createConstant(int coordinates, double speed) {
		MyTrajectory t = new MyTrajectory();
		long time = 0;
		double distanceMeters = speed;
		boolean laneExchange = false;
		for(int i=0; i < coordinates; i++) {
			t.coords.add(new MyCoord(speed, distanceMeters, time, laneExchange));
			time++;
			distanceMeters++;
		}
		return t;
	}

	private static double speedChange(double min, double max) {
		Random r = new Random();
		return Math.abs(r.nextInt((int) (max - min)) + min);
	}
	
}

enum Profile {
	SAFE,
	SLOW_DANGEROUS,
	LITTLE_AGGRESSIVE,
	AGGRESSIVE,
	VERY_AGGRESSIVE
}

class MyTrajectory {
	List<MyCoord> coords = new ArrayList<>();
	
	public MyTrajectory(){}
}

class MyCoord {
	double speed;
	double distance;
	double time;
	boolean laneExchange;
	
	public MyCoord(double speed, double distance, double time, boolean laneExchange) {
		super();
		this.speed = speed;
		this.distance = distance;
		this.time = time;
		this.laneExchange = laneExchange;
	}
}

class MyEval {
	double avgSpeed;
	double avgAcc;
	List<Double> segmentSpeedIndexes = new ArrayList<>();
	List<Double> segmentAccIndexes = new ArrayList<>();
	List<Double> segmentIndex = new ArrayList<>(); 
	double speedIndex;
	double accIndex;
	double trajectoryIndex;
	SpeedDist speedDist;
	AccelerationEvaluator accEval = new AccelerationEvaluator();
	
	MyEval(double speedLimitMs) {
		this. speedDist = new SpeedDist(speedLimitMs);
	}
	
}

class SpeedDist {
	int under50Limit;
	int underLimit;
	int fromLimitTo10;
	int from10to20;
	int from20to50;
	int over50;
	double maxSpeed;
	
	SpeedDist(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	public void countSpeed(double speedMs) {
		double percentage = eval(speedMs);
		count(percentage);
	}
	
	private void count(double limitPercentage) {
		//if (limitPercentage > -50 && limitPercentage < 0) {
		//	under50Limit++;
		//} else 
		if (limitPercentage <= 0) {
			underLimit++;
		} else if (limitPercentage > 0 && limitPercentage < 10) {
			fromLimitTo10++;
		} else if (limitPercentage >= 10 && limitPercentage < 20) {
			from10to20++;
		} else if (limitPercentage >= 20 && limitPercentage < 50) {
			from20to50++;
		} else if (limitPercentage >= 50) {
			fromLimitTo10++;
		}
	}
	
	private double eval(double speedMs) {
		return ( (speedMs*100)/maxSpeed ) - 100;
	}
	
	private double proportion(double value, double max) {
		return (value*100)/max ;
	}
	
	public void print() {
		System.out.println("Abaixo de 50% Limite: " + under50Limit);
		System.out.println("Dentro do Limite: " + underLimit);
		System.out.println("Até 10%: " + fromLimitTo10);
		System.out.println("De 10% a 20%: " + from10to20);
		System.out.println("De 20% a 50%: " + from20to50);
		System.out.println("Acima de 50%: " + over50);
	}
	
	public double getWeightEval() {
		
		int total = under50Limit + underLimit + fromLimitTo10 + from10to20 + from20to50 + over50;
		double underLimitProp = proportion(underLimit, total);
		double fromLimitTo10Prop = proportion(fromLimitTo10, total);
		double from10to20Prop = proportion(from10to20, total);
		double from20to50Prop = proportion(from20to50, total);
		double over50Prop = proportion(over50, total);
		
		double eval = (underLimitProp * 0) + (fromLimitTo10Prop * 0.35) +  (from10to20Prop * 0.5) + (from20to50Prop * 1.5) + (over50Prop * 2); 
		
		return eval > 100 ?  100 : eval;
	}
}
