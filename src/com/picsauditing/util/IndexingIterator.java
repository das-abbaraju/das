package com.picsauditing.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Iterator used to get a range of elements, functions exactly the same as a
 * normal Iterator with the addition of a <code>next(int number)</code> method.
 * 
 * @author David Tomberlin
 * @see <code>public Set<T> next(int number)</code>
 * @param <T>
 */
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

	/**
	 * Returns the next n elements in the underlying collection. If
	 * collection.size is < n, then only those elements are returns. Calling
	 * this is the same as calling .next n number of times, so it does advance
	 * the iterator as that would.
	 * 
	 * @param number
	 * @return A Set containing up to n number of elements
	 */
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
