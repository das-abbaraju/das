package com.picsauditing.actions.audits;

import java.util.Date;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditBuilderController;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorSave extends PicsActionSupport implements Preparable {

	private ContractorAccount ca = null;
	private ContractorAccountDAO dao;
	private int auditorId;
	private AuditBuilderController auditBuilder;

	public ContractorSave(ContractorAccountDAO dao, AuditBuilderController auditBuilder) {
		this.dao = dao;
		this.auditBuilder = auditBuilder;
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;

		if (ca.getId() == 0) {
			addActionError("Missing Contractor ID");
			return INPUT;
		}

		if (auditorId > 0) {
			ca.setAuditor(new User(auditorId));
			auditBuilder.buildAudits(ca, getUser());

			for (ContractorAudit conAudit : ca.getAudits()) {
				if (!conAudit.getAuditType().getClassType().isAudit()) {
					conAudit.setAuditor(ca.getAuditor());
					conAudit.setClosingAuditor(new User(conAudit.getIndependentClosingAuditor(ca.getAuditor())));
					conAudit.setAssignedDate(new Date());
					PicsLogger.log(" assigning auditorID " + auditorId + " to " + conAudit.getAuditType().getName().toString());
				}
			}
		}

		dao.save(ca);
		return SUCCESS;
	}

	public void prepare() throws Exception {
		ca = dao.find(getParameter("ca.id"));
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
