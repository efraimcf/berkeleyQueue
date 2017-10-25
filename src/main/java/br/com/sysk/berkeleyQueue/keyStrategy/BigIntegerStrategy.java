package br.com.sysk.berkeleyQueue.keyStrategy;

import java.math.BigInteger;

import br.com.sysk.berkeleyQueue.keyComparator.BigIntegerKeyComparator;
import br.com.sysk.berkeleyQueue.keyComparator.KeyComparator;

public class BigIntegerStrategy extends KeyStrategy<BigInteger> {

	@Override
	public BigInteger generate(BigInteger lastValue) {
		if (lastValue == null) {
			return BigInteger.ONE;
		}
		return lastValue.add(BigInteger.ONE);
	}

	@Override
	public KeyComparator<BigInteger> getComparator() {
		return new BigIntegerKeyComparator();
	}

}
