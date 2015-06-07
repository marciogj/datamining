package br.udesc.dcc.bdes.datamining.cluster.element;


public class Number implements Element {
	protected double value;

	public Number(double value) {
		this.value = value; 
	}
	
	@Override
	public double distance(Element another) {
		double result = value - ( (Number) another).value; 
		return result < 0 ? result * (-1) : result;
	}

	@Override
	public Element plus(Element another) {
		double result = value + ( (Number) another).value;
		return new Number(result);
	}

	@Override
	public Element divide(int dividend) {
		double result = this.value / dividend;
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

}
