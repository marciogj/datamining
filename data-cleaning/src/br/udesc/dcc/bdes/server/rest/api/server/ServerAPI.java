package br.udesc.dcc.bdes.server.rest.api.server;

import java.time.LocalDateTime;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.udesc.dcc.bdes.server.rest.APIPath;

@Path(APIPath.SERVER)
public class ServerAPI {
	
	@GET
    @Path("ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "Server is up and running! Server date and time " + LocalDateTime.now();
    }
	
}