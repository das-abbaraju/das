package com.picsauditing.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IndexingIterator<T> implements Iterator<T> {

	private final Collection<T> collection;
	private Iterator<T> it;

	private IndexingIterator(Collection<T> collection) {
		this.collection = collection;
		this.it = collection.iterator();
	}

	public static <T> IndexingIterator<T> getIterator(Collection<T> collection) {
		return new IndexingIterator<T>(collection);
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public T next() {
		return it.next();
	}

	@Override
	public void remove() {
		it.remove();
	}

	public Set<T> next(int number) {
		int count = 0;
		Set<T> result = new HashSet<T>();
		while (count < number && it.hasNext()) {
			result.add(it.next());
			count++;
		}
		return result;

	}

}
