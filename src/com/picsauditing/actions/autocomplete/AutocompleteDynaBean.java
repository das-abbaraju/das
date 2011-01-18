package com.picsauditing.actions.autocomplete;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.BaseTable;

@SuppressWarnings("serial")
public abstract class AutocompleteDynaBean extends PicsActionSupport {

	protected List<BasicDynaBean> items;
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

	protected void checkPermissions() throws Exception {

	}

	protected abstract void findItems();

	protected void createOutput() {
		for (BasicDynaBean item : items) {
			outputBuffer.append(createOutput(item)).append("\n");
		}
	}

	protected abstract String createOutput(BasicDynaBean item);

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
