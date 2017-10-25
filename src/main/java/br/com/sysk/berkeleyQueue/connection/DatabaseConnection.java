package br.com.sysk.berkeleyQueue.connection;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Comparator;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import br.com.sysk.berkeleyQueue.annotation.Id;
import br.com.sysk.berkeleyQueue.keyComparator.BigIntegerKeyComparator;
import br.com.sysk.berkeleyQueue.keyStrategy.KeyStrategy;
import br.com.sysk.berkeleyQueue.util.ReflectionsUtil;

public class DatabaseConnection {

	private static DatabaseConnection instance;
	private static String dbPath;
	
	private final Environment dbEnv;
	
	private DatabaseConnection(String dbPath) throws DatabaseException {
		File path = new File(dbPath);
		if (!path.exists()) {
			path.mkdirs();
		}
		final EnvironmentConfig dbEnvConfig = new EnvironmentConfig();
		dbEnvConfig.setTransactional(false);
		dbEnvConfig.setAllowCreate(true);
		dbEnvConfig.setLockTimeout(10000000);
		this.dbEnv = new Environment(path, dbEnvConfig);
	}
	
	public static DatabaseConnection getInstance(String dbPath) throws DatabaseException {
		if (DatabaseConnection.dbPath == null || !DatabaseConnection.dbPath.equals(dbPath)) {
			if (instance == null) {
				instance = new DatabaseConnection(dbPath);
			} else {
				instance.closeDatabase();
				instance = new DatabaseConnection(dbPath);
			}
		}
		return instance;
	}
	
	public Database createEntity(String name) throws DatabaseException {
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(false);
		dbConfig.setAllowCreate(true);
		dbConfig.setDeferredWrite(true);
		dbConfig.setBtreeComparator(new BigIntegerKeyComparator());
		return dbEnv.openDatabase(null, name, dbConfig);
	}

	@SuppressWarnings("rawtypes")
	public Database createEntity(Class clazz) 
			throws DatabaseException, InstantiationException, IllegalAccessException {
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(false);
		dbConfig.setAllowCreate(true);
		dbConfig.setDeferredWrite(true);
		dbConfig.setBtreeComparator(getComparatorForClass(clazz));
		return dbEnv.openDatabase(null, clazz.getSimpleName(), dbConfig);
	}
	
	public void closeDatabase() throws DatabaseException {
		dbEnv.close();
	}
	
	@SuppressWarnings("rawtypes")
	private Comparator getComparatorForClass(Class clazz) 
			throws InstantiationException, IllegalAccessException {
		Field field = ReflectionsUtil.getAnnotatedField(clazz, Id.class);
		Id id = (Id) field.getAnnotation(Id.class);
		return ((KeyStrategy) id.strategy().newInstance()).getComparator();
	}

}
