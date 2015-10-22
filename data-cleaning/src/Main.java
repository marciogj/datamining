import br.udesc.dcc.bdes.analysis.EvaluatedTrajectory;
import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.cleaning.TrajectoryCleaner;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.PltFileReader;


public class Main {

	public static void main(String[] args) {
		Trajectory trajectory = PltFileReader.read("cleaned-eps5.plt");
		EvaluatedTrajectory evaluatedTracjectory = TrajectoryEvaluator.evaluate(trajectory);
		print(evaluatedTracjectory);
		
		
		Trajectory newTrajectory = TrajectoryCleaner.removeNoise(trajectory);
		EvaluatedTrajectory evaluatedTracjectoryWithoutNoise = TrajectoryEvaluator.evaluate(newTrajectory);
		print(evaluatedTracjectoryWithoutNoise);
		
		//noise from noise
		newTrajectory = TrajectoryCleaner.removeNoise(newTrajectory);
		evaluatedTracjectoryWithoutNoise = TrajectoryEvaluator.evaluate(newTrajectory);
		print(evaluatedTracjectoryWithoutNoise);
		
		//noise from noise
		newTrajectory = TrajectoryCleaner.removeNoise(newTrajectory);
		evaluatedTracjectoryWithoutNoise = TrajectoryEvaluator.evaluate(newTrajectory);
		print(evaluatedTracjectoryWithoutNoise);
		
		
		//noise from noise
		newTrajectory = TrajectoryCleaner.removeNoise(newTrajectory);
		evaluatedTracjectoryWithoutNoise = TrajectoryEvaluator.evaluate(newTrajectory);
		print(evaluatedTracjectoryWithoutNoise);
		
		//
		newTrajectory = TrajectoryCleaner.removeNoise(newTrajectory);
		evaluatedTracjectoryWithoutNoise = TrajectoryEvaluator.evaluate(newTrajectory);
		print(evaluatedTracjectoryWithoutNoise);
		
		
		/*GeolifeTrajectorySummary summary = GeolifeTrajectoryEvaluator.evaluate(trajectory, 55, 5);
		
		System.out.println("Distance: " + summary.getTotalDistanceInMeters()/1000.0 + " Km");
		System.out.println("Time: " + summary.getTotalTimeInSeconds() + " s | " + summary.getTotalTimeInSeconds()/3600 + " h");
		System.out.println("Max Speed: " + summary.getMaxSpeedInMetersPerSecond()  + " m/s | " + summary.getMaxSpeedInMetersPerSecond() * 3.6 + " km/h");
		System.out.println("Avg Speed: " + summary.getAvgSpeedInMetersPerSecond()  + " m/s | " + summary.getAvgSpeedInMetersPerSecond() * 3.6 + " km/h");
		System.out.println("Max Acc: " + summary.getMaxAccelerationInMetersPerSecond()  + " m/s | " + summary.getMaxAccelerationInMetersPerSecond() * 3.6 + " km/h");
		*/
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
