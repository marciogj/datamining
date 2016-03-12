package br.udesc.dcc.bdes;
import java.io.File;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluation;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.io.SeniorCSVFileReader;
import br.udesc.dcc.bdes.server.model.TrajectoryTelemetry;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrackDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectoryMapper;


public class GPSReplay {
	public static final String SERVER_URL = "http://localhost:9090";
	private static final Logger logger = Logger.getLogger("GPSReplay");
	
	public static void main(String[] args) {
		System.out.println("Simulating GPS coordinates from recorded files...");
		String dirPath = "C:\\Users\\marciogj\\SkyDrive\\gps-tracker-service\\001";
		replayToService(dirPath);
		//replayToEvaluator(dirPath);
		
	}
	
	public static void replayToEvaluator(String dirPath) {
		File dir = new File(dirPath);
		TrajectoryEvaluation evaluator = new TrajectoryEvaluation(13.89, 6.95, -4.17);
		for(String file : dir.list()) {
			Trajectory trajectory = SeniorCSVFileReader.read(dirPath+"\\"+file);
			
			//TODO: Evaluate track changes 
			//TODO: Evaluate important places like schools 
			for (Coordinate coordinate : trajectory.getCoordinates()) {
				System.out.println(coordinate);
				
				evaluator.evaluate(coordinate);
				TrajectoryTelemetry telemetry = evaluator.getCurrentTelemetry();
	
				System.out.println("Time: " + telemetry.trajectoryTime.getTime());
				System.out.println("Distance: " + telemetry.trajectoryDistance.getKilometers() + " km");
				
				System.out.println("AvgSpeed: " + String.format("%.2f km/h", telemetry.avgSpeed.getKmh()));
				System.out.println("MaxSpeed: " + String.format("%.2f km/h", telemetry.maxSpeed.getKmh()));
				
				System.out.println("MaxAcc: " + String.format("%.2f km/m²", telemetry.maxAcc.getKmPerMin2()));
				System.out.println("MaxDec: " + String.format("%.2f km/m²", telemetry.maxDec.getKmPerMin2()));
				System.out.println("AccCount: " + telemetry.accCount);
				System.out.println("DecCount: " + telemetry.decCount);
				
				System.out.println("OverMaxSpeed: " + telemetry.overMaxAllowedSpeedCount);
				System.out.println("OverMaxAcc: " + telemetry.overMaxSecureAccCount);
				System.out.println("OverMaxDec: " + telemetry.overMaxSecureDecCount);
				
				System.out.println("--");
			}
			
		}
	}
	
	public static void replayToService(String dirPath) {
		File dir = new File(dirPath);
		
		//https://jersey.java.net/documentation/latest/client.html
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
			track.userId = trajectory.getUserId();
			for (Coordinate coordinate : trajectory.getCoordinates()) {
				if(batchSize >= batchLimit) {
					response = target.path("services").path("track").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(track, MediaType.APPLICATION_JSON_TYPE));
					logger.info("Response: " + response.getStatus());
					track = new TrackDTO();
					track.deviceId = trajectory.getDeviceId();
					track.userId = trajectory.getUserId();
					batchSize = 0;
				} else {
					track.coordinates.add(TrajectoryMapper.toDto(coordinate));
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




