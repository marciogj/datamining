package br.udesc.dcc.bdes.server.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import br.udesc.dcc.bdes.server.model.UDriver;

public class UDriverDAO {
	
	public void add(UDriver driver) {
		Connection conn = DBPool.getConnection().orElseThrow( () -> new RuntimeException("Could not allocate a new connection"));
		String query = "INSERT INTO drivers (id, name, deviceId) VALUES (?, ?, ?)";
		try {
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, driver.getId().getValue());
			pst.setString(2, driver.getName());
			pst.setString(3, driver.getDeviceId().getValue());
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error executing query: " + query);
		} finally {
			DBPool.release(conn);
		}
		
	}

}
