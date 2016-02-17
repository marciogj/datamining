package br.udesc.dcc.bdes.server.rest.api.track;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.udesc.dcc.bdes.analysis.TrajectoryEvaluation;
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
	public TrajectorySummaryDTO getLastTrajectorySummary(@PathParam("id") String userId) {
		TrajectoryEvaluation evaluation = repository.findTrajectoryEvaluationById(userId).orElseThrow( () -> new NotFoundException());
		return TrajectoryMapper.toDto(evaluation);
	}
}
