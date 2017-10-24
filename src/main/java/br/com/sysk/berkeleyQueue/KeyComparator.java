package br.com.sysk.berkeleyQueue;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Comparator;

public class KeyComparator implements Comparator<byte[]>, Serializable {

	private static final long serialVersionUID = 8412226615335470125L;

	@Override
    public int compare(byte[] key1, byte[] key2) {
        return new BigInteger(key1).compareTo(new BigInteger(key2));
    }
}
