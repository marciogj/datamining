package br.udesc.dcc.bdes.analysis;

import java.util.ArrayList;
import java.util.List;

public class AccelerationEvaluator {
	private List<AccelerationLimit> limits = new ArrayList<>();
	
	public AccelerationEvaluator() {
		//Definition according to Bagdadi and Varhelyi
		limits.add(new AccelerationLimit(-3.0, "Very Secure Desacceleration", 1));
		limits.add(new AccelerationLimit(-6.0, "Secure Desacceleration", 2.0));
		limits.add(new AccelerationLimit(-9.0, "Dangerous Desacceleration", 3.5));
		limits.add(new AccelerationLimit(-12.0, "Very dangerous Desacceleration", 4.5));
		
		limits.add(new AccelerationLimit(2.5, "Very secure acceleration", 1));
		limits.add(new AccelerationLimit(3.0, "Secure acceleration", 1.5));
		limits.add(new AccelerationLimit(4.3, "Dangerous acceleration", 2));
		limits.add(new AccelerationLimit(7.0, "Very dangerous acceleration", 3.5));
	}
	
	private AccelerationLimit findLimit(double value) {
		for (AccelerationLimit accLimit : limits) {
			boolean isFromThisLimit = value < 0 ? value >= accLimit.limit : value <= accLimit.limit; 
			if (isFromThisLimit) {
				return accLimit;
			}
		}
		return limits.get(limits.size()-1);
	}
		
	public int evaluate(double acceleration) {
		AccelerationLimit limit = findLimit(acceleration);
		limit.evaluate(acceleration);
		return (int) Math.round(acceleration * limit.weight);
		
	}
	
	public List<AccelerationLimit> getAccEval(){
		return limits;
	}
}



