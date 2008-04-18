package com.picsauditing.actions.auditType;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

// Samples
// AuditSaveAjax.action?audit.id=3260&audit.scheduledDate=01/01/2008&audit.auditor.id=907
// AuditSaveAjax.action?audit.id=3260&audit.scheduledDate=&audit.auditor=

public class AuditSave extends PicsActionSupport implements Preparable {
	ContractorAudit audit;
	ContractorAuditDAO dao;

	public AuditSave(ContractorAuditDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {
		// getPermissions();
		// TODO add security
		// TODO return an appropriate message

		dao.save(audit);
		this.message = "Success";

		return SUCCESS;
	}

	public void prepare() throws Exception {
		String[] ids = (String[]) ActionContext.getContext().getParameters().get("audit.id");
		int id = new Integer(ids[0]).intValue();
		audit = dao.find(id);
	}

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

}
