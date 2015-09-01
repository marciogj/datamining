import br.udesc.dcc.bdes.geolife.GeolifeTrajectory;
import br.udesc.dcc.bdes.geolife.PltFileReader;


public class Main {

	public static void main(String[] args) {
		GeolifeTrajectory trajectory = PltFileReader.read("20081023055305.plt");
		
		System.out.println("Distance: " + trajectory.getDistanceMeters() + " m");
		System.out.println("Distance: " + trajectory.getDistanceMeters()/1000.0 + " Km");
		System.out.println("Time: " + trajectory.getTotalTimeSeconds() + " s");
		System.out.println("Time: " + trajectory.getTotalTimeSeconds()/3600 + " h");
		System.out.println("Max Speed: " + trajectory.getMaxSpeedInMetersPerSecond() + " m/s");
		System.out.println("Max Speed: " + trajectory.getMaxSpeedInMetersPerSecond() * 3.6 + " km/h");
		System.out.println("Avg Speed: " + trajectory.getAvgSpeedInMetersPerSecond()  + " m/s");
		System.out.println("Avg Speed: " + trajectory.getAvgSpeedInMetersPerSecond() * 3.6 + " km/h");
		
	}
	
}
