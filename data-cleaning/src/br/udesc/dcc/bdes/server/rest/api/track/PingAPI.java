package br.udesc.dcc.bdes.server.rest.api.track;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.udesc.dcc.bdes.server.rest.APIPath;

import com.google.gson.Gson;

@Path(APIPath.PING)
public class PingAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String ping() {
		Gson gson = new Gson();
		return gson.toJson("Server is up and running " + new Date()); 
	}
}
