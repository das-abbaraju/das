package com.picsauditing.employeeguard.util;

import java.util.*;

@Deprecated
public class ExtractorUtil {

	@Deprecated
	public static <E, R> List<R> extractList(Collection<E> collection, Extractor<E, R> extractor) {
		List<R> result = new ArrayList<>();

		for (E entity : collection) {
			result.add(extractor.extract(entity));
		}

		return result;
	}

	@Deprecated
	public static <E, R> Set<R> extractSet(Collection<E> collection, Extractor<E, R> extractor) {
		Set<R> result = new HashSet<>();

		for (E entity : collection) {
			result.add(extractor.extract(entity));
		}

		return result;
	}

}
