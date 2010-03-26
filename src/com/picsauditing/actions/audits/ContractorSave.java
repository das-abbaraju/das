package com.picsauditing.actions.audits;

import java.util.Date;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class ContractorSave extends PicsActionSupport implements Preparable {

	private ContractorAccount ca = null;
	private ContractorAccountDAO dao;
	private int auditorId;
	private AuditBuilder auditBuilder;

	public ContractorSave(ContractorAccountDAO dao, AuditBuilder auditBuilder) {
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
			ca.setAuditor(new User());
			ca.getAuditor().setId(auditorId);
			auditBuilder.buildAudits(ca);

			for (ContractorAudit conAudit : ca.getAudits()) {
				System.out.println(" found " + conAudit.getAuditType().getAuditName());
				if (conAudit.getAuditType().getClassType().isPqf() 
						|| conAudit.getAuditType().isAnnualAddendum()
						|| conAudit.getAuditType().getId() == AuditType.WELCOME
						|| conAudit.getAuditType().getClassType().isPolicy()) {
					conAudit.setAuditor(new User(auditorId));
					conAudit.setAssignedDate(new Date());
					System.out.println(" updated auditorID to " + auditorId);
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
