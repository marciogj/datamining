package br.udesc.dcc.bdes.server.rest.api.track;

public class Alert {
	enum Type {
		SEVERE,
		VERY_SEVERE
	}
	
	private String message;
	private Type type;
	
	public Alert(Type type, String msg) {
		this.type = type;
		this.message = msg;
	}
	
	public static Alert severe(String msg) {
		return new Alert(Type.SEVERE, msg);
	}
	
	public static Alert verySevere(String msg) {
		return new Alert(Type.VERY_SEVERE, msg);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
}
