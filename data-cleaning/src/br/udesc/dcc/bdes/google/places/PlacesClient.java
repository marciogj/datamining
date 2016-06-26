package br.udesc.dcc.bdes.google.places;

import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.udesc.dcc.bdes.google.places.dto.PlacesDTO;


public class PlacesClient {
	public static final String GEOCODE_API = "https://maps.googleapis.com";

	public static Optional<PlacesDTO> getPlaces(double latitude, double longitude, int radius, String apiKey) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(GEOCODE_API);
		Response response = target.path("maps").path("api").path("place").path("nearbysearch").path("json")
				.queryParam("location", latitude + "," + longitude)
				.queryParam("radius", radius)
				.queryParam("key", apiKey)
				.request(MediaType.APPLICATION_JSON).get();
		//System.out.println(response.readEntity(String.class));
		return Optional.ofNullable(response.readEntity(PlacesDTO.class));
	}
//	
//	public static Optional<GeocodeAddress> getAddresses(double latitude, double longitude, String apiKey) {
//		Optional<GeocodeAddressDTO> address = geAddress(latitude, longitude, apiKey);
//		if (address.isPresent() && !address.get().results.isEmpty()) {
//			return Optional.of(new GeocodeAddress(address.get()));
//		}
//		return Optional.empty();
//	}

}
