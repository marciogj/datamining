import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.udesc.dcc.bdes.analysis.EvaluatedTrajectory;
import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.cleaning.ClusterizableCoordinate;
import br.udesc.dcc.bdes.cleaning.TrajectoryCleaner;
import br.udesc.dcc.bdes.datamining.cluster.density.DBScanResult;
import br.udesc.dcc.bdes.datamining.cluster.element.Element;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.PltFileReader;
import br.udesc.dcc.bdes.io.TrajectoryCSVFileWriter;


public class Main {

	public static void main(String[] args) {
		//Trajectory trajectory = PltFileReader.read("cleaned-eps5.plt");
		Trajectory trajectory = PltFileReader.read("20081023055305.plt");
		
		
		EvaluatedTrajectory evaluatedTracjectory = TrajectoryEvaluator.evaluate(trajectory);
		print(evaluatedTracjectory);
		
		/*
		Trajectory newTrajectory = TrajectoryCleaner.removeNoiseBySpeedWithApacheDBScan(trajectory);
		EvaluatedTrajectory evaluatedTracjectoryWithoutNoise = TrajectoryEvaluator.evaluate(newTrajectory);
		print(evaluatedTracjectoryWithoutNoise);
		
		System.out.println("###############################");
		
		Trajectory newTrajectory2 = TrajectoryCleaner.removeNoiseFromSpeed(trajectory);
		EvaluatedTrajectory evaluatedTracjectoryWithoutNoise2 = TrajectoryEvaluator.evaluate(newTrajectory2);
		print(evaluatedTracjectoryWithoutNoise2);
		
		System.out.println("###############################");
		*/
		
		DBScanResult result = TrajectoryCleaner.clusterByCoordinate(trajectory);
		Trajectory newTrajectory3 = TrajectoryCleaner.createTrajectoryFromClusterizableCoordinate(result.getClusterSet());
		
		EvaluatedTrajectory evaluatedTracjectoryWithoutNoise3 = TrajectoryEvaluator.evaluate(newTrajectory3);
		print(evaluatedTracjectoryWithoutNoise3);
		
		
		String datetime = new SimpleDateFormat("yyyy.MM.dd_HHmmss").format(new Date());
		
		try {
			TrajectoryCSVFileWriter.write(evaluatedTracjectoryWithoutNoise3, "dbscan_data_"+datetime+".csv");
			
			TrajectoryCSVFileWriter.write(convert(result.getNoise()), "dbscan_noise_"+datetime+".csv");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static Collection<Coordinate> convert(Collection<Element> elements) {
		Collection<Coordinate> coordinates = new ArrayList<>();
		for (Element e : elements) {
			coordinates.add(((ClusterizableCoordinate) e).getCoordinate());
			
		}
		return coordinates;
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
