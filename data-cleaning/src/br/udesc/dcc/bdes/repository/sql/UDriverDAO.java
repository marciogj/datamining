package br.udesc.dcc.bdes.repository.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.Driver;
import br.udesc.dcc.bdes.model.DriverId;

public class UDriverDAO extends Repository {
	
	public UDriverDAO(Connection conn) {
		super(conn);
	}
	
	public void add(Driver driver) throws SQLException {
		String query = "INSERT INTO drivers (id, name, deviceId) VALUES (?, ?, ?)";
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(query);
			pst.setString(1, driver.getId().getValue());
			pst.setString(2, driver.getName());
			pst.setString(3, driver.getDeviceId().getValue());
			pst.execute();
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
	}
	
	public List<Driver> loadAll() throws SQLException {
		String query = "SELECT id, name, deviceId FROM drivers";
		PreparedStatement pst = null;
		List<Driver> all = new LinkedList<>();
		try {
			pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery(query);
			while(rs.next()) {
				all.add(toObject(rs));
			}
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
		return all;
	}
	
	public Optional<Driver> loadById(DriverId id) throws SQLException {
		String query = "SELECT id, name, deviceId FROM drivers WHERE id = ?";
		PreparedStatement pst = null;
		Driver driver = null;
		try {
			pst = conn.prepareStatement(query);
			pst.setString(1, id.getValue());
			ResultSet rs = pst.executeQuery(query);
			while(rs.next()) {
				driver = toObject(rs);
			}
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
		return Optional.ofNullable(driver);
	}
	
	private Driver toObject(ResultSet rs) throws SQLException {
		Driver obj = new Driver();
		obj.setId(new DriverId(rs.getString("id")));
		obj.setName(rs.getString("name"));
		obj.setDeviceId(new DeviceId(rs.getString("deviceId")));
		return obj;
	}

}
