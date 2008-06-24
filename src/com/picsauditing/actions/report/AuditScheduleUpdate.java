package com.picsauditing.actions.report;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

public class AuditScheduleUpdate extends ActionSupport implements
		ServletRequestAware {
	protected ContractorAudit contractorAudit = null;
	protected ContractorAuditDAO dao = null;

	protected HttpServletRequest request;

	public AuditScheduleUpdate(ContractorAuditDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {
		String auditID = request.getParameter("auditID").toString();
		String type = request.getParameter("type").toString();
		contractorAudit = dao.find(Integer.parseInt(auditID));
		if (type.equals("c"))
			contractorAudit.setContractorConfirm(new Date());
		if (type.equals("a"))
			contractorAudit.setAuditorConfirm(new Date());
		dao.save(contractorAudit);
		return SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
