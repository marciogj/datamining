package br.udesc.dcc.bdes.model;

public class Distance {
	private double meters;
	
	public Distance(final double meters) {
		this.meters = meters;
	}
	
	public Distance() {
		this.meters = 0;
	}

	public double getMeters() {
		return meters;
	}
	
	public double getKilometers() {
		return meters/1000.0;
	}

	public void increase(final double distance) {
		this.meters += distance;
	}

	public void reset() {
		this.meters = 0;
	}

}
