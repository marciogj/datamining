import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import br.udesc.dcc.bdes.analysis.EvaluatedTrajectory;
import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.datamining.cluster.density.Cluster;
import br.udesc.dcc.bdes.datamining.cluster.density.DBScan;
import br.udesc.dcc.bdes.datamining.cluster.density.DBScanResult;
import br.udesc.dcc.bdes.datamining.cluster.density.EsterDBScanHeuristic;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.PltFileReader;
import br.udesc.dcc.bdes.io.TrajectoryCSVFileWriter;


public class Main {

	public static void main(String[] args) {
		timeDBScan();		
	}
	
	public static void timeDBScan() {
		double eps = 30; //seconds
		int minPts = 5;		
		Trajectory trajectory = PltFileReader.read("20081023055305.plt");//"20081023025304.plt");
		System.out.println("=== Raw Trajectory Evaluation ===");
		print(TrajectoryEvaluator.evaluate(trajectory));
		System.out.println();
		
		BiFunction<Coordinate, Coordinate, Double> distanceInSeconds = (c1,c2) -> {
			return new Double(Math.abs((c1.getDateTimeInMillis() - c2.getDateTimeInMillis())/1000));
		};
		System.out.println("=== Ester et al. Heuristic k-" + minPts + " ===");
		List<Map.Entry<Coordinate, Double>> kdistance = EsterDBScanHeuristic.kdistance(trajectory.getCoordinates(), minPts, distanceInSeconds);
		kdistance.forEach( e -> System.out.print(e.getValue() +","));
		System.out.println();
		
		System.out.println("=== DBScan - Sub trajectories by time distance ===");
		DBScan<Coordinate> dbscan = new DBScan<>();
		Collection<Coordinate> data = trajectory.getCoordinates();
		DBScanResult<Coordinate> result = dbscan.evaluate(data, eps, minPts, distanceInSeconds);
		System.out.println("Trajectories: " + result.getClusters().size());
		for (Cluster<Coordinate> cluster : result.getClusters()) {
			LocalDateTime start = null;
			LocalDateTime end = null;
			for (Coordinate coordinate : cluster.getElements()) {
				if (start == null ) {
					start = coordinate.getDateTime();
				} else {
					end = (end == null || coordinate.getDateTime().isAfter(end)) ? coordinate.getDateTime() : end;
				}
			}
			System.out.println(cluster.getName() + " with " + cluster.size() + " pts - Time: " + start + " - " + end);
		}
		System.out.println();
		System.out.println("Noises " + result.getNoises().size());
		result.getNoises().forEach(c -> System.out.println(c+ ", ") );
		System.out.println();
		
		
		System.out.println("=== Sub trajectories processing ===");
		double epsDistanceInMeters = 30.0;
		int minPtsDistanceMeters = 4;
		
		//BiFunction<Coordinate, Coordinate, Double> distanceInSpeed = (c1,c2) -> {
		//	return new Double(Math.abs((c1.getSpeed() - c2.getSpeed())));
		//};
		
		for (Cluster<Coordinate> cluster : result.getClusters()) {
			Trajectory subTrajectory = new Trajectory(cluster.getElements());
			System.out.println(cluster.getName());
			DBScanResult<Coordinate> dbScanDistance = distanceDBScan(subTrajectory, epsDistanceInMeters, minPtsDistanceMeters);
			Trajectory cleanedTrajectory = new Trajectory();
			dbScanDistance.getClusters().forEach( c -> {
				cleanedTrajectory.addAll(c.getElements());
			});
			
			//TODO: Find out why reset all speeds is needed
			//FIXME: Speed are being propagated to all coordinates - clone() needed.
			cleanedTrajectory.getCoordinates().forEach(c -> c.setSpeed(0.0));
			print(TrajectoryEvaluator.evaluate(cleanedTrajectory));
			
			System.out.println("#### Speed Evolution ####");
			cleanedTrajectory.getCoordinates().forEach(c -> System.out.print( (Math.round(c.getSpeed()*3.6*100.0)/100.0) + ", "));
			
			System.out.println("Noises " + dbScanDistance.getNoises().size());
			save(cluster.getName(), cleanedTrajectory.getCoordinates(), dbScanDistance.getNoises());
			
			
			//DBScanResult<Coordinate> dbscanSpeed = dbscan.evaluate(cleanedTrajectory.getCoordinates(), 1.14, 4, distanceInSpeed);
			//System.out.println(dbscanSpeed.getClusters().size());
			
			
			
			
			System.out.println();
			
			System.out.println("----------------------");
		}
		
		
		
	}
	
	
	public static DBScanResult<Coordinate> distanceDBScan(Trajectory trajectory, double eps, int minPts) {
		System.out.println("=== Trajectory Evaluation with "+trajectory.getCoordinates().size()+ " coordinates ===");
		List<Map.Entry<Coordinate, Double>> kdistance = EsterDBScanHeuristic.kdistance(trajectory.getCoordinates(), minPts, Coordinate::distance);
		System.out.println("KDistance("+minPts+"):");
		kdistance.forEach( e -> System.out.print(e.getValue() +","));
		System.out.println();
		
		//System.out.println("=== Raw Trajectory Evaluation ===");
		//print(TrajectoryEvaluator.evaluate(trajectory));
		//System.out.println();
		
		System.out.println("=== DBScan - Noise removal by distance eps("+eps+") | minPts("+minPts+") ===");
		DBScan<Coordinate> dbscan = new DBScan<>();
		Collection<Coordinate> data = trajectory.getCoordinates();
		return dbscan.evaluate(data, eps, minPts, Coordinate::distance);
	}
	
	
	public static void distanceDBScan() {
		double eps = 25.0;
		int minPts = 4;		
		Trajectory trajectory = PltFileReader.read("20081023055305.plt");			
		print(TrajectoryEvaluator.evaluate(trajectory));
		
		List<Map.Entry<Coordinate, Double>> kdistance = EsterDBScanHeuristic.kdistance(trajectory.getCoordinates(), minPts, Coordinate::distance);
		kdistance.forEach( e -> System.out.print(e.getValue() +","));
		
		System.out.println();
		System.out.println("----");
		
		DBScan<Coordinate> dbscan = new DBScan<>();
		Collection<Coordinate> data = trajectory.getCoordinates();
		
		DBScanResult<Coordinate> result = dbscan.evaluate(data, eps, minPts, Coordinate::distance);
		Trajectory cleanedTrajectory = new Trajectory();
		result.getClusters().forEach( cluster -> {
			cleanedTrajectory.addAll(cluster.getElements());
		});
		
		print(TrajectoryEvaluator.evaluate(cleanedTrajectory));
	}
	
