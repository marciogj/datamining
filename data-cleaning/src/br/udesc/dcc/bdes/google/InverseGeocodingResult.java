package br.udesc.dcc.bdes.google;

import java.util.LinkedList;
import java.util.List;

public class InverseGeocodingResult {
	public String place_id;
	public String formatted_address;
	public List<String> types = new LinkedList<>();
	public List<AddressComponentDTO> address_components = new LinkedList<>();
	public List<GeometryDTO> geometry = new LinkedList<>();
	
	
	
}
