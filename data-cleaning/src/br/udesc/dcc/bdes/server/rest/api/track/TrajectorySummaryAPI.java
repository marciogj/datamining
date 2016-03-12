package br.udesc.dcc.bdes.server.rest.api.track;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluation;
import br.udesc.dcc.bdes.server.model.DeviceId;
import br.udesc.dcc.bdes.server.repository.MemoryRepository;
import br.udesc.dcc.bdes.server.rest.APIPath;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectoryMapper;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectorySummaryDTO;

@Path(APIPath.SUMMARY)
public class TrajectorySummaryAPI {
	private final MemoryRepository repository = MemoryRepository.get();
	
	@GET
	@Path("trajectory/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public TrajectorySummaryDTO getLastTrajectorySummary(@PathParam("id") String deviceId) {
		TrajectoryEvaluation evaluation = repository.loadLatestTrajectoryEvaluationById(new DeviceId(deviceId)).orElseThrow( () -> new NotFoundException());
		return TrajectoryMapper.toDto(evaluation);
	}
	
	@GET
	@Path("trajectories/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TrajectorySummaryDTO> getTrajectoriesSummary(@PathParam("id") String deviceId) {
		List<TrajectoryEvaluation> evaluation = repository.loadTrajectoriesEvaluationById(new DeviceId(deviceId));
				
		//Fn.transform(evaluation, TrajectoryMapper::toDto);
		return evaluation.stream().map(TrajectoryMapper::toDto).collect(Collectors.toList());
	}
}
