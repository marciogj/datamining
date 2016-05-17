package br.udesc.dcc.bdes.model;

public class Acceleration {
	private double accelerationMs2;
	
	public Acceleration(double metersSeconds2) {
		this.accelerationMs2 = metersSeconds2;
	}
	
	public double getMPerSec2() {
		return accelerationMs2;
	}
	
	//https://www.unitjuggler.com/convert-acceleration-from-ms2-to-kmmin2.html?val=2.33
	public double getKmPerHour2() {
		return accelerationMs2 * 12960;
	}
	
	public double getKmPerMin2() {
		return accelerationMs2 * 3.6;
	}
	
	
}
