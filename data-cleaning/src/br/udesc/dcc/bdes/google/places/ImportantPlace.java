package br.udesc.dcc.bdes.google.places;

public class ImportantPlace {
	private String name;
	private String address;
	private String type;
	private Double lat;
	private Double lon;
	
	public ImportantPlace(){}		
	
	public ImportantPlace(String name, String address, String type, double lat, double lon) {
		super();
		this.name = name;
		this.address = address;
		this.type = type;
		this.lat = lat;
		this.lon = lon;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}
	
}
