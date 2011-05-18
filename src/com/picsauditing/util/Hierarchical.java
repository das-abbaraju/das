package com.picsauditing.util;

import java.util.Collection;

import com.picsauditing.jpa.entities.JSONable;

public interface Hierarchical<T> extends JSONable {
	T getParent();

	Collection<T> getChildren();

	boolean isLeaf();

	/**
	 * If this node has children, should they be shown be default?
	 */
	boolean showChildren();

}
