import java.io.File;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import br.udesc.dcc.bdes.analysis.RealTimeTrajectoryEvaluator;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.SeniorCSVFileReader;
import br.udesc.dcc.bdes.server.GPSTrack;

import com.google.gson.Gson;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;


public class MainGPSReplay {
	
	public static void main(String[] args) {
		System.out.println("Simulating GPS coordinates from recorded files...");
		String dirPath = "C:\\Users\\marciogj\\SkyDrive\\gps-tracker-service\\";
		replayToService(dirPath);

	}
	
	public static void replayToEvaluator(String dirPath) {
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
	
	public static void replayToService(String dirPath) {
		File dir = new File(dirPath);
		
		//ClientConfig config = new DefaultClientConfig();
		//Client client = Client.create(config);
		//WebResource service = client.resource(UriBuilder.fromUri("http://localhost:8080").build());
        
		
		for(String file : dir.list()) {
			Trajectory trajectory = SeniorCSVFileReader.read(dirPath+"\\"+file);
			
			int batchLimit = 10;
			int batchSize = 0;
			GPSTrack track = new GPSTrack();
			Gson gson = new Gson();
			for (Coordinate coordinate : trajectory.getCoordinates()) {
				System.out.println(coordinate);
				
				if(batchSize > batchLimit) {
					//service.path("track").accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(gson.toJson(track));
					track = new GPSTrack();					
					batchSize = 0;
				} else {
					track.add(coordinate);
					batchSize++;
				}
				
			}
			
			//remaining track (didn't reach a batch size)
			if (batchSize > 0 ) {
				//service.path("track").accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(gson.toJson(track));
			}
			
		}
	}


}




