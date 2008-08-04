package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCatOperatorDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;

public class ManageDesktopMatrix extends PicsActionSupport {
	private List<AuditQuestion> questions;
	
	protected OperatorAccountDAO operatorAccountDAO;
	protected AuditTypeDAO auditDAO;
	protected AuditCatOperatorDAO auditCatOperatorDAO;
	
	public ManageDesktopMatrix(OperatorAccountDAO operatorAccountDAO, AuditTypeDAO auditCategoryDAO, AuditCatOperatorDAO auditCatOperatorDAO) {
		this.operatorAccountDAO = operatorAccountDAO;
		this.auditDAO = auditCategoryDAO;
		this.auditCatOperatorDAO = auditCatOperatorDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.ManageAudits);
		
		return SUCCESS;
	}

	// GETTERS && SETTERS
	
}
