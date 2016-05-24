package br.udesc.dcc.bdes.google;

import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class InverseGeocodingClient {
	public static final String GEOCODE_API = "https://maps.googleapis.com";
	
	public static Optional<GeocodeAddressDTO> address(double latitude, double longitude, String apiKey) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(GEOCODE_API);
		Response response = target.path("maps").path("api").path("geocode").path("json")
				.queryParam("latlng", latitude + "," + longitude)
				.queryParam("key", apiKey)
				.request(MediaType.APPLICATION_JSON).get();
		
		GeocodeAddressDTO address = response.readEntity(GeocodeAddressDTO.class);
		return Optional.of(address);
		
	}

}
