package br.udesc.dcc.bdes.datamining.cluster.density.fn;


public class ClusterizableCoordinate {
		
	public static Double distance(Coordinate coordinate, Coordinate another) {
		return coordinate.distanceInMeters(another);
	}

	
}
