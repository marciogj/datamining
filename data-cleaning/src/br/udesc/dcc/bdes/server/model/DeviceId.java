package br.udesc.dcc.bdes.server.model;

import java.util.UUID;

public class DeviceId {
	private String id;
	
	public DeviceId() {
		super();
		this.id = UUID.randomUUID().toString();
	}
	
	public DeviceId(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof DeviceId)) return false;
		DeviceId other = (DeviceId) obj;
		return id.equals( other.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	
	
	
}
