package br.udesc.dcc.bdes.google;

import br.udesc.dcc.bdes.google.dto.GeocodeAddressDTO;
import br.udesc.dcc.bdes.google.dto.InverseGeocodingResultDTO;

public class GeocodeAddress {
	private String street_address;
	private String sublocality_level_1;
	private String locality;
	private String administrative_area_level_2;
	private String administrative_area_level_1;
	private String country;
	
	public GeocodeAddress(GeocodeAddressDTO dto) {
		for(InverseGeocodingResultDTO result : dto.results) {
			if (result.types.contains("street_address") || result.types.contains("route")) {
				street_address = result.formatted_address;
			}
			if (result.types.contains("sublocality_level_1")) {
				sublocality_level_1 = result.formatted_address;
			}
			if (result.types.contains("locality")) {
				locality = result.formatted_address;
			}
			if (result.types.contains("administrative_area_level_2")) {
				administrative_area_level_2 = result.formatted_address;
			}
			if (result.types.contains("administrative_area_level_1")) {
				administrative_area_level_1 = result.formatted_address;
			}
			if (result.types.contains("country")) {
				country = result.formatted_address;
			}
		}
	}

	public String getStreetAddress() {
		return street_address;
	}

	public String getSublocalityLevel1() {
		return sublocality_level_1;
	}

	public String getLocality() {
		return locality;
	}

	public String getAdministrativeAreaLevel2() {
		return administrative_area_level_2;
	}

	public String getAdministrativeAreaLevel1() {
		return administrative_area_level_1;
	}

	public String getCountry() {
		return country;
	}
	
}
