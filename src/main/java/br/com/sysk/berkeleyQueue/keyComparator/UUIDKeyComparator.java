package br.com.sysk.berkeleyQueue.keyComparator;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import br.com.sysk.berkeleyQueue.util.Version;

public class UUIDKeyComparator extends KeyComparator<UUID>{

	private static final long serialVersionUID = Version.NUMBER;

	@Override
	public int compare(byte[] o1, byte[] o2) {
		UUID uuid1 = UUID.fromString(new String(o1, StandardCharsets.UTF_8));
		UUID uuid2 = UUID.fromString(new String(o2, StandardCharsets.UTF_8));
		long key1 = uuid1.timestamp();
		long key2 = uuid2.timestamp();
		return key1 < key2 ? -1 : key1 > key2 ? 1 : 0;
	}
}
