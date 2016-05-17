package br.udesc.dcc.bdes.repository.sql;

import java.sql.Connection;

public class Repository {
	protected Connection conn;
	
	public Repository(Connection conn) {
		this.conn = conn;
	}

}
