import br.udesc.dcc.bdes.geolife.CSVTrajectoryFileWriter;
import br.udesc.dcc.bdes.geolife.GeolifeCoordinate;
import br.udesc.dcc.bdes.geolife.GeolifeTrajectory;
import br.udesc.dcc.bdes.geolife.GeolifeTrajectoryEvaluator;
import br.udesc.dcc.bdes.geolife.GeolifeTrajectorySummary;
import br.udesc.dcc.bdes.geolife.PltFileReader;
import br.udesc.dcc.bdes.geolife.SATrajectory;


public class Main {

	public static void main(String[] args) {
		GeolifeTrajectory trajectory = PltFileReader.read("20081023055305.plt");
		
		
		SATrajectory saTrajectory = new SATrajectory();
		for (GeolifeCoordinate coordinate : trajectory.getCoordinates()) {
			saTrajectory.add(coordinate);
		}
		try {
			CSVTrajectoryFileWriter.write(saTrajectory, "20081023055305.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GeolifeTrajectorySummary summary = GeolifeTrajectoryEvaluator.evaluate(trajectory, 55, 5);
		
		System.out.println("Distance: " + summary.getTotalDistanceInMeters()/1000.0 + " Km");
		System.out.println("Time: " + summary.getTotalTimeInSeconds() + " s | " + summary.getTotalTimeInSeconds()/3600 + " h");
		System.out.println("Max Speed: " + summary.getMaxSpeedInMetersPerSecond()  + " m/s | " + summary.getMaxSpeedInMetersPerSecond() * 3.6 + " km/h");
		System.out.println("Avg Speed: " + summary.getAvgSpeedInMetersPerSecond()  + " m/s | " + summary.getAvgSpeedInMetersPerSecond() * 3.6 + " km/h");
		
	}
	
	
	
}
