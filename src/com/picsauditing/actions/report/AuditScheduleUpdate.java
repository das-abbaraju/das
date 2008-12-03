package com.picsauditing.actions.report;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class AuditScheduleUpdate extends PicsActionSupport implements ServletRequestAware {
	protected ContractorAudit contractorAudit = null;
	protected ContractorAuditDAO dao = null;

	protected HttpServletRequest request;

	public AuditScheduleUpdate(ContractorAuditDAO dao) {
		this.dao = dao;
	}

	public String execute() {
		String auditIDString = request.getParameter("auditID");
		if (auditIDString == null || auditIDString.length() == 0) {
			addActionError("Missing auditID, invalid URL");
			return SUCCESS;
		}

		int auditID = Integer.parseInt(auditIDString);
		String type = request.getParameter("type");
		if (type == null || type.length() != 1) {
			addActionError("Missing type, invalid URL");
			return SUCCESS;
		}

		contractorAudit = dao.find(auditID);
		if (contractorAudit == null) {
			addActionError("Missing audit, invalid URL");
			return SUCCESS;
		}

		if (type.equals("c")) {
			contractorAudit.setContractorConfirm(new Date());
			String note = " Confirmed the " + contractorAudit.getAuditType().getAuditName();

			contractorAudit.getContractorAccount().addNote(null, note);
		}
		if (type.equals("a"))
			contractorAudit.setAuditorConfirm(new Date());

		dao.save(contractorAudit);
		return SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public void setAuditID(String auditID) {
	}

	public void setType(String type) {
	}

	public void setKey(String key) {
	}
}
