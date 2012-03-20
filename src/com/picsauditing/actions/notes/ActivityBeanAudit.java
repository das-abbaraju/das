package com.picsauditing.actions.notes;

import com.picsauditing.jpa.entities.NoteCategory;

/**
 * This version of ActivityBean holds data from a CAOW record
 */
public class ActivityBeanAudit extends ActivityBean {

	@Override
	public NoteCategory getNoteCategory() {
		if (getAuditType().getClassType().isImEmployee())
			return NoteCategory.Employee;
		if (getAuditType().getClassType().isPolicy())
			return NoteCategory.Insurance;
		return NoteCategory.Audits;
	}

	@Override
	public boolean needsComplexSummaryWithTranlations() {
		return true;
	}

	@Override
	public boolean hasDetails() {
		return false;
	}
}
