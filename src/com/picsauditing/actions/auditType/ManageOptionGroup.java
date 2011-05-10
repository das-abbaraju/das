package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.jpa.entities.AuditOptionGroup;

@SuppressWarnings("serial")
public class ManageOptionGroup extends ManageOptionComponent {
	@Override
	public String save() throws Exception {
		if (type.getName() == null || type.getName().toString().isEmpty())
			addActionError("Missing name");

		type.setAuditColumns(permissions);
		auditOptionValueDAO.save(type);

		return SUCCESS;
	}

	@Override
	public String delete() throws Exception {
		// TODO Check which questions/answers use this option type!
		return SUCCESS;
	}

	public String editAjax() throws Exception {
		if (type == null)
			type = new AuditOptionGroup();

		return "edit";
	}

	// Get all
	public List<AuditOptionGroup> getAll() {
		return auditOptionValueDAO.getAllOptionTypes();
	}
}
