package com.picsauditing.actions.audits;

import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

// Samples
// AuditSaveAjax.action?audit.id=3260&audit.scheduledDate=01/01/2008&audit.auditor.id=907
// AuditSaveAjax.action?audit.id=3260&audit.scheduledDate=&audit.auditor=

public class AuditSave extends PicsActionSupport implements Preparable {
	ContractorAudit audit;
	ContractorAuditDAO dao;
	OperatorAccountDAO opDao;
	ContractorAccountDAO conDao;

	protected int contractorID = 0;
	
	public AuditSave(ContractorAuditDAO dao, OperatorAccountDAO opDao, ContractorAccountDAO conDao) {
		this.dao = dao;
		this.opDao = opDao;
		this.conDao = conDao;
	}

	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		// TODO add security
		// TODO return an appropriate message

		if (audit.getAuditor() != null
				&& audit.getAuditor().getId() != 0 ) {
			audit.setAuditor(getUser(audit.getAuditor().getId()) );
		} else {
			audit.setAuditor(null);
		}
		//weird workaround.  conAudit.contractorAccount.id was not properly setting off the request,
		// so we're doing it manually.  if we changed the name of the parameter to:
		//	conAudit.contractorAccount.trade, which is in the ContractorAccount class, it sets fine.
		//	conAudit.contractorAccount.name, which is in the Account class, it sets fine
		//	conAudit.contractorAccount.id, which is in the Account class, the id is not set.  
		// We think it might be related to the fact that it's either not nullable or a join column in 
		//	a mapped inheritance relationship. 
		if( contractorID != 0 )
		{
			audit.setContractorAccount(conDao.find(contractorID));
		}
		
		dao.save(audit);
		this.message = "Success";

		return SUCCESS;
	}

	public void prepare() throws Exception {
		String[] ids = (String[]) ActionContext.getContext().getParameters().get("audit.id");

		if (ids != null && ids.length > 0) {
			int id = new Integer(ids[0]).intValue();
			audit = dao.find(id);
			dao.clear();
		}
	}

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	public ContractorAudit getConAudit() {
		return audit;
	}

	public void setConAudit(ContractorAudit audit) {
		this.audit = audit;
	}


	public int getContractorID() {
		return contractorID;
	}


	public void setContractorID(int contractorID) {
		this.contractorID = contractorID;
	}


}
