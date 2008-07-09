package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditEdit extends AuditActionSupport {

	protected OperatorAccountDAO opDao = null;
	protected ContractorAccountDAO conDao = null;
	protected AuditTypeDAO auditTypeDao = null;
	
	public AuditEdit(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, OperatorAccountDAO opDao, ContractorAccountDAO conDao, AuditTypeDAO auditTypeDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.opDao = opDao;
		this.conDao = conDao;
		this.auditTypeDao = auditTypeDao;
	}

	
	public String execute() throws Exception {

		if (!forceLogin())
			return LOGIN;
		
		
		if( auditID != 0 )
		{
			findConAudit();
		}
		return SUCCESS;
	}

	public List<OperatorAccount> getAllOperators()
	{
		return opDao.findWhere(true, null);
	}
	
	public List<ContractorAccount> getAllContractors()
	{
		return conDao.findWhere(null);
	}
	public List<AuditType> getAllAuditTypes()
	{
		return auditTypeDao.findAll();
	}
	
}
