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
			if (value <= accLimit.limit) {
				return accLimit;
			}
		}
		return limits.get(limits.size()-1);
	}
		
	public int weight(double acceleration) {
		AccelerationLimit limit = findLimit(acceleration);
		limit.increment();
		return (int) Math.round(acceleration * limit.weight);
		
	}
}

class AccelerationLimit {
	protected double limit;
	protected String description;
	protected int count = 0;
	protected double weight = 0;
	
	public AccelerationLimit(double limit, String desc, double weight) {
		this.description = desc;
		this.limit = limit;
		this.weight = weight;
	}

	public void increment() {
		count++;
	}
	
}

