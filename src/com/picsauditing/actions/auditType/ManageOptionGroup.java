package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOptionGroup;

@SuppressWarnings("serial")
public class ManageOptionGroup extends ManageOptionComponent {
	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String save() throws Exception {
		if (group.getName() == null || group.getName().toString().isEmpty())
			addActionError("Missing name");

		group.setAuditColumns(permissions);
		auditOptionValueDAO.save(group);

		return SUCCESS;
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Delete)
	public String delete() throws Exception {
		List<AuditData> data = auditDataDAO.findByOptionGroupID(group.getId());

		if (group.getQuestions().size() > 0 || data.size() > 0)
			addActionError("Option group '" + group.getName()
					+ "' is still linked to audit questions and/or audit data.");

		if (getActionErrors().size() == 0)
			auditOptionValueDAO.remove(group);

		return SUCCESS;
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
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
