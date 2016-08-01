package br.udesc.dcc.bdes.analysis.newapp;

public class Index {
	double sum = 0;
	int count = 0;
	
	public void add(double value) {
		sum += value;
		count++;
	}
	
	public double get() {
		if (count == 0) return 0;
		return sum/count;
	}
}
