import java.io.File;


import br.udesc.dcc.bdes.analysis.RealTimeTrajectoryEvaluator;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.SeniorCSVFileReader;


public class MainGPSReplay {
	
	public static void main(String[] args) {
		System.out.println("Simulating GPS coordinates from recorded files...");
		
		//String dirPath = "C:\\Users\\marcio.jasinski\\OneDrive\\GPS_DATA\\udesc\\marcio\\";
		//String file = "Dados_Coletados_20150825_181920711.csv";
		//Trajectory trajectory = UdescCSVFileReader.read(dirPath+"\\"+file);
		
		String dirPath = "C:\\Users\\marciogj\\SkyDrive\\gps-tracker-service\\";
		
		File dir = new File(dirPath);
		RealTimeTrajectoryEvaluator evaluator = new RealTimeTrajectoryEvaluator(13.89, 6.95, -4.17);
		for(String file : dir.list()) {
			Trajectory trajectory = SeniorCSVFileReader.read(dirPath+"\\"+file);
			
			
			
			//TODO: Evaluate track changes 
			//TODO: Evaluate important places like schools 
			for (Coordinate coordinate : trajectory.getCoordinates()) {
				System.out.println(coordinate);
				
				evaluator.evaluate(coordinate);
				System.out.println("Time: " + evaluator.getTotalTime());
				System.out.println("Distance: " + evaluator.getTotalDistance() + "m");
				
				System.out.println("AvgSpeed: " + evaluator.getAvgSpeed());
				System.out.println("MaxSpeed: " + evaluator.getMaxSpeed());
				
				System.out.println("MaxAcc: " + evaluator.getMaxAccecelration());
				System.out.println("MaxDec: " + evaluator.getMaxDeceleration());
				System.out.println("AccCount: " + evaluator.getAccecelerationCount());
				System.out.println("DecCount: " + evaluator.getDecelerationCount());
				
				System.out.println("OverMaxSpeed: " + evaluator.getOverMaxSpeedCount());
				System.out.println("OverMaxAcc: " + evaluator.getOverMaxAccelerationCount());
				System.out.println("OverMaxDec: " + evaluator.getOverMaxDecelerationCount());
				
				System.out.println("--");
			}
			
		}
	}

}




