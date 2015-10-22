package br.udesc.dcc.bdes.gis;

import java.util.ArrayList;
import java.util.List;

public class Trajectory {
	protected List<Coordinate> coordinates = new ArrayList<>();
	protected String sourceProvider; //Geolife, UDESC
	protected String id; //taxi id, user
	
	public Trajectory() {}

	public void add(Coordinate coordinate) {
		if (coordinate != null) {
			coordinates.add(coordinate);
		}
	}

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public int size() {
		return coordinates.size();
	}

}
