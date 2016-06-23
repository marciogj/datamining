package br.udesc.dcc.bdes.server.rest.api.track;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.udesc.dcc.bdes.model.DriverProfile;
import br.udesc.dcc.bdes.model.DriverId;
import br.udesc.dcc.bdes.repository.memory.MemoryRepository;
import br.udesc.dcc.bdes.server.rest.APIPath;
import br.udesc.dcc.bdes.server.rest.api.track.dto.DriverProfileDTO;
import br.udesc.dcc.bdes.server.rest.api.track.dto.TrajectoryMapper;

@Path(APIPath.DRIVER_PROFILE)
public class DriverProfileAPI {
	private final MemoryRepository repository = MemoryRepository.get();
	private final Logger logger = Logger.getLogger("api");
	
	@GET
	@Path("{driverId}")
	@Produces(MediaType.APPLICATION_JSON)
	public DriverProfileDTO getDriverProfile(@PathParam("driverId") String driverId) {
		logger.info("getDriverProfile " + driverId);
		DriverProfile profile  = repository.loadDriverProfile(new DriverId(driverId)).orElseThrow( () -> new NotFoundException());
		return TrajectoryMapper.toDto(profile);
	}
}
