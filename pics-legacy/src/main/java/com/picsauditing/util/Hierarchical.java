package com.picsauditing.util;

import java.util.Collection;
import java.util.Map;

public interface Hierarchical<T> {
	T getParent();

	Collection<T> getChildren();

	String getNodeDisplay();

	Map<String, String> getNodeAttributes();

	boolean isLeaf();

	/**
	 * If this node has children, should they be shown be default?
	 */
	boolean showChildren();

}
