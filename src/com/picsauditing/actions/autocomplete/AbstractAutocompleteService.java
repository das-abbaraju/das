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
			result.add(formatTokenJson(item));
		}

		json.put("result", result);

		return json;
	}

	@SuppressWarnings("unchecked")
	private JSONObject formatTokenJson(T item) {
		JSONObject o = new JSONObject();
		o.put("key", getAutocompleteItem(item));
		o.put("value", getAutocompleteValue(item));
		
		return o;
	}

	protected abstract Collection<T> getItems(String search, Permissions permissions);

	protected abstract Object getAutocompleteItem(T item);

	protected abstract Object getAutocompleteValue(T item);
}