	public static void save(Collection<Coordinate> data, Collection<Coordinate> noises) {
		save("", data, noises);
	}
	
	public static void save(String identifier, Collection<Coordinate> data, Collection<Coordinate> noises) {
		String datetime = new SimpleDateFormat("yyyy.MM.dd_HHmmss").format(new Date());
		try {
			TrajectoryCSVFileWriter.write(data, identifier+"_dbscan_data_"+datetime+".csv");
			TrajectoryCSVFileWriter.write(noises, identifier+"_dbscan_noise_"+datetime+".csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void print(EvaluatedTrajectory trajectory) {
		System.out.println("Coordinates: " + trajectory.getTotalCoordinates());
		System.out.println("Coordinate Rate: " + trajectory.getCoordinateRate() + " seconds/coordinate");
		System.out.println("Distance: " + trajectory.getTotalDistance()/1000 + " km");
		System.out.println("Time: " + trajectory.getTotalTime());
		
		System.out.println("Avg Speed: " + trajectory.getAvgSpeed()*3.6 + " km/h");
		System.out.println("Max Speed: " + trajectory.getMaxSpeed()*3.6 + " km/h");
		
		System.out.println("Max Slowdown: " + trajectory.getMaxSlowdown()*3.6 + " km/h");
		System.out.println("Max Speedup: " + trajectory.getMaxSpeedUp()*3.6 + " km/h");
		System.out.println("Acceleration changes: " + trajectory.getSpeedUpDownOscilations());
	}
	
}
