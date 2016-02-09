import java.io.File;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.udesc.dcc.bdes.analysis.RealTimeTrajectoryEvaluator;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.SeniorCSVFileReader;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrackDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrackMapper;


public class GPSReplay {
	public static final String SERVER_URL = "http://localhost:9090";
	private static final Logger logger = Logger.getLogger("GPSReplay");
	
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
		
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(SERVER_URL);
		logger.info("Requesting ping to " + SERVER_URL);
		Response response = target.path("services").path("server").path("ping").request(MediaType.TEXT_PLAIN_TYPE).get();
		logger.info("Response: " + response.getStatus() + " - " + response.readEntity(String.class));
		if (response.getStatus() != 200) {
			logger.info("Server is down. Aborting.");
			System.exit(0);
		}
		
		for(String file : dir.list()) {
			Trajectory trajectory = SeniorCSVFileReader.read(dirPath+"\\"+file);
			
			int batchLimit = 10;
			int batchSize = 0;
			TrackDTO track = new TrackDTO();
			track.deviceId = trajectory.getDeviceId();
			track.userId = trajectory.getId();
			for (Coordinate coordinate : trajectory.getCoordinates()) {
				if(batchSize >= batchLimit) {
					response = target.path("services").path("track").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(track, MediaType.APPLICATION_JSON_TYPE));
					logger.info("Response: " + response.getStatus());
					track = new TrackDTO();
					track.deviceId = trajectory.getDeviceId();
					track.userId = trajectory.getId();
					batchSize = 0;
				} else {
					track.coordinates.add(TrackMapper.toDto(coordinate));
					batchSize++;
				}
				
			}
			
			//remaining track (didn't reach a batch size)
			if (batchSize > 0 ) {
				response = target.path("services").path("track").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(track, MediaType.APPLICATION_JSON_TYPE));
				logger.info("Response: " + response.getStatus());
			}
			
		}
	}


}




