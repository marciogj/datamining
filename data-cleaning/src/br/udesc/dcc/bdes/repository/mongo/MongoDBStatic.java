package br.udesc.dcc.bdes.repository.mongo;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.DB;
import com.mongodb.MongoClient;

@SuppressWarnings("deprecation")
public abstract class MongoDBStatic {

	private static String DB_HOST;
	private static int DB_PORT;
	private static String DB_USER;
	private static String DB_PASSWD;
	private static String DB_NAME;

	private static MongoClient mongoClient;
	private static DB db;
	private static Jongo jongo;

	static {
		DB_HOST = "localhost";
		DB_PORT = 27017;
		DB_NAME = "driver-profile-analytics";

		DB_USER = System.getProperty("DB_USER");
		if (DB_USER == null) {
			DB_USER = "";
		}

		DB_PASSWD = System.getProperty("DB_PASSWD");
		if (DB_PASSWD == null) {
			DB_PASSWD = "";
		}

		try {
			mongoClient = new MongoClient(DB_HOST, DB_PORT);
			db = mongoClient.getDB(DB_NAME);
			jongo = new Jongo(db);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

	public static MongoCollection getCollection(String collectionName) {
		return jongo.getCollection(collectionName);
	}

	private MongoDBStatic() {

	}
}
