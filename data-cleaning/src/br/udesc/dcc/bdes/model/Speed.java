package br.udesc.dcc.bdes.model;


public class Speed {
	private final double meterPerSec;
	
	public static Speed fromKmh(double speedkmh) {
		return new Speed(speedkmh/3.6);
	}
	
	public Speed(double meterPerSec) {
		this.meterPerSec = meterPerSec;
	}
	
	public double getKmh() {
		return meterPerSec * 3.6;
	}

	public double getMs() {
		return meterPerSec;
	}
	
}
