package com.picsauditing.actions.auditType;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditOptionGroup;

@SuppressWarnings("serial")
public abstract class ManageOptionComponent extends PicsActionSupport {
	@Autowired
	protected AuditOptionValueDAO auditOptionValueDAO;

	protected AuditOptionGroup type;

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits)
	public String execute() throws Exception {
		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public abstract String save() throws Exception;

	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Delete)
	public abstract String delete() throws Exception;

	public AuditOptionGroup getType() {
		return type;
	}

	public void setType(AuditOptionGroup type) {
		this.type = type;
	}
}
