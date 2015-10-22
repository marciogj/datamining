package br.udesc.dcc.bdes.cleaning;

import org.apache.commons.math3.ml.clustering.Clusterable;

import br.udesc.dcc.bdes.gis.Coordinate;

public class SpeedCoordinate implements Clusterable {
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
	
}
