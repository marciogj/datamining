package br.udesc.dcc.bdes.server.rest.api.track;

import java.util.LinkedList;
import java.util.List;

import br.udesc.dcc.bdes.model.Speed;

public class SpeedIndexEval {
	private double max;
	private List<Alert> alerts = new LinkedList<>();

	/**
	 * Create an evaluator with 40km/h as max speed
	 */
	public SpeedIndexEval() {
		super();
		this.max = 40/3.6;
	}
	
	public SpeedIndexEval(Speed maxSpeed) {
		super();
		if (maxSpeed.getMs() <= 0 ) throw new IllegalArgumentException("Max Speed cannot be equal or lessa than zero");
		this.max = maxSpeed.getMs();
	}
	
	public double evaluate(Speed speed) {
		return evaluate(speed.getMs());
	}
	
	public double evaluate(double speedMs) {
		if (speedMs < max) return 0;
		double index = ( (speedMs*100)/max ) - 100;
		if (index >= 20 && index <= 50) {
			alerts.add(Alert.severe("Speeding over 20% up to 50% of max speed: " + new Speed(speedMs).getKmh() + " km/h"));
		}
		if (index > 50) {
			alerts.add(Alert.verySevere("Speeding over 50% of max speed: " + new Speed(speedMs).getKmh() + " km/h"));
		}
		return index;
	}
	
	public void changeMax(Speed newSpeed) {
		this.max = newSpeed.getMs();
	}
	
	public void clearAlerts() {
		this.alerts.clear();
	}
	
	public List<Alert> getAlerts() {
		return alerts;
	}
	
	
}
