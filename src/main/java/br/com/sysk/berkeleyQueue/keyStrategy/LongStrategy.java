package br.com.sysk.berkeleyQueue.keyStrategy;

import br.com.sysk.berkeleyQueue.keyComparator.KeyComparator;
import br.com.sysk.berkeleyQueue.keyComparator.LongKeyComparator;

public class LongStrategy extends KeyStrategy<Long>{

	@Override
	public Long generate(Long lastValue) {
		if (lastValue == null) {
			return 1l;
		}
		return lastValue + 1;
	}

	@Override
	public KeyComparator<Long> getComparator() {
		return new LongKeyComparator();
	}

}
