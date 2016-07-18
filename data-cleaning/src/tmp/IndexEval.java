package tmp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import br.udesc.dcc.bdes.model.Speed;
import br.udesc.dcc.bdes.model.SpeedIndexEval;


public class IndexEval {

	public static void main(String[] args) {
		System.out.println("======= Trajectory Index Evaluation ========\n\n");
		//aggressiveConstant110And50onLimit50();
		//aggressiveConstant110onLimit50();
		//littleAggressiveConstant65OnLimit50();
		
		createEvaluation();
		
		System.out.println("\n\n=======GAME OVER========");
	}
	
	public static void createEvaluation() {
		System.out.println("=== createEvaluation ===");
		int speedLimit = 50;
		int initialSpeed = 50;
		int finalSpeed = 90;
		
		//100%
		blah(speedLimit, initialSpeed, finalSpeed, 100);
		System.out.println();
		//50%
		blah(speedLimit, initialSpeed, finalSpeed, 50);
		
		System.out.println();
		//25%
		blah(speedLimit, initialSpeed, finalSpeed, 25);
	}
	
	/*
	55km/h & 25\% & 2.50
	60km/h & 25\% & 10.00
	65km/h & 25\% & 15.00
	70km/h & 25\% & 20.00
	75km/h & 25\% & 25.00
	80km/h & 25\% & 75.00
	85km/h & 25\% & 87.50
	90km/h & 25\% & 100.00
	*/
	
	public static void blah(int speedLimitKmh, int initialSpeedKmh, int finalSpeedkmh, int proporcao) {
		Speed speedLimit = Speed.fromKmh(speedLimitKmh);
		System.out.println("Velocidade (km/h) & \\% do limite (50 km/h) & \\% Trajetória & IVA");
		for (int i=55; i <= 90; i+= 5) {
			double percentageOverLimit = ((i*100.0)/speedLimitKmh) - 100; 
			MyTrajectory trajectory = new MyTrajectory();
			trajectory.coords.addAll(createConstantCoords(1001, Speed.fromKmh(i).getMs()));
			int x = (100 / proporcao) - 1;
			if ( x >= 1) {
				trajectory.coords.addAll(createConstantCoords(x*1000, Speed.fromKmh(speedLimitKmh-5).getMs()));
			}
			MyEval eval = eval(trajectory, speedLimit.getMs());
			Locale.setDefault(Locale.US);
			String iva = String.format("%.2f",eval.speedDist.getWeightEval2());
			System.out.println(i + " &  " + percentageOverLimit + "\\% & " + proporcao + "\\% & " + iva);
			//System.out.println("Acc Weight Eval2: " + String.format("%.2f",eval.accDist.getWeightEval2()));
		}
	}
	
	
	public static void constant55OnLimit50() {
		System.out.println("=== constant55OnLimit50 ===");
		Speed speedLimit = Speed.fromKmh(50);
		MyTrajectory trajectory = new MyTrajectory();
		trajectory.coords.addAll(createConstantCoords(1001, Speed.fromKmh(55).getMs()));
		MyEval eval = eval(trajectory, speedLimit.getMs());
		
		EvalPrinter.print(eval);
		Locale.setDefault(Locale.US);
		System.out.println("Speed Weight Eval " + String.format("%.2f", eval.speedDist.getWeightEval()));
		System.out.println("Speed Weight Eval2: " + String.format("%.2f",eval.speedDist.getWeightEval2()));
		System.out.println("Acc Weight Eval " + String.format("%.2f",eval.accDist.getWeightEval()));
		System.out.println("Acc Weight Eval2: " + String.format("%.2f",eval.accDist.getWeightEval2()));
		System.out.println("");
		//EvalPrinter.print(eval.accEval);
		EvalPrinter.print(eval.speedDist);
		EvalPrinter.print(eval.accDist);
	}
	
