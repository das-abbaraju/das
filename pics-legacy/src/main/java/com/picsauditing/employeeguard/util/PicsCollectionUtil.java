package com.picsauditing.employeeguard.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PicsCollectionUtil {

	public interface CollectionToMapConverter<R, C, V> {

		R getRow(V value);

		C getColumn(V value);

	}

	public static <R, C, V> Map<R, Map<C, V>> convertToMapOfMaps(final Collection<V> values,
																 final CollectionToMapConverter<R, C, V> converter) {
		if (CollectionUtils.isEmpty(values) || converter == null) {
			return Collections.emptyMap();
		}

		Map<R, Map<C, V>> result = new HashMap<>();
		for (V value : values) {
			R row = converter.getRow(value);
			C column = converter.getColumn(value);

			if (!result.containsKey(row)) {
				result.put(row, new HashMap<C, V>());
			}

			result.get(row).put(column, value);
		}

		return result;
	}
}