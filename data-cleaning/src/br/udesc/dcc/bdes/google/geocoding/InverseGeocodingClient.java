package br.udesc.dcc.bdes.google.geocoding;

import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.udesc.dcc.bdes.google.geocoding.dto.GeocodeAddressDTO;


public class InverseGeocodingClient {
	public static final String GEOCODE_API = "https://maps.googleapis.com";
	
	public static Optional<GeocodeAddressDTO> getAddress(double latitude, double longitude, String apiKey) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(GEOCODE_API);
		Response response = target.path("maps").path("api").path("geocode").path("json")
				.queryParam("latlng", latitude + "," + longitude)
				.queryParam("key", apiKey)
				.request(MediaType.APPLICATION_JSON).get();
		//System.out.println("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude+"&key="+apiKey);
		//System.out.println(response.readEntity(String.class));
		GeocodeAddressDTO address = response.readEntity(GeocodeAddressDTO.class);
		return Optional.of(address);
	}
	
	public static Optional<GeocodeAddress> getAddresses(double latitude, double longitude, String apiKey) {
		Optional<GeocodeAddressDTO> address = getAddress(latitude, longitude, apiKey);
		if (address.isPresent() && !address.get().results.isEmpty()) {
			return Optional.of(new GeocodeAddress(address.get()));
		}
		return Optional.empty();
	}

}
