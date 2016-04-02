package br.udesc.dcc.bdes.server.rest.api.track;

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
import br.udesc.dcc.bdes.server.model.DeviceId;
import br.udesc.dcc.bdes.server.repository.MemoryRepository;
import br.udesc.dcc.bdes.server.rest.APIPath;
import br.udesc.dcc.bdes.server.rest.api.track.dto.SpeedTelemetryDTO;
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
		TrajectoryEvaluator evaluation = repository.loadTrajectoryEvaluationById(evaluationId).orElseThrow( () -> new NotFoundException());
		return TrajectoryMapper.toDto(evaluation);
	}
	
	@GET
	@Path("trajectories/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TrajectorySummaryDTO> getTrajectoriesSummary(@PathParam("id") String deviceId) {
		//logger.info("getTrajectoriesSummary " + deviceId);
		List<TrajectoryEvaluator> evaluation = repository.loadTrajectoriesEvaluationById(new DeviceId(deviceId));
				
		//Fn.transform(evaluation, TrajectoryMapper::toDto);
		return evaluation.stream().map(TrajectoryMapper::toDto).collect(Collectors.toList());
	}
	
	@GET
	@Path("trajectories/telemetry/speed/{trajectoryEvaluationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public SpeedTelemetryDTO getTrajectoriesSpeedTelemetry(@PathParam("trajectoryEvaluationId") String trajectoryEvaluationId) {
		logger.info("getTrajectoriesSpeedTelemetry " + trajectoryEvaluationId);
		TrajectoryEvaluator evaluation = repository.loadTrajectoryEvaluationById(trajectoryEvaluationId).orElseThrow( () -> new NotFoundException());
		return TrajectoryMapper.toDto(evaluation.getTrajectory().getCoordinates());
	}
}
