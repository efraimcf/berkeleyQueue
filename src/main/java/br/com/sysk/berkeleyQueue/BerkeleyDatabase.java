package br.com.sysk.berkeleyQueue;

import java.io.File;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class BerkeleyDatabase {

	private static BerkeleyDatabase instance;
	
	private final Environment dbEnv;
	private final DatabaseConfig dbConfig;
	
	private BerkeleyDatabase(String dbPath) throws DatabaseException {
		File path = new File(dbPath);
		if (!path.exists()) {
			path.mkdirs();
		}
		final EnvironmentConfig dbEnvConfig = new EnvironmentConfig();
		dbEnvConfig.setTransactional(false);
		dbEnvConfig.setAllowCreate(true);
		dbEnvConfig.setLockTimeout(10000000);
		this.dbEnv = new Environment(path, dbEnvConfig);
		
		dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(false);
		dbConfig.setAllowCreate(true);
		dbConfig.setDeferredWrite(true);
		dbConfig.setBtreeComparator(new KeyComparator());
	}
	
	public static BerkeleyDatabase getInstance(String dbPath) throws DatabaseException {
		if (instance == null) {
			instance = new BerkeleyDatabase(dbPath);
		}
		return instance;
	}
	
	public Database createQueue(String queueName) throws DatabaseException {
		return dbEnv.openDatabase(null, queueName, dbConfig);
	}
	
	public void closeDatabase() throws DatabaseException {
		dbEnv.close();
	}
}
