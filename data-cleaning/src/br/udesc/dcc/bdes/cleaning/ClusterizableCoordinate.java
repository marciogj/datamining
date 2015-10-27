package br.udesc.dcc.bdes.cleaning;

import br.udesc.dcc.bdes.datamining.cluster.element.Element;
import br.udesc.dcc.bdes.gis.Coordinate;

public class ClusterizableCoordinate implements Element {
	Coordinate coordinate;
	
	public ClusterizableCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public double distance(Element another) {
		ClusterizableCoordinate other = (ClusterizableCoordinate) another;
		return coordinate.distanceInMeters(other.coordinate);
	}

	@Override
	public double euclideanDistance(Element another) {
		ClusterizableCoordinate other = (ClusterizableCoordinate) another;
		return coordinate.distanceInMeters(other.coordinate);
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

	public String toString() {
		return coordinate.toString();
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}
	
}
