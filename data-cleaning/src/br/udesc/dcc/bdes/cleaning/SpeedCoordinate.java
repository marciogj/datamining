package br.udesc.dcc.bdes.cleaning;

import org.apache.commons.math3.ml.clustering.Clusterable;

import br.udesc.dcc.bdes.datamining.cluster.element.Element;
import br.udesc.dcc.bdes.gis.Coordinate;

public class SpeedCoordinate implements Clusterable, Element {
	private Coordinate coordinate;
	
	public SpeedCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public double[] getPoint() {
		return new double [] { coordinate.getSpeed() };
	}
	
	@Override
	public String toString() {
		return coordinate.getSpeed()+"";
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	@Override
	public double distance(Element another) {
		SpeedCoordinate speedCoord = (SpeedCoordinate) another;
		return Math.abs(this.coordinate.getSpeed() - speedCoord.getCoordinate().getSpeed());
	}

	@Override
	public double euclideanDistance(Element another) {
		SpeedCoordinate speedCoord = (SpeedCoordinate) another;
		return Math.abs(this.coordinate.getSpeed() - speedCoord.getCoordinate().getSpeed());
	}

	@Override
	public Element plus(Element element) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Element divide(double dividend) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Element multiply(double value) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Element zero() {
		throw new RuntimeException("Not implemented");
	}
	
}
