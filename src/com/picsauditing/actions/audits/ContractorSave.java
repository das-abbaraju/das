package com.picsauditing.actions.audits;

import java.util.Date;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;

public class ContractorSave extends PicsActionSupport implements Preparable {

	private ContractorAccount ca = null;
	private ContractorAccountDAO dao;
	private int auditorId;

	public ContractorSave(ContractorAccountDAO dao) {
		this.dao = dao;
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;

		if (ca.getId() == 0) {
			message = "Missing Contractor ID";
			return INPUT;
		}

		if (auditorId > 0) {
			ca.setAuditor(new User());
			ca.getAuditor().setId(auditorId);
		}

		for (ContractorAudit conAudit : ca.getAudits()) {
			if (conAudit.getAuditType().isPqf()
					|| conAudit.getAuditType().getAuditTypeID() == AuditType.WELCOME) {
				if (conAudit.getAuditor() == null) {
					conAudit.setAuditor(new User());
					conAudit.getAuditor().setId(auditorId);
					conAudit.setAssignedDate(new Date());
				}
			}
		}
		ca = dao.save(ca);
		return SUCCESS;
	}

	public void prepare() throws Exception {
		String[] ids = (String[]) ActionContext.getContext().getParameters()
				.get("ca.id");
		int id = new Integer(ids[0]).intValue();
		ca = dao.find(id);
	}

	public ContractorAccount getCa() {
		return ca;
	}

	public void setCa(ContractorAccount ca) {
		this.ca = ca;
	}

	public int getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(int auditorId) {
		this.auditorId = auditorId;
	}
}
