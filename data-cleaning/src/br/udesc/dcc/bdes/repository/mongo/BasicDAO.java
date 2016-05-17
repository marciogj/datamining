package br.udesc.dcc.bdes.repository.mongo;

import java.util.ArrayList;
import java.util.List;

import org.jongo.MongoCollection;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

public abstract class BasicDAO<T> {
	protected String collectionName;
	protected String keyField;
	
	 /**
     * http://docs.mongodb.org/manual/reference/method/db.collection.ensureIndex/
     */
    protected MongoCollection getIndexedCollection() {
        MongoCollection collection  = MongoDBStatic.getCollection(collectionName);
        collection.ensureIndex("{"+keyField+":1}","{unique:true, sparse:true}");
        return collection;
    }
	
	public InsertResult save(T object) {
		InsertResult iResult = null;
        try {
        	MongoCollection db  = getIndexedCollection();
			db.save(object);
            iResult = InsertResult.OK;
        } catch (DuplicateKeyException dk) {
            iResult = InsertResult.DUPLICATE;
        } catch (MongoException me) {
        	iResult = InsertResult.UNKNOWN_ERROR;
        	me.printStackTrace();
        }
		return iResult;
	}
	
	public T get(String key, Class<T> clazz) {
		MongoCollection db = getIndexedCollection(); 
		return db.findOne("{"+keyField+":#}", key).as(clazz);
	}
	
	public List<T> getAll(Class<T> clazz) {
		List<T> all = new ArrayList<T>();
        Iterable<T> foundObjects = getIndexedCollection().find().as(clazz);
        for (T obj : foundObjects) {
        	all.add(obj);
        }
        return all;
		
	}

	
	public void delete(String userId) {
		MongoCollection db = getIndexedCollection(); 
		db.remove("{"+keyField+":#}", userId);
	}

}
