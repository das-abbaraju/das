package com.picsauditing.util.generic;

import org.apache.commons.collections.Predicate;

public abstract class GenericPredicate<E> implements Predicate {

	@SuppressWarnings("unchecked")
	public boolean evaluate(Object object) {
		return evaluateEntity((E) object);
	}

	public abstract boolean evaluateEntity(E object);

}
