package br.udesc.dcc.bdes.model;


public class SpeedIndexEval {
	private double max;

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
		return index > 100 ? 100 : index;
	}
	
	public void changeMax(Speed newSpeed) {
		this.max = newSpeed.getMs();
	}
	
	
	
}
