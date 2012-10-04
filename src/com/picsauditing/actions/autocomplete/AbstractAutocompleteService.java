package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.Permissions;

public abstract class AbstractAutocompleteService<T> {

	@SuppressWarnings("unchecked")
	public final JSONObject tokenJson(String search, Permissions permissions) {
		JSONObject json = new JSONObject();

		JSONArray result = new JSONArray();
		for (T item : getItems(search, permissions)) {
			result.add(formatTokenJson(item, permissions));
		}

		json.put("result", result);

		return json;
	}

	@SuppressWarnings("unchecked")
	private JSONObject formatTokenJson(T item, Permissions permissions) {
		JSONObject o = new JSONObject();
		o.put("key", getKey(item));
		o.put("value", getValue(item, permissions));
		
		return o;
	}

	protected abstract Collection<T> getItems(String search, Permissions permissions);

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
