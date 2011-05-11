package com.picsauditing.actions.auditType;

import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageOptionValue extends ManageOptionComponent {
	private AuditOptionValue value;

	@Override
	public String save() throws Exception {
		if (value.getName() == null)
			addActionError("Missing answer");

		if (getActionErrors().size() == 0) {
			if (Strings.isEmpty(value.getUniqueCode()))
				value.setUniqueCode(null);

			value.setGroup(group);
			value.setAuditColumns(permissions);
			auditOptionValueDAO.save(value);
		}

		return SUCCESS;
	}

	@Override
	public String delete() throws Exception {
		// TODO check which questions have this answer
		return SUCCESS;
	}

	public String listAjax() throws Exception {
		return "list";
	}

	public String editAjax() throws Exception {
		if (value == null)
			value = new AuditOptionValue();

		return "edit";
	}

	public AuditOptionValue getValue() {
		return value;
	}

	public void setValue(AuditOptionValue value) {
		this.value = value;
	}
}
