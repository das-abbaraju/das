package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.jpa.entities.AuditOptionGroup;

@SuppressWarnings("serial")
public class ManageOptionGroup extends ManageOptionComponent {
	@Override
	public String save() throws Exception {
		if (group.getName() == null || group.getName().toString().isEmpty())
			addActionError("Missing name");

		group.setAuditColumns(permissions);
		auditOptionValueDAO.save(group);

		return SUCCESS;
	}

	@Override
	public String delete() throws Exception {
		// TODO Check which questions/answers use this option type!
		return SUCCESS;
	}

	public String editAjax() throws Exception {
		if (group == null)
			group = new AuditOptionGroup();

		return "edit";
	}

	// Get all
	public List<AuditOptionGroup> getAll() {
		return auditOptionValueDAO.getAllOptionTypes();
	}
}
