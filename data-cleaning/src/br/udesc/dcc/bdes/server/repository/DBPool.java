package br.udesc.dcc.bdes.server.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class DBPool {
	public static final String HOST = "localhost";
	public static final String USER = "root";
	public static final String PASSWORD = "";
	public static final String SCHEMA = "driver-profile";
	public static final int MAX_POOL_SIZE = 50;
	private static DBPool instance;
	
	private List<Connection> availableConn = Collections.synchronizedList(new ArrayList<>());
	private List<Connection> usedConn = Collections.synchronizedList(new ArrayList<>());

	public static DBPool get() {
		if (instance == null) {
			instance = new DBPool();
		}
		return instance;
	}
	
	public static synchronized Optional<Connection> getConnection() {
		return get().obtainConnection();
	}
	
	public synchronized Optional<Connection> obtainConnection() {
		System.out.println("Used Conn: " + usedConn.size());
		System.out.println("Available Conn: " + availableConn.size());
		boolean isPoolOpen = usedConn.size() < MAX_POOL_SIZE;
		if (!isPoolOpen) {
			System.out.println("Pool is full. Cannot afford more connections.");
		} else if (availableConn.isEmpty()) {
			Optional<Connection> optConn = this.newConnection();
			if (optConn.isPresent()) {
				usedConn.add(optConn.get());
				return Optional.of(optConn.get());
			}
		} else {
			Connection conn = availableConn.get(0);
			usedConn.add(conn);
			return Optional.of(conn);
		}
		return Optional.empty();
	}
	
	public static synchronized void release(Connection conn) {
		get().releaseConnection(conn);
	}
	
	public synchronized void releaseConnection(Connection conn) {
		usedConn.remove(conn);
		availableConn.add(conn);
	}
	
	private Optional<Connection> newConnection() {
		Connection conn = null;
		try {
			String mysqlUrl = "jdbc:mysql://" + HOST + "/" + SCHEMA + "?user=" +USER + "&password="+ PASSWORD;
		    conn = DriverManager.getConnection(mysqlUrl);
		    return Optional.of(conn);
		} catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return Optional.empty();
	}
	
}
