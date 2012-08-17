package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.picsauditing.jpa.entities.Autocompleteable;

@Service
@SuppressWarnings({"unchecked" })
public abstract class AutocompleteService<T extends Autocompleteable> {

	public final String autocomplete(String q) {
		StringBuilder sb = new StringBuilder();

		for (T item : getItems(q)) {
			sb.append(formatAutocomplete(item)).append("\n");
		}

		return sb.toString();
	}

	public final JSONObject json(String q) {
		JSONObject json = new JSONObject();

		JSONArray result = new JSONArray();
		for (T item : getItems(q)) {
			result.add(formatJson(item));
		}

		json.put("result", result);

		return json;
	}

	public final JSONObject tokenJson(String q) {
		JSONObject json = new JSONObject();

		JSONArray result = new JSONArray();
		for (T item : getItems(q)) {
			result.add(formatTokenJson(item));
		}

		json.put("result", result);

		return json;
	}

	public StringBuilder formatAutocomplete(T item) {
		StringBuilder sb = new StringBuilder();
		return sb.append(item.getAutocompleteResult()).append("|").append(item.getAutocompleteItem()).append("|")
				.append(item.getAutocompleteValue());
	}

	public JSONObject formatJson(T item) {
		return item.toJSON();
	}

	public JSONObject formatTokenJson(T item) {
		JSONObject o = new JSONObject();
		o.put("key", item.getAutocompleteItem());
		o.put("value", item.getAutocompleteValue());
		return o;
	}

	protected abstract Collection<T> getItems(String q);

	protected boolean isSearchDigit(String q) {
		try {
			Integer.parseInt(q);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

}
