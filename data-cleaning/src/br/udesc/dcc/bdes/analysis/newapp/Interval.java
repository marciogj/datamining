package br.udesc.dcc.bdes.analysis.newapp;

class Interval {
	double min;
	double max;

	public Interval(double min, double max) {
		super();
		this.min = min;
		this.max = max;
	}

	public double diff() {
		return max - min;
	}

	public static Interval of(double min, double max) {
		return new Interval(min, max);
	}
}