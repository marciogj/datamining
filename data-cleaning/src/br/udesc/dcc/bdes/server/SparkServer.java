package br.udesc.dcc.bdes.server;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.Date;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class SparkServer {
	public static final int SERVICES_PORT = 8080;
	
	private final Logger logger = Logger.getLogger("GPSSparkServer");
	private final Gson gson = new Gson();
	
	public static void main(String[] args) {
		new SparkServer();
	}
	

	public SparkServer() {
		port(SERVICES_PORT);
		
		get("/ping", (request, response) -> {
			return String.format("Server is up and running! Server date and time %tc ", new Date());
		});

		post("/track", (request, response) -> {
			GPSTrack track = gson.fromJson(request.body(), GPSTrack.class);
			
			logger.info("Received: " + request.body());
			return String.format("Got a message from %s", track.getUserId());
			
		});
	}


}
