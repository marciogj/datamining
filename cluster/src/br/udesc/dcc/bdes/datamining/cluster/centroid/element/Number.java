package br.udesc.dcc.bdes.datamining.cluster.centroid.element;

import org.apache.commons.math3.ml.clustering.Clusterable;


public class Number implements Element, Clusterable {
	protected double value;

	public Number(double value) {
		this.value = value; 
	}
	
	@Override
	public double distance(Element another) {
		return Math.abs(value - ( (Number) another).value);
	}

	@Override
	public Element plus(Element another) {
		double result = value + ( (Number) another).value;
		return new Number(result);
	}

	@Override
	public Element divide(double dividend) {
		double result = this.value / dividend;
		return new Number(result);
	}
	
	@Override
	public Element multiply(double value) {
		double result = this.value * value;
		return new Number(result);
	}

	@Override
	public Element zero() {
		return new Number(0);
	}
	
	@Override
	public String toString() {
		return "" + value;
	}
	
	@Override
	public double euclideanDistance(Element another){
		double difference = value - ( (Number) another).value;
		return Math.sqrt(difference * difference);
	}

	@Override
	public double[] getPoint() {
		return new double[] {value};
	}

}
