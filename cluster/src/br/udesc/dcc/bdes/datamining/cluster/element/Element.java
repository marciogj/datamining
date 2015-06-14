package br.udesc.dcc.bdes.datamining.cluster.element;

public interface Element {
	
	public double distance(Element another);
	
	public double euclideanDistance(Element another);

	public Element plus(Element element);

	public Element divide(double dividend);
	
	public Element multiply(double value);

	public Element zero();
	
}
