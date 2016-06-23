package br.udesc.dcc.bdes.repository.mongo;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.JacksonMapper;
//import org.jongo.marshall.jackson.Builder;

import org.jongo.marshall.jackson.JacksonMapper.Builder;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
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
			
			
			Builder tmpMapper = new JacksonMapper.Builder();
	        for (Module module : ObjectMapper.findModules()) {
	            tmpMapper.registerModule(module);
	        }
	        tmpMapper.enable(MapperFeature.AUTO_DETECT_GETTERS);
	        tmpMapper.registerModule(new JSR310Module()).registerModule(new Jdk8Module());
			
			db = mongoClient.getDB(DB_NAME);
			//jongo = new Jongo(db);
			jongo = new Jongo(db, tmpMapper.build());
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
