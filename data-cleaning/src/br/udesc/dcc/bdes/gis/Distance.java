package br.udesc.dcc.bdes.gis;

public class Distance {
	private double meters;
	
	public Distance(double meters) {
		this.meters = meters;
	}
	
	public double getMeters() {
		return meters;
	}
	
	public double getKilometers() {
		return meters/1000.0;
	}

}
