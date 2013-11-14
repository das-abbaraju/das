package com.picsauditing.employeeguard.util;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ListUtil {
	public static <E extends Comparable<E>> List<E> removeDuplicatesAndSort(List<E> list) {
		Set<E> unique = new TreeSet<>(list);
		list.clear();
		list.addAll(unique);

		return list;
	}

	public static <E> List<E> removeDuplicatesAndSort(List<E> list, Comparator<E> comparator) {
		Set<E> unique = new TreeSet<>(comparator);
		unique.addAll(list);
		list.clear();
		list.addAll(unique);

		return list;
	}
}
