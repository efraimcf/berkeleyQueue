package br.com.sysk.berkeleyQueue;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;

import br.com.sysk.berkeleyQueue.util.JsonUtil;

public class BerkeleyQueue<T> {

	private final String queueName;
	
	private final Database queue;
	
	private Class<T> clazz;
	
	public BerkeleyQueue(String dbPath, String queueName, Class<T> clazz) throws DatabaseException {
		this.queueName = queueName;
		this.queue = BerkeleyDatabase.getInstance(dbPath).createQueue(queueName);
		this.clazz = clazz;
	}

	public String getName() {
		return queueName;
	}

	public long size() throws DatabaseException {
		return queue.count();
	}
	
	public void close() throws DatabaseException {
		queue.close();
	}
	
	public void push(T item) throws DatabaseException {
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		Cursor cursor = queue.openCursor(null, null);
		cursor.getLast(key, data, LockMode.RMW);
		BigInteger prevKeyValue;
		if (key.getData() == null) {
			prevKeyValue = BigInteger.valueOf(-1);
		} else {
			prevKeyValue = new BigInteger(key.getData());
		}
		BigInteger newKeyValue = prevKeyValue.add(BigInteger.ONE);
		final DatabaseEntry newKey = new DatabaseEntry(newKeyValue.toByteArray());
		final DatabaseEntry newData = new DatabaseEntry(JsonUtil.toJson(item).getBytes());
		queue.put(null, newKey, newData);
		queue.sync();
		cursor.close();
	}
	
	public T pull() throws DatabaseException, UnsupportedEncodingException {
		final DatabaseEntry key = new DatabaseEntry();
		final DatabaseEntry data = new DatabaseEntry();
		final Cursor cursor = queue.openCursor(null, null);
		cursor.getFirst(key, data, LockMode.RMW);
		if (data.getData() == null) {
			return null;
		}
		final String json = new String(data.getData(), "UTF-8");
		T item = JsonUtil.fromJson(json, clazz);
		cursor.delete();
		queue.sync();
		cursor.close();
		return item;
	}
}
