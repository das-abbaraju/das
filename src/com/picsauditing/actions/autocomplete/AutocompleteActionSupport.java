package com.picsauditing.actions.autocomplete;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.BaseTable;

@SuppressWarnings("serial")
public abstract class AutocompleteActionSupport<T extends BaseTable> extends PicsActionSupport {

	protected List<T> items;
	protected String q;
	protected StringBuffer outputBuffer = new StringBuffer();

	@Override
	public String execute() throws Exception {
		checkPermissions();
		findItems();
		createOutput();
		output = outputBuffer.toString();
		return SUCCESS;
	}

	protected abstract void findItems();

	protected void checkPermissions() throws Exception {

	}

	protected void createOutput() {
		for (T item : items) {
			outputBuffer.append(item.toString()).append("\n");
		}
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
