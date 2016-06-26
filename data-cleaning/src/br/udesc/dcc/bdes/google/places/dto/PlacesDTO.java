package br.udesc.dcc.bdes.google.places.dto;

import java.util.ArrayList;
import java.util.Collection;

public class PlacesDTO {
	public Collection<PlaceResultDTO> results = new ArrayList<>();
	public String status;
	public String next_page_token;
	//"html_attributions" : [],
}