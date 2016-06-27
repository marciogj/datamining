package br.udesc.dcc.bdes.google.places;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import br.udesc.dcc.bdes.google.places.dto.PlaceResultDTO;
import br.udesc.dcc.bdes.google.places.dto.PlacesDTO;


public class ImportantPlacesClient {
	public static final Collection<String> types = Arrays.asList(new String[] {"hospital","embassy","school","university","police"});
	
	public static List<ImportantPlace> getPlaces(double latitude, double longitude, int radius, String apiKey) {
		List<ImportantPlace> places = new ArrayList<>();
		Optional<PlacesDTO> dto = PlacesClient.getPlacesByTypes(latitude, longitude, radius, types, apiKey);
		if (!dto.isPresent()) {
			return places;
		}
		Collection<PlaceResultDTO> results = dto.get().results;
		results.forEach( placeDto -> {
			places.add(new ImportantPlace(placeDto.name, placeDto.vicinity, placeDto.types.stream().findFirst().orElse("unknown")));
		});
		return places;
	}
}
