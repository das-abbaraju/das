package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageOptionValue extends ManageOptionComponent {
	private AuditOptionValue value;

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits)
	public String execute() throws Exception {
		if (group == null)
			addActionError("Missing Option Group");

		return SUCCESS;
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
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
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Delete)
	public String delete() throws Exception {
		List<AuditData> data = auditDataDAO.findByOptionGroupAndValue(group.getId(), value.getIdentifier());

		if (data.size() > 0)
			addActionError("Option value '" + value.getName() + "' is being used in Audit Data");

		if (getActionErrors().size() == 0)
			auditOptionValueDAO.remove(value);

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageAudits)
	public String listAjax() throws Exception {
		return "list";
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
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
