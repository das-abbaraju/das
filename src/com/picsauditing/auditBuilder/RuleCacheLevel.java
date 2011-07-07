package com.picsauditing.auditBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

abstract public class RuleCacheLevel<K, V extends RuleFilterable<R>, R> implements RuleFilterable<R> {
	protected Map<K, V> data = new LinkedHashMap<K, V>();

	public V getData(K value) {
		return data.get(value);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (K k : data.keySet()) {
			builder.append("{");
			builder.append(k.toString());
			builder.append("}");
		}

		builder.append("]");
		return builder.toString();
	}

	public abstract void add(R rule);
}
