package br.udesc.dcc.bdes.analysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

public class AccelerationEvaluator {
	private List<AccelerationLimit> limits = new ArrayList<>();
	
	public AccelerationEvaluator() {
		//Definition according to Bagdadi and Varhelyi
		limits.add(new AccelerationLimit(-3.0, "a) Very Secure Desacceleration", 1));
		limits.add(new AccelerationLimit(-6.0, "b) Secure Desacceleration", 2.0));
		limits.add(new AccelerationLimit(-9.0, "c) Dangerous Desacceleration", 3.5));
		limits.add(new AccelerationLimit(-12.0, "d) Very dangerous Desacceleration", 4.5));
		
		limits.add(new AccelerationLimit(2.5, "f) Very secure acceleration", 1));
		limits.add(new AccelerationLimit(3.0, "g) Secure acceleration", 1.5));
		limits.add(new AccelerationLimit(4.3, "h) Dangerous acceleration", 2));
		limits.add(new AccelerationLimit(7.3, "i) Very dangerous acceleration", 3.5));
	}
	
	public void count(double acceleration) {
		if (acceleration <= 0 && acceleration >= -3.0) {
			limits.get(0).count++;
			limits.get(0).sum += acceleration;
		} else if (acceleration < -3.0 && acceleration >= -6.0) {
			limits.get(1).count++;
			limits.get(1).sum += acceleration;
		} else if (acceleration < -6.0 && acceleration >= -9.0) {
			limits.get(2).count++;
			limits.get(2).sum += acceleration;
		} else if (acceleration < -9.0) {
			limits.get(3).count++;
			limits.get(3).sum += acceleration;;
		} else if (acceleration > 0 && acceleration <= 2.5) {
			limits.get(4).count++;
			limits.get(4).sum += acceleration;
		} else if (acceleration > 2.5 && acceleration <= 3.0) {
			limits.get(5).count++;
			limits.get(5).sum += acceleration;
		} else if (acceleration > 3.0 && acceleration <= 4.3) {
			limits.get(6).count++;
			limits.get(6).sum += acceleration;
		} else if (acceleration >= 7.3){
			limits.get(7).count++;
			limits.get(7).sum += acceleration;
		} 
	}
	
	public double evaluate(double acceleration) {
		Pair<Double, Double> limits = getLimts(acceleration);
		
		double maxValue = limits.getFirst();
		double maxPercentage = limits.getSecond();
		double value = (Math.abs(acceleration) * maxPercentage)/maxValue;
		return value > 100 ? 100 : value;
	}
	
	private Pair<Double, Double> getLimts(double acceleration) {
		double maxValue = 0;
		double maxPercentage = 0;
		if (acceleration < 0 && acceleration >= -3.0) {
			maxPercentage = 0;
			maxValue = 3.0;
		} else if (acceleration <= -3.1 && acceleration >= -4.0) {
			maxPercentage = 20;
			maxValue = 4.0;
		} else if (acceleration <= -4.1 && acceleration >= -6.0) {
			maxPercentage = 40;
			maxValue = 6.0;
		} else if (acceleration <= -6.1 && acceleration >= -9.0) {
			maxPercentage = 60;
			maxValue = 9.0;
		} else if (acceleration <= -9.1 && acceleration >= -12.0) {
			maxPercentage = 100;
			maxValue = 12;
		} else if (acceleration > 0 && acceleration <= 2.5) {
			maxPercentage = 0;
			maxValue = 2.5;
		} else if (acceleration >= 2.6 && acceleration <= 3.0) {
			maxPercentage = 20;
			maxValue = 3.0;
		} else if (acceleration >= 3.1 && acceleration <= 4.3) {
			maxPercentage = 50;
			maxValue = 4.3;
		} else {
			maxPercentage = 100;
			maxValue = 7.3;
		}
		return new Pair<Double, Double>(maxValue, maxPercentage);
	}
	
	
	/*
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
		
	}*/
	
	public List<AccelerationLimit> getAccEval(){
		return limits;
	}
}



