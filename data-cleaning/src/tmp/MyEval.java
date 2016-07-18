package tmp;

import java.util.ArrayList;
import java.util.List;

import br.udesc.dcc.bdes.analysis.AccelerationEvaluator;

public class MyEval {
	double avgSpeed;
	double avgAcc;
	List<Double> segmentSpeedIndexes = new ArrayList<>();
	List<Double> segmentAccIndexes = new ArrayList<>();
	List<Double> segmentIndex = new ArrayList<>(); 
	double speedIndex;
	double accIndex;
	double trajectoryIndex;
	SpeedDist speedDist;
	AccelerationDist accDist = new AccelerationDist();
	AccelerationEvaluator accEval = new AccelerationEvaluator();
	
	double weightSpeedIndex;
	double weightAccIndex;
	
	MyEval(double speedLimitMs) {
		this.speedDist = new SpeedDist(speedLimitMs);
	}
	
}