	public static void aggressiveOnLimit50() {
		System.out.println("=== aggressiveConstant110And50onLimit50 ===");
		Speed speedLimit = Speed.fromKmh(50);
		MyTrajectory trajectory = new MyTrajectory();
		
		trajectory.coords.add(new MyCoord(15, 1, 1, false));
		trajectory.coords.add(new MyCoord(20, 2, 2, false));
		trajectory.coords.add(new MyCoord(25, 3, 3, false));
		trajectory.coords.add(new MyCoord(35, 4, 4, false));
		trajectory.coords.add(new MyCoord(35, 5, 5, false));
		trajectory.coords.add(new MyCoord(20, 6, 6, false));
		trajectory.coords.add(new MyCoord(15, 7, 7, false));
		trajectory.coords.add(new MyCoord(12, 8, 8, false));
		trajectory.coords.add(new MyCoord(13, 9, 9, false));
		trajectory.coords.add(new MyCoord(16, 10, 10, false));
		
		trajectory.coords.addAll(createConstantCoords(500, Speed.fromKmh(49).getMs()));
		trajectory.coords.addAll(createConstantCoords(100, Speed.fromKmh(55).getMs()));
		trajectory.coords.addAll(createConstantCoords(100, Speed.fromKmh(65).getMs()));
		trajectory.coords.addAll(createConstantCoords(100, Speed.fromKmh(75).getMs()));
		trajectory.coords.addAll(createConstantCoords(100, Speed.fromKmh(85).getMs()));
		trajectory.coords.addAll(createConstantCoords(100, Speed.fromKmh(95).getMs()));
		trajectory.coords.addAll(createConstantCoords(10, Speed.fromKmh(105).getMs()));
		MyEval eval = eval(trajectory, speedLimit.getMs());
		
		EvalPrinter.print(eval);
		Locale.setDefault(Locale.US);
		System.out.println("Speed Weight Eval " + String.format("%.2f", eval.speedDist.getWeightEval()));
		System.out.println("Speed Weight Eval2: " + String.format("%.2f",eval.speedDist.getWeightEval2()));
		System.out.println("Acc Weight Eval " + String.format("%.2f",eval.accDist.getWeightEval()));
		System.out.println("Acc Weight Eval2: " + String.format("%.2f",eval.accDist.getWeightEval2()));
		System.out.println("");
		//EvalPrinter.print(eval.accEval);
		EvalPrinter.print(eval.speedDist);
		EvalPrinter.print(eval.accDist);
	}
	
	
	public static void aggressiveConstant110And50onLimit50() {
		System.out.println("=== aggressiveConstant110And50onLimit50 ===");
		Speed speedLimit = Speed.fromKmh(50);
		MyTrajectory trajectory = new MyTrajectory();
		trajectory.coords.addAll(createConstantCoords(1001, Speed.fromKmh(110).getMs()));
		trajectory.coords.addAll(createConstantCoords(1000, Speed.fromKmh(49).getMs()));
		MyEval eval = eval(trajectory, speedLimit.getMs());
		
		EvalPrinter.print(eval);
		Locale.setDefault(Locale.US);
		System.out.println("Speed Weight Eval " + String.format("%.2f", eval.speedDist.getWeightEval()));
		System.out.println("Speed Weight Eval2: " + String.format("%.2f",eval.speedDist.getWeightEval2()));
		System.out.println("Acc Weight Eval " + String.format("%.2f",eval.accDist.getWeightEval()));
		System.out.println("Acc Weight Eval2: " + String.format("%.2f",eval.accDist.getWeightEval2()));
		System.out.println("");
		//EvalPrinter.print(eval.accEval);
		EvalPrinter.print(eval.speedDist);
		EvalPrinter.print(eval.accDist);
	}
	
	
	/*
	public static void littleAggressiveConstant65OnLimit50() {
		Speed speedLimit = Speed.fromKmh(50);
		System.out.println("=== Aggresive ===");
		MyTrajectory noAggressive = createConstant(2001, Speed.fromKmh(65).getMs());
		MyEval noAggressiveEval = eval(noAggressive, speedLimit.getMs());
		
		EvalPrinter.print(noAggressiveEval);
		EvalPrinter.printAccWeightIndex(noAggressiveEval.accEval);
		System.out.println("***");
		
		System.out.println("Speed Weight Eval " + noAggressiveEval.speedDist.getWeightEval());
		System.out.println("Speed Weight Eval2: " + noAggressiveEval.speedDist.getWeightEval2());

		System.out.println("Acc Weight Eval " + noAggressiveEval.accDist.getWeightEval());
		System.out.println("Acc Weight Eval2: " + noAggressiveEval.accDist.getWeightEval2());
		
		
		EvalPrinter.print(noAggressiveEval.accEval);
		EvalPrinter.print(noAggressiveEval.speedDist);
		System.out.println("");
		EvalPrinter.print(noAggressiveEval.accDist);
	}
	
	
	public static void aggressiveConstant110And50onLimit50() {
		System.out.println("=== aggressiveConstant110And50onLimit50 ===");
		Speed speedLimit = Speed.fromKmh(50);
		
		MyTrajectory trajectory = new MyTrajectory();
		trajectory.coords.addAll(createConstantCoords(1001, Speed.fromKmh(110).getMs()));
		trajectory.coords.addAll(createConstantCoords(1000, Speed.fromKmh(49).getMs()));
		
		MyEval eval = eval(trajectory, speedLimit.getMs());
		
		EvalPrinter.print(eval);
		
		System.out.println("Speed Weight Eval " + eval.speedDist.getWeightEval());
		System.out.println("Speed Weight Eval2: " + eval.speedDist.getWeightEval2());
		
		EvalPrinter.printAccWeightIndex(eval.accEval);
		
		EvalPrinter.print(eval.accEval);
		EvalPrinter.print(eval.speedDist);
	}
	
	public static void aggressiveConstant110onLimit50() {
		Speed speedLimit = Speed.fromKmh(50);
		System.out.println("=== Aggresive ===");
		MyTrajectory noAggressive = createConstant(2001, Speed.fromKmh(110).getMs());
		MyEval eval = eval(noAggressive, speedLimit.getMs());
		
		EvalPrinter.print(eval);
		
		System.out.println("Speed Weight Eval " + eval.speedDist.getWeightEval());
		System.out.println("Speed Weight Eval2: " + eval.speedDist.getWeightEval2());
		EvalPrinter.printAccWeightIndex(eval.accEval);
		
		EvalPrinter.print(eval.accEval);
		EvalPrinter.print(eval.speedDist);
	}
	

	public static void aggressiveConstant90onLimit50() {
		Speed speedLimit = Speed.fromKmh(50);
		System.out.println("=== Aggresive ===");
		MyTrajectory noAggressive = createConstant(2001, Speed.fromKmh(90).getMs());
		MyEval noAggressiveEval = eval(noAggressive, speedLimit.getMs());
		
		EvalPrinter.print(noAggressiveEval);
		
		System.out.println("Speed Weight Eval " + noAggressiveEval.speedDist.getWeightEval());
		System.out.println("Speed Weight Eval2: " + noAggressiveEval.speedDist.getWeightEval2());
		EvalPrinter.printAccWeightIndex(noAggressiveEval.accEval);
		
		EvalPrinter.print(noAggressiveEval.accEval);
		EvalPrinter.print(noAggressiveEval.speedDist);
	}

	public static void aggressiveConstant70onLimit50() {
		Speed speedLimit = Speed.fromKmh(50);
		System.out.println("=== Aggresive ===");
		MyTrajectory noAggressive = createConstant(2001, Speed.fromKmh(70).getMs());
		MyEval noAggressiveEval = eval(noAggressive, speedLimit.getMs());
		
		EvalPrinter.print(noAggressiveEval);
		
		System.out.println("Speed Weight Eval " + noAggressiveEval.speedDist.getWeightEval());
		System.out.println("Speed Weight Eval2: " + noAggressiveEval.speedDist.getWeightEval2());
		EvalPrinter.printAccWeightIndex(noAggressiveEval.accEval);
		
		EvalPrinter.print(noAggressiveEval.accEval);
		EvalPrinter.print(noAggressiveEval.speedDist);
	}
	
	
	
	public static void litleAggressiveConstant() {
		Speed speedLimit = Speed.fromKmh(50);
		System.out.println("=== No Aggresive ===");
		MyTrajectory noAggressive = createConstant(2001, Speed.fromKmh(56).getMs());
		MyEval noAggressiveEval = eval(noAggressive, speedLimit.getMs());
		
		EvalPrinter.print(noAggressiveEval);
		
		System.out.println("Speed Weight Eval " + noAggressiveEval.speedDist.getWeightEval());
		EvalPrinter.printAccWeightIndex(noAggressiveEval.accEval);
		
		
		EvalPrinter.print(noAggressiveEval.accEval);
		noAggressiveEval.speedDist.print();
	}
	
	
	public static void noAggressiveConstant() {
		Speed speedLimit = Speed.fromKmh(50);
		System.out.println("=== No Aggresive ===");
		MyTrajectory noAggressive = createConstant(2001, Speed.fromKmh(45).getMs());
		MyEval noAggressiveEval = eval(noAggressive, speedLimit.getMs());
		
		EvalPrinter.print(noAggressiveEval);
		
		System.out.println("Speed Weight Eval " + noAggressiveEval.speedDist.getWeightEval());
		EvalPrinter.printAccWeightIndex(noAggressiveEval.accEval);
		
		
		EvalPrinter.print(noAggressiveEval.accEval);
		noAggressiveEval.speedDist.print();
	}
	*/
	
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
		for(MyCoord currentCoord : t.coords) {
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
			eval.accDist.countAcc(currentAcc);
			
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
		t.coords.addAll(createConstantCoords(coordinates, speed));
		return t;
	}
	
	public static List<MyCoord> createConstantCoords(int coordinates, double speed) {
		List<MyCoord> coords = new ArrayList<MyCoord>(coordinates);
		long time = 0;
		double distanceMeters = speed;
		boolean laneExchange = false;
		for(int i=0; i < coordinates; i++) {
			coords.add(new MyCoord(speed, distanceMeters, time, laneExchange));
			time++;
			distanceMeters++;
		}
		return coords;
	}

	private static double speedChange(double min, double max) {
		Random r = new Random();
		return Math.abs(r.nextInt((int) (max - min)) + min);
	}
	
}
