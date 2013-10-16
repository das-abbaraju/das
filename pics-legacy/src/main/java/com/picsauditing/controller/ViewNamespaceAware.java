package com.picsauditing.controller;

public interface ViewNamespaceAware {

	/**
	 * Construct a unique page id for the HTML
	 * 
	 * @return
	 */
	String getUniquePageId();

	/**
	 * Construct a page id for the HTML (may represent many pages)
	 * 
	 * @return
	 */
	String getPageId();

}
