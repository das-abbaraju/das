package com.picsauditing.employeeguard.util;

import java.util.ArrayList;
import java.util.List;

public class ExtractorUtil {
	public static <E, R> List<R> extractList(List<E> list, Extractor<E, R> extractor) {
		List<R> result = new ArrayList<>();

		for (E entity : list) {
			result.add(extractor.extract(entity));
		}

		return result;
	}
}
