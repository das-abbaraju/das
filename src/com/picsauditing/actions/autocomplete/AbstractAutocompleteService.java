package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.Permissions;

public abstract class AbstractAutocompleteService<T> {

	/**
	 * This is the limit of the total number of records that can be returned at one time
	 * from the auto-complete.
	 */
	protected static final int RESULT_SET_LIMIT = 10;

	public final JSONObject getJson(String search, Permissions permissions) {
		Collection<T> searchResults = getItemsForSearch(search, permissions);
		return getJsonForSearch(searchResults, permissions);
	}

	public final JSONObject searchByKey(String searchKey, Permissions permissions) {
		Collection<T> searchResults = getItemsForSearchKey(searchKey, permissions);
		return getJsonForSearch(searchResults, permissions);
	}

	@SuppressWarnings("unchecked")
	private JSONObject getJsonForSearch(Collection<T> searchResults, Permissions permissions) {
		JSONObject json = new JSONObject();

		JSONArray result = new JSONArray();
		for (T item : searchResults) {
			result.add(formatJson(item, permissions));
		}

		json.put("result", result);

		return json;
	}

	@SuppressWarnings("unchecked")
	private JSONObject formatJson(T item, Permissions permissions) {
		JSONObject o = new JSONObject();
		o.put("key", getKey(item));
		o.put("value", getValue(item, permissions));

		return o;
	}

	protected abstract Collection<T> getItemsForSearch(String search, Permissions permissions);

	protected abstract Collection<T> getItemsForSearchKey(String searchKey, Permissions permissions);

	protected abstract Object getKey(T item);

	/**
	 * This is the value that will be displayed in the drop-down of the auto-complete
	 *
	 * @param item
	 * @param permissions
	 * @return
	 */
	protected abstract Object getValue(T item, Permissions permissions);
}
