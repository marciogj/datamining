package br.udesc.dcc.bdes.google.places;

public class ImportantPlace {
	private String name;
	private String address;
	private String type;
	
	public ImportantPlace(){}		
	
	public ImportantPlace(String name, String address, String type) {
		super();
		this.name = name;
		this.address = address;
		this.type = type;
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

}
