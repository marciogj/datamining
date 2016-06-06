package br.udesc.dcc.bdes.model;

import java.util.UUID;

public class Id {
	protected String id;
	
	public Id() {
		super();
		this.id = UUID.randomUUID().toString();
	}
	
	public Id(String id) {
		super();
		if(id == null) throw new NullPointerException();
		this.id = id;
	}

	public String getValue() {
		return id;
	}

	public void setValue(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof Id)) return false;
		Id other = (Id) obj;
		return id.equals( other.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	
	
	
}
