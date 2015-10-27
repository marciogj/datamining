package br.udesc.dcc.bdes.cleaning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import br.udesc.dcc.bdes.datamining.cluster.ClusterSet;
import br.udesc.dcc.bdes.datamining.cluster.density.DBScan;
import br.udesc.dcc.bdes.datamining.cluster.density.DBScanResult;
import br.udesc.dcc.bdes.datamining.cluster.element.Element;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;

public class TrajectoryCleaner {
	public static final int MINPTS = 50;
	public static final double EPS = 2.78 ; //~10 km/h
	
	public static Trajectory removeNoiseBySpeedWithApacheDBScan(Trajectory trajectory) {
		Trajectory newTrajectory = new Trajectory();
		
		List<Cluster<SpeedCoordinate>> clusters = clusterBySpeedWithApacheDbScan(trajectory);;
		for (Cluster<SpeedCoordinate> cluster : clusters) {
			List<SpeedCoordinate> coordinateCluster = cluster.getPoints();
			
			for (SpeedCoordinate speedCoordinate : coordinateCluster) {
				newTrajectory.add(speedCoordinate.getCoordinate());
			}	
		}
		
		return newTrajectory;
	}
	
	public static Trajectory removeNoiseFromSpeed(Trajectory trajectory) {
		Trajectory newTrajectory = new Trajectory();
		
		ClusterSet clusters = clusterBySpeedWithDbScan(trajectory).getClusterSet();
		for (br.udesc.dcc.bdes.datamining.cluster.Cluster cluster : clusters.getClusters()) {
			List<Element> coordinateCluster = cluster.getElements();
			
			for (Element speedCoordinate : coordinateCluster) {
				newTrajectory.add(((SpeedCoordinate)speedCoordinate).getCoordinate());
			}	
		}
		
		return newTrajectory;
	}
	
	public static Trajectory createTrajectoryFromClusterizableCoordinate(ClusterSet clusterSet) {
		Trajectory newTrajectory = new Trajectory();
		
		for (br.udesc.dcc.bdes.datamining.cluster.Cluster cluster : clusterSet.getClusters()) {
			List<Element> coordinateCluster = cluster.getElements();
			
			for (Element coord : coordinateCluster) {
				newTrajectory.add(((ClusterizableCoordinate)coord).getCoordinate());
			}	
		}
		
		return newTrajectory;
	}
	
	public static List<Cluster<SpeedCoordinate>> clusterBySpeedWithApacheDbScan(Trajectory trajectory) {
		Collection<Coordinate> coordinates = trajectory.getCoordinates();
		Collection<SpeedCoordinate> speedCoordinates = new ArrayList<>(coordinates.size());
		for(Coordinate coordinate : coordinates) {
			speedCoordinates.add(new SpeedCoordinate(coordinate));
		}
		System.out.println("=== Apache (Speed Cluster)===");
		DBSCANClusterer<SpeedCoordinate> apacheDbscan = new DBSCANClusterer<>(EPS, MINPTS);
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
	
	public static DBScanResult clusterBySpeedWithDbScan(Trajectory trajectory) {
		Collection<Coordinate> coordinates = trajectory.getCoordinates();
		Collection<SpeedCoordinate> speedCoordinates = new ArrayList<>(coordinates.size());
		for(Coordinate coordinate : coordinates) {
			speedCoordinates.add(new SpeedCoordinate(coordinate));
		}
		System.out.println("=== MyDBScan (Speed Cluster) ===");
		DBScan dbscan = new DBScan();
		DBScanResult solution = dbscan.dbscan(speedCoordinates, EPS, MINPTS);
		//System.out.println(Printer.clusterSetToString(solution));		
				
		System.out.println("=========================");
		return solution;
	}
	
	
	
	public static DBScanResult clusterByCoordinate(Trajectory trajectory) {
		double eps = 40 ; //meters
		int minPts = 6; //points
		
		Collection<Coordinate> coordinates = trajectory.getCoordinates();
		Collection<ClusterizableCoordinate> clusterizableCoordinates = new ArrayList<>(coordinates.size());
		for(Coordinate coordinate : coordinates) {
			clusterizableCoordinates.add(new ClusterizableCoordinate(coordinate));
		}
		
		System.out.println("=== MyDBScan (Coordinate Cluster) ===");
		DBScan dbScan = new DBScan();
		DBScanResult result = dbScan.dbscan(clusterizableCoordinates, eps, minPts);
		
				
		System.out.println("=========================");
		return result;
	}
	
	
	
	

}
