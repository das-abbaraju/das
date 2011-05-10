package com.picsauditing.actions.auditType;

import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageOptionValue extends ManageOptionComponent {
	private AuditOptionValue optionValue;

	@Override
	public String save() throws Exception {
		if (optionValue.getName() == null)
			addActionError("Missing answer");

		if (getActionErrors().size() == 0) {
			if (Strings.isEmpty(optionValue.getUniqueCode()))
				optionValue.setUniqueCode(null);

			optionValue.setType(type);
			optionValue.setAuditColumns(permissions);
			auditOptionValueDAO.save(optionValue);
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
		if (optionValue == null)
			optionValue = new AuditOptionValue();

		return "edit";
	}

	public AuditOptionValue getOption() {
		return optionValue;
	}

	public void setOption(AuditOptionValue option) {
		this.optionValue = option;
	}
}
