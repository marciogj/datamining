package br.udesc.dcc.bdes.analysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

public class AccelerationEvaluator {
	private List<AccelerationLimit> limits = new ArrayList<>();
	private double DESACC_LIMIT0 = 0;
	private double DESACC_LIMIT1 = -3.0;
	private double DESACC_LIMIT2 = -6.0;
	private double DESACC_LIMIT3 = -9.0;
	private double DESACC_LIMIT4 = -14.0;
	
	private double ACC_LIMIT0 = 0;
	private double ACC_LIMIT1 = 2.0;
	private double ACC_LIMIT2 = 3.0;
	private double ACC_LIMIT3 = 7.3;
	private double ACC_LIMIT4 = 12;
	
	
	public AccelerationEvaluator() {
		//Definition according to Bagdadi and Varhelyi
		limits.add(new AccelerationLimit(DESACC_LIMIT1, "a) Very Secure Desacceleration", 0));
		limits.add(new AccelerationLimit(DESACC_LIMIT2, "b) Secure Desacceleration", 1));
		limits.add(new AccelerationLimit(DESACC_LIMIT3, "c) Dangerous Desacceleration", 1.5));
		limits.add(new AccelerationLimit(DESACC_LIMIT4, "d) Very dangerous Desacceleration", 3));
		
		limits.add(new AccelerationLimit(ACC_LIMIT1, "f) Very secure acceleration", 0));
		limits.add(new AccelerationLimit(ACC_LIMIT2, "g) Secure acceleration", 1));
		limits.add(new AccelerationLimit(ACC_LIMIT3, "h) Dangerous acceleration", 1.5));
		limits.add(new AccelerationLimit(ACC_LIMIT4, "i) Very dangerous acceleration", 3));
	}
	
	public void count(double acceleration) {
		if (acceleration <= DESACC_LIMIT0 && acceleration >= DESACC_LIMIT1) {
			limits.get(0).count++;
			limits.get(0).sum += acceleration;
		} else if (acceleration < DESACC_LIMIT1 && acceleration >= DESACC_LIMIT2) {
			limits.get(1).count++;
			limits.get(1).sum += acceleration;
		} else if (acceleration < DESACC_LIMIT2 && acceleration >= DESACC_LIMIT3) {
			limits.get(2).count++;
			limits.get(2).sum += acceleration;
		} else if (acceleration < DESACC_LIMIT3) {
			limits.get(3).count++;
			limits.get(3).sum += acceleration;;
		} else if (acceleration > ACC_LIMIT0 && acceleration <= ACC_LIMIT1) {
			limits.get(4).count++;
			limits.get(4).sum += acceleration;
		} else if (acceleration > ACC_LIMIT1 && acceleration <= ACC_LIMIT2) {
			limits.get(5).count++;
			limits.get(5).sum += acceleration;
		} else if (acceleration > ACC_LIMIT2 && acceleration <= ACC_LIMIT3) {
			limits.get(6).count++;
			limits.get(6).sum += acceleration;
		} else if (acceleration > ACC_LIMIT3){
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
		if (acceleration < DESACC_LIMIT0 && acceleration >= DESACC_LIMIT1) {
			maxPercentage = 0;
			maxValue = 3.0;
		} else if (acceleration < DESACC_LIMIT1 && acceleration >= DESACC_LIMIT2) {
			maxPercentage = 20;
			maxValue = 4.0;
		} else if (acceleration < DESACC_LIMIT2 && acceleration >= DESACC_LIMIT3) {
			maxPercentage = 40;
			maxValue = 6.0;
		} else if (acceleration < DESACC_LIMIT3 && acceleration >= DESACC_LIMIT4) {
			maxPercentage = 60;
			maxValue = 9.0;
		} else if (acceleration < DESACC_LIMIT4) {
			maxPercentage = 100;
			maxValue = 12;
		} else if (acceleration > ACC_LIMIT0 && acceleration <= ACC_LIMIT1) {
			maxPercentage = 0;
			maxValue = 2.5;
		} else if (acceleration > ACC_LIMIT1 && acceleration <= ACC_LIMIT2) {
			maxPercentage = 20;
			maxValue = 3.0;
		} else if (acceleration > ACC_LIMIT2 && acceleration <= ACC_LIMIT3) {
			maxPercentage = 50;
			maxValue = 4.3;
		} else {
			maxPercentage = 100;
			maxValue = 7.3;
		}
		return new Pair<Double, Double>(maxValue, maxPercentage);
	}
	
	
	public List<AccelerationLimit> getAccEval(){
		return limits;
	}
}



