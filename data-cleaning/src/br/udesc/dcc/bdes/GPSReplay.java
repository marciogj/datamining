package br.udesc.dcc.bdes;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.io.SeniorCSVFileReader;
import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.model.TrajectoryEvaluation;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrackDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectoryMapper;


public class GPSReplay {
	public static final String SERVER_URL = "http://localhost:9090";
	private static final Logger logger = Logger.getLogger("GPSReplay");

	public static void main(String[] args) {
		System.out.println("Simulating GPS coordinates from recorded files...");
		//String dirPath = "C:\\Users\\marciogj\\SkyDrive\\gps-tracker-service\\001";
		//String baseDir = "C:\\Users\\marciogj\\SkyDrive\\GPS_DATA\\GPSTracker\\";
		Locale.setDefault(Locale.US);
		String baseDir = "C:\\Users\\marciogj\\SkyDrive\\GPS_DATA\\Evaluation\\";
		File dir = new File(baseDir);
		
		for (File subdir : dir.listFiles()) {
			if (subdir.isDirectory()) {
				Trajectory trajectory = readAllFiles(subdir.getAbsolutePath());
				replayToService(trajectory);
			}
		}
		
	}
	
	public static void replayToEvaluator(String[] dirPath) {
		for(String path : dirPath) {
			Trajectory trajectory = readAllFiles(path);
			replayToService(trajectory);
		}
	}

	public static void replayToEvaluator(String dirPath) {
		File dir = new File(dirPath);
		TrajectoryEvaluator evaluator = new TrajectoryEvaluator(13.89, 6.95, -4.17);
		for(String file : dir.list()) {
			Trajectory trajectory = SeniorCSVFileReader.read(dirPath+"\\"+file);

			//TMP
			trajectory.setUserId("moto-x");
			
			//TODO: Evaluate track changes 
			//TODO: Evaluate important places like schools 
			for (Coordinate coordinate : trajectory.getCoordinates()) {
				System.out.println(coordinate);

				evaluator.evaluate(coordinate);
				TrajectoryEvaluation telemetry = evaluator.getCurrentTelemetry();

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

	public static void healthCheck() {
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
	}
	

	public static void replayToService(String dirPath) {
		healthCheck();
		File dir = new File(dirPath);
		for(String file : dir.list()) {
			Trajectory trajectory = SeniorCSVFileReader.read(dirPath+"\\"+file);
			replayToService(trajectory);
		}
	}

	public static void replayToService(Trajectory trajectory) {
		healthCheck();
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(SERVER_URL);
		int batchLimit = 50;
		int batchSize = 0;
		TrackDTO track = new TrackDTO();
		track.deviceId = trajectory.getDeviceId();
		track.userId = trajectory.getUserId();
		
		Invocation.Builder http = target.path("services").path("track").path("evaluate").request(MediaType.APPLICATION_JSON_TYPE);
		
		for (Coordinate coordinate : trajectory.getCoordinates()) {
			if(batchSize >= batchLimit) {
				Response response = http.post(Entity.entity(track, MediaType.APPLICATION_JSON_TYPE));
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
			Response response = http.post(Entity.entity(track, MediaType.APPLICATION_JSON_TYPE));
			logger.info("Response: " + response.getStatus() ) ;
		}
	}

	//Some files are not sorted so this might be needed to process in a real time frame
	public static Trajectory readAllFiles(String dirPath) {
		File dir = new File(dirPath);
		Trajectory trajectory = new Trajectory();
		logger.info("Dir: " + dirPath);
		String[] files = dir.list();
		for(String file : files) {
			if (file.contains(".csv")) {
				Trajectory subTrajectory = SeniorCSVFileReader.read(dirPath+"\\"+file);
				trajectory.addAll(subTrajectory.getCoordinates());
				trajectory.setDeviceId(subTrajectory.getDeviceId());
				trajectory.setUserId(trajectory.getUserId());
			}
		}

		Collections.sort(trajectory.getCoordinates(), new Comparator<Coordinate>(){
			public int compare(Coordinate c1, Coordinate c2){
				return c1.getDateTime().compareTo(c2.getDateTime());
			}
		});

		return trajectory;
	}

}




