package br.udesc.dcc.bdes.datamining.cluster.element;


public class NumberElement implements Element {
	protected double value;

	public NumberElement(double value) {
		this.value = value; 
	}
	
	@Override
	public double distance(Element another) {
		double result = value - ( (NumberElement) another).value; 
		return result < 0 ? result * (-1) : result;
	}

	@Override
	public Element plus(Element another) {
		double result = value + ( (NumberElement) another).value;
		return new NumberElement(result);
	}

	@Override
	public Element divide(int dividend) {
		double result = this.value / dividend;
		return new NumberElement(result);
	}

	@Override
	public Element zero() {
		return new NumberElement(0);
	}
	
	@Override
	public String toString() {
		return "" + value;
	}

}
