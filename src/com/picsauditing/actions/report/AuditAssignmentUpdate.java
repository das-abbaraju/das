package com.picsauditing.actions.report;

import java.util.Map;

import org.apache.struts2.interceptor.ParameterAware;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;

public class AuditAssignmentUpdate extends PicsActionSupport implements Preparable, ParameterAware {

	protected ContractorAudit contractorAudit = null;
	protected User auditor = null;
	
	protected ContractorAuditDAO dao = null;
	protected UserDAO userDao = null;

	protected Map parameters = null;
	
	
	public AuditAssignmentUpdate( ContractorAuditDAO dao, UserDAO userDao )
	{
		this.dao = dao;
		this.userDao = userDao;
	}
	
	
	
	public String execute() throws Exception 
	{
		auditor = userDao.find(auditor.getId());
		
		contractorAudit.setAuditor(auditor);
		
		dao.save(contractorAudit);
		return SUCCESS;
	}

	
	@Override
	public void prepare() throws Exception {
		
		String[] ids = (String[]) parameters.get("contractorAudit.id");
		
		if( ids != null && ids.length > 0 )
		{
			int auditId = Integer.parseInt(ids[0]);
			contractorAudit = dao.find( auditId );
		}
	}
	


	public ContractorAudit getContractorAudit() {
		return contractorAudit;
	}
	public void setContractorAudit(ContractorAudit contractorAudit) {
		this.contractorAudit = contractorAudit;
	}
	public Map getParameters() {
		return parameters;
	}
	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}



	public User getAuditor() {
		return auditor;
	}
	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}
}

