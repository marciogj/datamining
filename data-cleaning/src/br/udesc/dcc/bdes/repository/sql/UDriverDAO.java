package br.udesc.dcc.bdes.repository.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.UDriver;
import br.udesc.dcc.bdes.model.UDriverId;

public class UDriverDAO extends Repository {
	
	public UDriverDAO(Connection conn) {
		super(conn);
	}
	
	public void add(UDriver driver) throws SQLException {
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
	
	public List<UDriver> loadAll() throws SQLException {
		String query = "SELECT id, name, deviceId FROM drivers";
		PreparedStatement pst = null;
		List<UDriver> all = new LinkedList<>();
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
	
	public Optional<UDriver> loadById(UDriverId id) throws SQLException {
		String query = "SELECT id, name, deviceId FROM drivers WHERE id = ?";
		PreparedStatement pst = null;
		UDriver driver = null;
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
	
	private UDriver toObject(ResultSet rs) throws SQLException {
		UDriver obj = new UDriver();
		obj.setId(new UDriverId(rs.getString("id")));
		obj.setName(rs.getString("name"));
		obj.setDeviceId(new DeviceId(rs.getString("deviceId")));
		return obj;
	}

}
