import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
		Trajectory trajectory = PltFileReader.read("20081023025304.plt");			
		print(TrajectoryEvaluator.evaluate(trajectory));
		
		
		BiFunction<Coordinate, Coordinate, Double> distanceInSeconds = (c1,c2) -> {
			return new Double(Math.abs((c1.getDateTimeInMillis() - c2.getDateTimeInMillis())/1000));
		};
		
		List<Map.Entry<Coordinate, Double>> kdistance = EsterDBScanHeuristic.kdistance(trajectory.getCoordinates(), minPts, distanceInSeconds);
		kdistance.forEach( e -> System.out.print(e.getValue() +","));
		
		System.out.println();
		System.out.println("----");
		
		DBScan<Coordinate> dbscan = new DBScan<>();
		Collection<Coordinate> data = trajectory.getCoordinates();
		
		DBScanResult<Coordinate> result = dbscan.evaluate(data, eps, minPts, distanceInSeconds);
		Trajectory cleanedTrajectory = new Trajectory();
		result.getClusters().forEach( cluster -> {
			cleanedTrajectory.addAll(cluster.getElements());
		});
		
		print(TrajectoryEvaluator.evaluate(cleanedTrajectory));
		
		
		System.out.println("Trajectories: " + result.getClusters().size());
		for (Cluster<Coordinate> cluster : result.getClusters()) {
			System.out.println(cluster.getName() + " with " + cluster.size() + " pts");
			LocalDateTime start = null;
			LocalDateTime end = null;
			for (Coordinate coordinate : cluster.getElements()) {
				if (start == null ) {
					start = coordinate.getDateTime();
				} else {
					end = (end == null || coordinate.getDateTime().isAfter(end)) ? coordinate.getDateTime() : end;
				}
			}
			System.out.println("Time: " + start + " - " + end);
		}
		
		System.out.println("\n#### NOISES " + result.getNoises().size() +  " ####");
		result.getNoises().forEach(c -> System.out.println(c+ ", ") );
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
		String datetime = new SimpleDateFormat("yyyy.MM.dd_HHmmss").format(new Date());
		try {
			TrajectoryCSVFileWriter.write(data, "dbscan_data_"+datetime+".csv");
			TrajectoryCSVFileWriter.write(noises, "dbscan_noise_"+datetime+".csv");
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
