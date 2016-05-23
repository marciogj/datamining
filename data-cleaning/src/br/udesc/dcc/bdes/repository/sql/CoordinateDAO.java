package br.udesc.dcc.bdes.repository.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.TrajectoryId;

public class CoordinateDAO extends Repository {
	
	public CoordinateDAO(Connection conn) {
		super(conn);
	}
	
	public void add(Coordinate coordinate, TrajectoryId trajectoryId) throws SQLException {
		String query = "INSERT INTO coordinates (trajectoryId, timestamp, latitude, longitude, altitude, speed) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(query);
			pst.setString(1, trajectoryId.getValue());
			pst.setTimestamp(2, new Timestamp(coordinate.getDateTimeInMillis()));
			pst.setDouble(3, coordinate.getLatitude());
			pst.setDouble(4, coordinate.getLongitude());
			pst.setDouble(5, coordinate.getAltitude());
			pst.setDouble(6, coordinate.getSpeed().get());
			pst.execute();
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
	}
	
	public List<Coordinate> loadAll(TrajectoryId trajectoryId) throws SQLException {
		String query = "SELECT timestamp, latitude, longitude, altitude, speed FROM drivers WHERE trajectoryId = ?";
		PreparedStatement pst = null;
		List<Coordinate> all = new LinkedList<>();
		try {
			pst = conn.prepareStatement(query);
			pst.setString(1, trajectoryId.getValue());
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
	
	
	private Coordinate toObject(ResultSet rs) throws SQLException {
		Coordinate obj = new Coordinate();
		obj.setLatitude(rs.getDouble("latitude"));
		obj.setLongitude(rs.getDouble("longitude"));
		obj.setAltitude(rs.getDouble("altitude"));
		obj.setSpeed(rs.getDouble("speed"));
		//TODO: Add timezone based data on the database side
		obj.setDateTime( rs.getTimestamp("timestamp").toLocalDateTime());
		return obj;
	}

}
