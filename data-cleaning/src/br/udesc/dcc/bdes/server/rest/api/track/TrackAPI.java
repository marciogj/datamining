package br.udesc.dcc.bdes.server.rest.api.track;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluation;
import br.udesc.dcc.bdes.gis.Trajectory;
import br.udesc.dcc.bdes.openweather.OpenWeatherClient;
import br.udesc.dcc.bdes.openweather.OpenWeatherConditionDTO;
import br.udesc.dcc.bdes.server.JettyServer;
import br.udesc.dcc.bdes.server.repository.MemoryRepository;
import br.udesc.dcc.bdes.server.rest.APIPath;
import br.udesc.dcc.bdes.server.rest.api.track.dto.CoordinateDTO;
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
		
		TrajectoryEvaluation trajectoryEval = evaluateTrack(trackDto);
		repository.save(trackDto.userId, trajectoryEval);
		
		notifyWSClients(trackDto);			
    }
	
	private TrajectoryEvaluation evaluateTrack(TrackDTO trackDto) {
		TrajectoryEvaluation trajectoryEval = new TrajectoryEvaluation();
		Optional<TrajectoryEvaluation> optTrajectory = repository.findTrajectoryEvaluationById(trackDto.userId);
		if (optTrajectory.isPresent()) {
			//TODO: Break trajectories considering contextual information: stops and place
			//load from repository (as time goes on, we should split trajectories...)
			//take the last one and evaluate:
			// is it the same road (taken from google API)?
			// how much time elapsed from last one
			trajectoryEval = optTrajectory.get();
		}
		
		Trajectory receivedTrajectory = TrajectoryMapper.fromDto(trackDto);
		Optional<OpenWeatherConditionDTO> currentWeather = getLastPositionWeather(trackDto);
		trajectoryEval.evaluate(receivedTrajectory.getCoordinates(), currentWeather);
		
		return trajectoryEval;
		
	}
	
	private Optional<OpenWeatherConditionDTO> getLastPositionWeather(TrackDTO trackDto) {
		Optional<String> openWetaherKey = JettyServer.get().getOpenWeatherKey();
		if (openWetaherKey.isPresent()) {
			CoordinateDTO lastCoord = trackDto.coordinates.get(trackDto.coordinates.size()-1);
			return OpenWeatherClient.weatherByCooordinate(lastCoord.latitude, lastCoord.longitude, openWetaherKey.get());
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