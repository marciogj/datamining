package br.udesc.dcc.bdes.datamining.cluster.density.fn;

import java.util.Collection;

public class FnMain {

	public static void main(String[] args) {
		DBScan<Coordinate> x = new DBScan<>();
		Collection<Coordinate> data = null;
		double eps = 1.0;
		int minPts = 5;
		
		x.dbscan(data, eps, minPts, ClusterizableCoordinate::distance);

	}

}
