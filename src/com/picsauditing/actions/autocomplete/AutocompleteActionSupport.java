package com.picsauditing.actions.autocomplete;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.BaseTable;

@SuppressWarnings("serial")
public abstract class AutocompleteActionSupport<T extends BaseTable> extends PicsActionSupport {

	protected List<T> items;
	protected String q;
	protected StringBuffer outputBuffer = new StringBuffer();
	protected JSONArray jsonObjs = new JSONArray();

	@Override
	public String execute() throws Exception {
		checkPermissions();
		findItems();
		createOutput();
		output = outputBuffer.toString();
		if ("json".equals(button))
			return JSON;

		return PLAIN_TEXT;
	}

	protected abstract void findItems();

	protected void checkPermissions() throws Exception {

	}

	@SuppressWarnings("unchecked")
	protected void createOutput() {
		for (T item : items) {
			createOutput(item);
		}

		if ("json".equals(button))
			json.put("items", jsonObjs);
	}

	@SuppressWarnings("unchecked")
	protected void createOutput(T item) {
		if ("json".equals(button)) {
			jsonObjs.add(createOutputJSON(item));
		} else {
			outputBuffer.append(createOutputAutocomplete(item));
		}
	}

	protected JSONObject createOutputJSON(T item) {
		return item.toJSON();
	}

	protected String createOutputAutocomplete(T item) {
		return item.getAutocompleteId() + "|" + item.getAutocompleteValue() + "\n";
	}

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

}
