package com.picsauditing.util.generic;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Any Entity <E> used by this class must implement the equals() method.
 */
public class IntersectionAndComplementProcess {

	public interface EventCallbacks<E> {

		E handleNewEntity(E entity);

		E handleDuplicate(E entity);

		void handleRemoval(E entity);

	}

	public static <E> List<E> intersection(final List<E> newList, final List<E> oldList, Comparator<E> comparator,
	                                       final EventCallbacks<E> eventCallbacks) {

		List<E> result = new ArrayList<>();
		if (CollectionUtils.isEmpty(newList) && CollectionUtils.isEmpty(oldList)) {
			return result;
		}

		if (CollectionUtils.isEmpty(newList) && CollectionUtils.isNotEmpty(oldList)) {
			processRemovals(oldList, eventCallbacks);
			return result;
		}

		for (E entity : newList) {
			if (isIntersection(oldList, entity, comparator)) {
				int index = oldList.indexOf(entity);
				E resultOfDuplicateCallback = eventCallbacks.handleDuplicate(oldList.remove(index));
				result.add(resultOfDuplicateCallback);
			} else {
				E resultOfNewEntityCallback = eventCallbacks.handleNewEntity(entity);
				result.add(resultOfNewEntityCallback);
			}
		}

		processRemovals(oldList, eventCallbacks);

		return result;
	}

	private static <E> boolean isIntersection(final List<E> collection, final E entity, Comparator<E> comparator) {
		return contains(collection, entity, comparator);
	}

    private static <E> boolean contains(List<E> collection, E elementToFind, Comparator<E> comparator) {
        if (CollectionUtils.isEmpty(collection)) {
            return false;
        }

        for (E element : collection) {
            if (comparator.compare(element, elementToFind) == 0) {
                return true;
            }
        }

        return false;
    }

	private static <E> void processRemovals(final List<E> collection, final EventCallbacks<E> eventCallbacks) {
		if (CollectionUtils.isEmpty(collection)) {
			return;
		}

		for (E entity : collection) {
			eventCallbacks.handleRemoval(entity);
		}
	}

}
