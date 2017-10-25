package br.com.sysk.berkeleyQueue.keyStrategy;

import java.util.Date;

import br.com.sysk.berkeleyQueue.keyComparator.KeyComparator;
import br.com.sysk.berkeleyQueue.keyComparator.TimestampKeyComparator;

public class TimestampStrategy extends KeyStrategy<Long>{

	@Override
	public Long generate(Long lastValue) {
		return new Date().getTime();
	}

	@Override
	public KeyComparator<Long> getComparator() {
		return new TimestampKeyComparator();
	}
}
