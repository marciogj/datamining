package br.udesc.dcc.bdes.repository.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import br.udesc.dcc.bdes.model.Coordinate;
import br.udesc.dcc.bdes.model.Trajectory;
import br.udesc.dcc.bdes.model.TrajectoryId;
import br.udesc.dcc.bdes.model.UDriverId;

public class TrajectoryDAO extends Repository {
	
	public TrajectoryDAO(Connection conn) {
		super(conn);
	}
	
	public void add(Trajectory trajectory) throws SQLException {
		String query = "INSERT INTO trajectories (id, source, driverId) VALUES (?, ?, ?)";
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(query);
			pst.setString(1, trajectory.getId().getValue());
			pst.setString(2, trajectory.getSourceProvider());
			//pst.setString(3, trajectory.getUserId());
			pst.setString(3, "8aa83efa-8eb7-4e8d-a052-d7d4d360687d");
			pst.execute();
			
			CoordinateDAO coordsDao = new CoordinateDAO(conn);
			for(Coordinate coord : trajectory.getCoordinates()) {
				coordsDao.add(coord, trajectory.getId());
			}
			
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
	}
	
	public List<Trajectory> loadAllByDriverId(UDriverId driverId) throws SQLException {
		String query = "SELECT id, source, driverId FROM trajectories WHERE driverId = ?";
		PreparedStatement pst = null;
		List<Trajectory> all = new LinkedList<>();
		try {
			pst = conn.prepareStatement(query);
			pst.setString(1, driverId.getValue());
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
	
	public Optional<Trajectory> loadAllById(UDriverId driverId) throws SQLException {
		String query = "SELECT id, source, driverId FROM trajectories WHERE driverId = ?";
		PreparedStatement pst = null;
		Trajectory trajectory = null;
		try {
			pst = conn.prepareStatement(query);
			pst.setString(1, driverId.getValue());
			ResultSet rs = pst.executeQuery(query);
			trajectory = toObject(rs);
			CoordinateDAO coordsDao = new CoordinateDAO(conn);
			trajectory.addAll(coordsDao.loadAll(trajectory.getId()));
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
		return Optional.ofNullable(trajectory);
	}
	
	private Trajectory toObject(ResultSet rs) throws SQLException {
		Trajectory obj = new Trajectory();
		obj.setId(new TrajectoryId(rs.getString("id")));
		obj.setSourceProvider(rs.getString("source"));
		obj.setUserId(rs.getString("driverId"));
		return obj;
	}
	

}
