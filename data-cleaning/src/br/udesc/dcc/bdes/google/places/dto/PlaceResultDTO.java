package br.udesc.dcc.bdes.google.places.dto;

import java.util.ArrayList;
import java.util.Collection;

public class PlaceResultDTO {
	public String id;
	public String icon;
	public String name;
	public String place_id;
	public String scope;
	public String reference;
	public String vicinity;
	public GeometryDTO geometry;
	public OpeningHoursDTO opening_hours;
	public Collection<PhotoDTO> photos;
	public Collection<String> types = new ArrayList<>();
	public Collection<AltIdsDTO> alt_ids = new ArrayList<>(); 
}