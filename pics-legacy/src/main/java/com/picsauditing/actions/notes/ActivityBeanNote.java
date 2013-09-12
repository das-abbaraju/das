package com.picsauditing.actions.notes;

/**
 * This version of ActivityBean holds data from a Note record
 */
public class ActivityBeanNote extends ActivityBean {

	@Override
	public boolean hasDetails() {
		return true;
	}

	@Override
	public boolean needsComplexSummaryWithTranlations() {
		return false;
	}

}
