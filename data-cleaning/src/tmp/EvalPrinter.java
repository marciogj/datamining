package tmp;

import java.util.List;

import br.udesc.dcc.bdes.analysis.AccelerationEvaluator;
import br.udesc.dcc.bdes.analysis.AccelerationLimit;
import br.udesc.dcc.bdes.model.Speed;

public class EvalPrinter {

	public static double proportion(double value, double max) {
		return (value*100)/max ;
	}
	
	public static void print(SpeedDist speedDist) {
		System.out.println("== Velocidade ==");
		System.out.println("<50% Limite: \t" + speedDist.under50Limit);
		System.out.println("No do Limite: \t" + speedDist.underLimit);
		System.out.println("Até 10%: \t" + speedDist.fromLimitTo10);
		System.out.println("De 10% a 20%: \t" + speedDist.from10to20);
		System.out.println("De 20% a 50%: \t" + speedDist.from20to50);
		System.out.println("Acima de 50%: \t" + speedDist.over50);
	}
	
	public static void print(AccelerationDist accDist) {
		System.out.println("== Desaceleração ==");
		System.out.println("De    0 a -3.0: \t" + accDist.decFrom0ToMinus30 + "(" + percent(accDist.decFrom0ToMinus30,accDist.totalCount) + ")");
		System.out.println("De -3.0 a -6.0: \t" + accDist.decFromMinus30toMinus60 + "(" + percent(accDist.decFromMinus30toMinus60,accDist.totalCount) + ")");
		System.out.println("De -6.0 a -9.0: \t" + accDist.decFromMinus60toMinus90 + "(" + percent(accDist.decFromMinus60toMinus90,accDist.totalCount) + ")");
		System.out.println("De -9.0 a -12.0: \t" + accDist.decFromMinus90toInfinity + "(" + percent(accDist.decFromMinus90toInfinity,accDist.totalCount) + ")");
		
		System.out.println("== Aceleração ==");
		System.out.println("De 0 a 2.5: \t" + accDist.accFrom0ToMinus25 + "(" + percent( accDist.accFrom0ToMinus25,accDist.totalCount) + ")");
		System.out.println("De 2.5 a 4.3: \t" + accDist.accFrom25toMinus43 + "(" + percent(accDist.accFrom25toMinus43,accDist.totalCount) + ")");
		System.out.println("De 4.3 a 7.3: \t" + accDist.accFromMinus43toMinus73 + "(" + percent(accDist.accFromMinus43toMinus73,accDist.totalCount) + ")");
		System.out.println("De 7.3 a 12.0: \t" + accDist.accFromMinus73toInfinity + "(" + percent(accDist.accFromMinus73toInfinity,accDist.totalCount) + ")");
	}
	
	private static String percent(int value, int total) {
		double percent = (double) value/total;
		return String.format("%.2f", percent * 100) + "%";
	}

	public static void print(MyEval eval) {
		System.out.println("Speed Index: \t" + eval.speedIndex);
		System.out.println("Acc Index: \t" + eval.accIndex);
		System.out.println("Overall: \t" + eval.trajectoryIndex);
		
		System.out.println("Avg Speed: \t" + new Speed(eval.avgSpeed).getKmh());
		System.out.println("Avg Acc: \t" + eval.avgAcc + " m/s²\n");
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
		System.out.println("Weight Acc index: \t" + accIndex);
		
	}
	

}
