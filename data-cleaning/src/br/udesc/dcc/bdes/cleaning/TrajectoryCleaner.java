package br.udesc.dcc.bdes.cleaning;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;

public class TrajectoryCleaner {
	
	//remove invalid data
	
	//split trajectory based on stops
	
	public static Trajectory removeNoise(Trajectory trajectory) {
		Trajectory newTrajectory = new Trajectory();
		
		List<Cluster<SpeedCoordinate>> clusters = clusterBySpeed(trajectory);;
		for (Cluster<SpeedCoordinate> cluster : clusters) {
			List<SpeedCoordinate> coordinateCluster = cluster.getPoints();
			
			for (SpeedCoordinate speedCoordinate : coordinateCluster) {
				newTrajectory.add(speedCoordinate.getCoordinate());
			}	
		}
		
		return newTrajectory;
	}
	
	public static List<Cluster<SpeedCoordinate>> clusterBySpeed(Trajectory trajectory) {
		List<Coordinate> coordinates = trajectory.getCoordinates();
		List<SpeedCoordinate> speedCoordinates = new ArrayList<>(coordinates.size());
		for(Coordinate coordinate : coordinates) {
			speedCoordinates.add(new SpeedCoordinate(coordinate));
		}
		
		//long startTime = System.nanoTime();
		double eps = 1.1 ; //~5 km/h
		int minPts = 30;
		
		System.out.println("=== Apache ===");
		DBSCANClusterer<SpeedCoordinate> apacheDbscan = new DBSCANClusterer<>(eps, minPts);
		List<Cluster<SpeedCoordinate>> clusters = apacheDbscan.cluster(speedCoordinates);
		int i = 0;
		for (Cluster<SpeedCoordinate> cluster : clusters) {
			List<SpeedCoordinate> coordinateCluster = cluster.getPoints();
			System.out.println("Cluster " + i + " - Size: " + coordinateCluster.size());
			
			for (SpeedCoordinate coordinate : coordinateCluster) {
				System.out.print(coordinate +", ");
			}
			System.out.println();
			i++;
		}
		
		
				
		System.out.println("=========================");
		return clusters;
	}

}
