package br.udesc.dcc.bdes.analysis;

import java.util.Collection;

import br.udesc.dcc.bdes.datamining.cluster.density.DBScanResult;
import br.udesc.dcc.bdes.datamining.cluster.density.EsterDBScan;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;

public class TrajectoryCleaner {
	
	
	public static Trajectory removeNoiseCoordinates(Trajectory trajectory) {
		double eps = 30;
		int minPts = 4;
		DBScanResult<Coordinate> dbScanDistance = distanceDBScan(trajectory, eps, minPts);
		Trajectory cleanedTrajectory = new Trajectory();
		dbScanDistance.getClusters().forEach( c -> {
			cleanedTrajectory.addAll(c.getElements());
		});
		return cleanedTrajectory;
	}
	
	
	public static DBScanResult<Coordinate> distanceDBScan(Trajectory trajectory, double eps, int minPts) {
		EsterDBScan<Coordinate> dbscan = new EsterDBScan<>();
		Collection<Coordinate> data = trajectory.getCoordinates();
		return dbscan.evaluate(data, eps, minPts, Coordinate::distance);
	}

}
