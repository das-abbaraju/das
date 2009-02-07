package com.picsauditing.actions.forms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorFormDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorForm;

@SuppressWarnings("serial")
public class ContractorForms extends ContractorActionSupport {
	OperatorFormDAO operatorFormDAO;
	
	public ContractorForms(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			OperatorFormDAO operatorFormDAO) {
		super(accountDao, auditDao);
		this.operatorFormDAO = operatorFormDAO;
	}
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		findContractor();
		
		subHeading = "Forms & Documents";
		return SUCCESS;
	}
	
	public List<OperatorForm> getForms() {
		Set<Integer> ids = new HashSet<Integer>();
		for(ContractorOperator co : contractor.getOperators()) {
			ids.add(co.getOperatorAccount().getId());
		}
		return operatorFormDAO.findByOperators(ids);
	}
}
