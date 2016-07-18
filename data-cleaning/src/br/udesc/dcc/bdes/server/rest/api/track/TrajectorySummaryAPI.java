package br.udesc.dcc.bdes.server.rest.api.track;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluator;
import br.udesc.dcc.bdes.analysis.TrajectoryEvaluatorId;
import br.udesc.dcc.bdes.google.places.ImportantPlace;
import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.Distance;
import br.udesc.dcc.bdes.model.Time;
import br.udesc.dcc.bdes.repository.memory.MemoryRepository;
import br.udesc.dcc.bdes.server.rest.APIPath;
import br.udesc.dcc.bdes.server.rest.api.track.dto.ImportantPlaceDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.PenaltyAlertDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.SpeedTelemetryDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectoryDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectoryMapper;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectorySummaryDTO;

@Path(APIPath.SUMMARY)
public class TrajectorySummaryAPI {
	private final MemoryRepository repository = MemoryRepository.get();
	private final Logger logger = Logger.getLogger("api");
	
	@Deprecated
	@GET
	@Path("trajectory/{deviceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public TrajectorySummaryDTO getLastTrajectorySummary(@PathParam("deviceId") String deviceId) {
		//logger.info("getLastTrajectorySummary " + deviceId);
		TrajectoryEvaluator evaluation = repository.loadLatestTrajectoryEvaluationById(new DeviceId(deviceId)).orElseThrow( () -> new NotFoundException());
		return TrajectoryMapper.toDto(evaluation);
	}
	
	@GET
	@Path("trajectory-evaluation/{evaluationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public TrajectorySummaryDTO getTrajectoryEvaluation(@PathParam("evaluationId") String evaluationId) {
		logger.info("getTrajectoryEvaluation " + evaluationId);
		TrajectoryEvaluator evaluator = repository.loadTrajectoryEvaluationById(new TrajectoryEvaluatorId(evaluationId)).orElseThrow( () -> new NotFoundException());
		return TrajectoryMapper.toDto(evaluator);
	}
	
	@GET
	@Path("trajectory-evaluation/{evaluationId}/coordinates")
	@Produces(MediaType.APPLICATION_JSON)
	public TrajectoryDTO getTrajectoryEvaluationCoordinates(@PathParam("evaluationId") String evaluationId) {
		logger.info("getTrajectoryEvaluationCoordinates " + evaluationId);
		TrajectoryEvaluator evaluation = repository.loadTrajectoryEvaluationById(new TrajectoryEvaluatorId(evaluationId)).orElseThrow( () -> new NotFoundException());
		return TrajectoryMapper.toDto(evaluation.getTrajectory());
	}
	
	@GET
	@Path("trajectory-evaluation/{evaluationId}/important-places")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ImportantPlaceDTO> getImportantPlaces(@PathParam("evaluationId") String evaluationId) {
		logger.info("getTrajectoryImportantPlaces " + evaluationId);
		TrajectoryEvaluator evaluation = repository.loadTrajectoryEvaluationById(new TrajectoryEvaluatorId(evaluationId)).orElseThrow( () -> new NotFoundException());
		return TrajectoryMapper.toPlaceDto(evaluation.getImportantPlaces());
	}
	
	@GET
	@Path("trajectory-evaluation/{evaluationId}/important-places-traveled")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ImportantPlaceDTO> getTrajectoryImportantPlaces(@PathParam("evaluationId") String evaluationId) {
		logger.info("getTrajectoryImportantPlaces " + evaluationId);
		TrajectoryEvaluator evaluation = repository.loadTrajectoryEvaluationById(new TrajectoryEvaluatorId(evaluationId)).orElseThrow( () -> new NotFoundException());
		List<ImportantPlaceDTO> nearByPlaces =TrajectoryMapper.toPlaceDto(evaluation.getImportantPlaces());
		List<String> streets = evaluation.getStreets().stream().map( street -> cleanAddress(street)).collect(Collectors.toList());
		
		System.out.println("====================");
		for (String string : streets) {
			System.out.println(cleanAddress(string));
		}
		System.out.println("====================");
		
		//---
		
		System.out.println("*****************");
		for(ImportantPlaceDTO place : nearByPlaces) {
			String placeStreet = cleanAddress(place.address);
			place.isTravelled = streets.contains(placeStreet);
			System.out.println(cleanAddress(place.address));
		}
		System.out.println("*****************");
		return nearByPlaces;
	}
	
	private String cleanAddress(String address) {
		String streetName = address.split(",")[0];
		streetName = streetName.replaceAll("Rua ", "");
		streetName = streetName.replaceAll("R. ", "");
		streetName = streetName.replaceAll("R ", "");
		streetName = streetName.replaceAll("Doutor", "Dr.");
		
		return streetName;
	}
	
	
	@GET
	@Path("trajectory-evaluation/{evaluationId}/alerts")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PenaltyAlertDTO> getTrajectoryEvaluationAlerts(@PathParam("evaluationId") String evaluationId) {
		logger.info("getTrajectoryEvaluationCoordinates " + evaluationId);
		TrajectoryEvaluator evaluation = repository.loadTrajectoryEvaluationById(new TrajectoryEvaluatorId(evaluationId)).orElseThrow( () -> new NotFoundException());
		return evaluation.getAlerts().stream().map(alert ->  TrajectoryMapper.toDto(alert)).collect(Collectors.toList());
	}
	
	@GET
	@Path("trajectories/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TrajectorySummaryDTO> getTrajectoriesSummary(@PathParam("id") String deviceId) {
		//logger.info("getTrajectoriesSummary " + deviceId);
		List<TrajectoryEvaluator> evaluation = repository.loadTrajectoriesEvaluationById(new DeviceId(deviceId));
		double distance = 0;
		long time = 0;
		for (TrajectoryEvaluator eval : evaluation) {
			distance += eval.getTotalDistance();
			time += eval.getTotalTime();
		}
		Distance d = new Distance(distance);
		Time t = new Time(time);
		System.out.println("Tempo: " + t.getTime());
		System.out.println("Trajetorias: " + evaluation.size());
		System.out.println("Distancia: " + d.getKilometers() + " km");
 		
		//Fn.transform(evaluation, TrajectoryMapper::toDto);
		//return evaluation.stream().map(TrajectoryMapper::toDto).collect(Collectors.toList());
		return evaluation.stream().map(eval -> TrajectoryMapper.toDto(eval)).filter(dto -> dto.coordinateCount > 10).collect(Collectors.toList());
	}
	
	@GET
	@Path("trajectories/telemetry/speed/{trajectoryEvaluationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public SpeedTelemetryDTO getTrajectoriesSpeedTelemetry(@PathParam("trajectoryEvaluationId") String trajectoryEvaluationId) {
		logger.info("getTrajectoriesSpeedTelemetry " + trajectoryEvaluationId);
		TrajectoryEvaluator evaluation = repository.loadTrajectoryEvaluationById(new TrajectoryEvaluatorId(trajectoryEvaluationId)).orElseThrow( () -> new NotFoundException());
		return TrajectoryMapper.toDto(evaluation.getTrajectory().getCoordinates());
	}
}
