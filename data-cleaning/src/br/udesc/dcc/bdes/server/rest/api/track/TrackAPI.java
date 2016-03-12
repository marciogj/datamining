package br.udesc.dcc.bdes.server.rest.api.track;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluation;
import br.udesc.dcc.bdes.gis.Coordinate;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.openweather.OpenWeatherClient;
import br.udesc.dcc.bdes.openweather.OpenWeatherConditionDTO;
import br.udesc.dcc.bdes.server.JettyServer;
import br.udesc.dcc.bdes.server.model.DeviceId;
import br.udesc.dcc.bdes.server.repository.MemoryRepository;
import br.udesc.dcc.bdes.server.rest.APIPath;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrackDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectoryMapper;

import com.google.gson.Gson;

@Path(APIPath.TRACK)
public class TrackAPI {
	private final Gson gson = new Gson();
	private final MemoryRepository repository = MemoryRepository.get();
	
	/**
	 * 
	 * curl -X POST -H "Content-Type: application/json" http://localhost:9090/services/track -d 
	 *   '{ "deviceId": "aaaa", "userId": "bbb", "coordinates": [ 
	 *      { "latitude": "0", "longitude": "1" }, 
	 *      { "latitude": "3", "longitude": "4"}  
	 *    ]}'
	 * 
	 * @param trackDto
	 */
	@POST
    public void postTrack(TrackDTO trackDto) {
		System.out.println("DeviceId: " + trackDto.deviceId + " - Coordinates: " + trackDto.coordinates.size());
		
		evaluateTrack(trackDto);
		
		
		notifyWSClients(trackDto);			
    }
	
	private void evaluateTrack(TrackDTO trackDto) {
		TrajectoryEvaluation trajectoryEval = new TrajectoryEvaluation();
		Trajectory receivedTrajectory = TrajectoryMapper.fromDto(trackDto);
		
		Optional<TrajectoryEvaluation> optTrajectory = repository.loadLatestTrajectoryEvaluationById(new DeviceId(trackDto.deviceId));
		long timeTolerance =  1000 * 60 * 5;
		
		if (optTrajectory.isPresent()) {
			//TODO: Break trajectories considering contextual information: stops and place
			Trajectory previousTrajectory = optTrajectory.get().getTrajectory();
			Optional<Coordinate> latestCoodrinate = previousTrajectory.getLastestCoordinate();
			Optional<Coordinate> receivedCoordinate = receivedTrajectory.getFirstCoordintae();
			
			long lastTimePreviousCoord = receivedCoordinate.isPresent() ? receivedCoordinate.get().getDateTimeInMillis() : 0;
			long firstTimeCurrentCoord = latestCoodrinate.isPresent() ? latestCoodrinate.get().getDateTimeInMillis() : 0;
			long difference = firstTimeCurrentCoord - lastTimePreviousCoord;
			//Evaluates whether it is the same trajectory or a new one
			if (difference <= timeTolerance) {
				trajectoryEval = optTrajectory.get();
			}
		}
		
		List<Trajectory> subtrajectoriesByTime = trajectoryEval.subtrajectoriesByTime(receivedTrajectory, timeTolerance);
		System.out.println("Subtrajectories: " + subtrajectoriesByTime.size());
		
		for (Trajectory subTrajectory : subtrajectoriesByTime) {
			Optional<OpenWeatherConditionDTO> currentWeather = getLastPositionWeather(subTrajectory);
			trajectoryEval.evaluate(receivedTrajectory.getCoordinates(), currentWeather);
			repository.save(new DeviceId(trackDto.deviceId), trajectoryEval);
			trajectoryEval = new TrajectoryEvaluation();
		}
		
	}
	
	private Optional<OpenWeatherConditionDTO> getLastPositionWeather(Trajectory trajectory) {
		Optional<String> openWetaherKey = JettyServer.get().getOpenWeatherKey();
		Optional<Coordinate> lastCoord = trajectory.getLastestCoordinate();
		if (openWetaherKey.isPresent() && lastCoord.isPresent()) {
			return OpenWeatherClient.weatherByCooordinate(lastCoord.get().getLatitude(), lastCoord.get().getLongitude(), openWetaherKey.get());
		}
		return Optional.empty();
	}
	
	private void notifyWSClients(TrackDTO trackDto) {
		JettyServer server = JettyServer.get();
		List<Session> sessions = server.getRegisteredSessions();
		sessions.forEach( session -> {
			System.out.println("Sending message to WebSocket session");
			RemoteEndpoint remote = session.getRemote();
			remote.sendStringByFuture(gson.toJson(trackDto));
		});
	}
	
	
	
} 