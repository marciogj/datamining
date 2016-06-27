package br.udesc.dcc.bdes.google.places;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.udesc.dcc.bdes.google.places.dto.PlacesDTO;


public class PlacesClient {
	public static final String GEOCODE_API = "https://maps.googleapis.com";

	public static Optional<PlacesDTO> getPlaces(double latitude, double longitude, int radius, String apiKey) {
		return getPlacesByTypes(latitude, longitude, radius, new ArrayList<String>(), apiKey);
	}
	
	//https://developers.google.com/places/supported_types#table1
	public static Optional<PlacesDTO> getPlacesByTypes(double latitude, double longitude, int radius, Collection<String> types, String apiKey) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(GEOCODE_API);
		target = target.path("maps").path("api").path("place").path("nearbysearch").path("json")
				.queryParam("location", latitude + "," + longitude)
				.queryParam("radius", radius)
				.queryParam("key", apiKey);
		
		if (!types.isEmpty()) { 
			target = target.queryParam("types", types.stream().collect(Collectors.joining("|")));
		}
		
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		//System.out.println(response.readEntity(String.class));
		return Optional.ofNullable(response.readEntity(PlacesDTO.class));
	}
}
