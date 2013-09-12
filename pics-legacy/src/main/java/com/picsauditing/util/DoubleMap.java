package com.picsauditing.util;


public class DoubleMap<K1, K2, V> {
	protected SuperMap<V> data = new SuperMap<V>();

	public V get(K1 k1, K2 k2) {
		return data.get(k1, k2);
	}

	public boolean put(K1 k1, K2 k2, V v) {
		return data.put(k1, k2, v);
	}
}
