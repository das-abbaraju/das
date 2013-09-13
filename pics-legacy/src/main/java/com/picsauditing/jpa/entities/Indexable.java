package com.picsauditing.jpa.entities;

import java.util.List;

import com.picsauditing.util.IndexObject;

public interface Indexable {

	/**
	 * If you Index a BaseTable object, then don't implement this again!
	 * 
	 * @return
	 */
	public int getId();

	/**
	 * a property on the Entity, used to query a list of Entities that need
	 * reindexing.
	 * 
	 * @return
	 */
	public boolean isNeedsIndexing();

	public void setNeedsIndexing(boolean b);

	public List<IndexObject> getIndexValues();

	/**
	 * Examples: C, CO, AS, U, E, T
	 * 
	 * @return
	 */
	public String getIndexType();

	/**
	 * Examples: accounts, users, employee, trade
	 * 
	 * @return
	 */
	public String getReturnType();

	/**
	 * @return URL destination when the user clicks on this search result
	 */
	public String getViewLink();

	/**
	 * @return Return the Text to display in the search results. This should
	 *         implement Locale-specific logic
	 */
	public String getSearchText();

	/**
	 * @return Returns whether or not the object should be removed from the
	 *         app_index
	 */
	public boolean isRemoved();
}
