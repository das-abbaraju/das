package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.BaseTable;

/**
 * 
 * @author kpartridge
 * 
 */
@SuppressWarnings( { "serial", "unchecked" })
public abstract class AutocompleteActionSupport<T extends BaseTable> extends PicsActionSupport {

	protected String q;
	protected int limit;
	public int[] itemKeys;

	public final String autocomplete() {
		StringBuilder sb = new StringBuilder();

		for (T item : getItems()) {
			sb.append(formatAutocomplete(item)).append("\n");
		}

		output = sb.toString();

		return PLAIN_TEXT;
	}

	public final String json() {
		json = new JSONObject();

		JSONArray result = new JSONArray();
		for (T item : getItems()) {
			result.add(formatJson(item));
		}

		json.put("result", result);

		return JSON;
	}

	public final String tokenJson() {
		json = new JSONObject();

		JSONArray result = new JSONArray();
		for (T item : getItems()) {
			result.add(formatTokenJson(item));
		}

		json.put("result", result);

		return JSON;
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
		o.put("id", item.getAutocompleteResult());
		o.put("name", item.getAutocompleteValue());
		return o;
	}

	protected abstract Collection<T> getItems();

	protected boolean isSearchDigit() {
		try {
			Integer.parseInt(q);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int[] getItemKeys() {
		return itemKeys;
	}

	public void setItemKeys(int[] itemKeys) {
		this.itemKeys = itemKeys;
	}

}
