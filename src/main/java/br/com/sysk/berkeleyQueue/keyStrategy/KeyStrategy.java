package br.com.sysk.berkeleyQueue.keyStrategy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.sleepycat.je.DatabaseEntry;

import br.com.sysk.berkeleyQueue.keyComparator.KeyComparator;

public abstract class KeyStrategy<K> {

	public DatabaseEntry generateKey(K lastValue) throws IOException {
		K newValue = generate(lastValue);
		DatabaseEntry key = new DatabaseEntry(toByteArray(newValue));
		return key;
	}
	
	protected byte[] toByteArray(K newValue) {
		return newValue.toString().getBytes(StandardCharsets.UTF_8);
	}

	protected abstract K generate(K lastValue);
	
	public abstract KeyComparator<K> getComparator();
}
