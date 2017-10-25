package br.com.sysk.berkeleyQueue.dao;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import br.com.sysk.berkeleyQueue.annotation.Id;
import br.com.sysk.berkeleyQueue.connection.DatabaseConnection;
import br.com.sysk.berkeleyQueue.keyStrategy.KeyStrategy;
import br.com.sysk.berkeleyQueue.util.JsonUtil;
import br.com.sysk.berkeleyQueue.util.ReflectionsUtil;

public class BerkeleyDAO<T, K> {

	private final String tableName;
	
	private final Database queue;
	
	private Class<T> clazz;
	
	public BerkeleyDAO(String dbPath, Class<T> clazz) throws DatabaseException {
		this.tableName = clazz.getSimpleName();
		this.queue = DatabaseConnection.getInstance(dbPath).createEntity(this.tableName);
		this.clazz = clazz;
	}

	public String getName() {
		return tableName;
	}

	public long size() throws DatabaseException {
		return queue.count();
	}
	
	public void close() throws DatabaseException {
		queue.close();
	}
	
	public T save(T entity) 
			throws DatabaseException, IllegalArgumentException, IllegalAccessException, 
			IOException, InstantiationException {
		if (entity == null) {
			throw new RuntimeException("Entity instance of " + clazz.getSimpleName() + " is null");
		}
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		Cursor cursor = queue.openCursor(null, null);
		cursor.getLast(key, data, LockMode.RMW);
		K id = getId(entity);
		final DatabaseEntry newKey;
		newKey = getNewKey(id);
		final DatabaseEntry newData = new DatabaseEntry(JsonUtil.toJson(entity).getBytes());
		queue.put(null, newKey, newData);
		queue.sync();
		cursor.close();
		updateEntityKey(id, entity);
		return entity;
	}
	
	public List<T> findAll() throws DatabaseException, IOException {
		List<T> list = new ArrayList<T>();
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		Cursor cursor = queue.openCursor(null, null);
		while(cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			final String json = new String(data.getData(), StandardCharsets.UTF_8);
			T item = JsonUtil.fromJson(json, clazz);
			list.add(item);
		}
		cursor.close();
		return list;
	}
	
	public T findById(K id) throws DatabaseException, IOException {
		final DatabaseEntry key = new DatabaseEntry(id.toString().getBytes());
		final DatabaseEntry data = new DatabaseEntry();
		final Cursor cursor = queue.openCursor(null, null);
		cursor.getSearchKey(key, data, LockMode.RMW);
		if (data.getData() == null) {
			return null;
		}
		final String json = new String(data.getData(), StandardCharsets.UTF_8);
		T item = JsonUtil.fromJson(json, clazz);
		cursor.close();
		return item;
	}
	
	@SuppressWarnings("unchecked")
	private K getId(T entity) throws IllegalArgumentException, IllegalAccessException, IOException {
		Class<K> idClazz = ReflectionsUtil.getAnnotatedFieldClass(entity, Id.class);
		K id = ReflectionsUtil.getAnnotatedFieldValue(entity, Id.class, idClazz);
		return id;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DatabaseEntry getNewKey(Object entity) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, IOException {
		Field field = ReflectionsUtil.getAnnotatedField(entity.getClass(), Id.class);
		Id idAnnotation = (Id) field.getAnnotation(Id.class);
		Class<? extends KeyStrategy> clazz = idAnnotation.strategy();
		if (clazz == null) {
			throw new RuntimeException("Cannot generate a new Key without strategy");
		}
		DatabaseEntry key = clazz.newInstance().generateKey(field.get(entity));
		return key;
	}
	
	private void updateEntityKey(K id, T entity) {
		try {
			Field field = ReflectionsUtil.getAnnotatedField(entity.getClass(), Id.class);
			ReflectionsUtil.setFieldValue(field, entity, id);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LoggerFactory.getLogger(BerkeleyDAO.class).warn(e.getMessage());
		}
	}
}
