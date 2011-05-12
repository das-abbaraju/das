package com.picsauditing.actions.auditType;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditOptionGroup;

@SuppressWarnings("serial")
public abstract class ManageOptionComponent extends PicsActionSupport {
	@Autowired
	protected AuditOptionValueDAO auditOptionValueDAO;
	@Autowired
	protected AuditDataDAO auditDataDAO;

	protected AuditOptionGroup group;

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits)
	public String execute() throws Exception {
		return SUCCESS;
	}

	public abstract String save() throws Exception;

	public abstract String delete() throws Exception;

	public abstract String editAjax() throws Exception;

	public AuditOptionGroup getGroup() {
		return group;
	}

	public void setGroup(AuditOptionGroup group) {
		this.group = group;
	}
}
