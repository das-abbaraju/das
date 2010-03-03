package com.picsauditing.actions.forms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorFormDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
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
		
		ids.add(Account.PicsID);
		
		for(ContractorOperator co : contractor.getNonCorporateOperators()) {
			// Add this contractor's operator(s)
			ids.add(co.getOperatorAccount().getId());
			for(Facility f : co.getOperatorAccount().getCorporateFacilities()) {
				// Add this operator's corporate parent(s) too
				ids.add(f.getCorporate().getId());
			}
		}
		return operatorFormDAO.findByOperators(ids);
	}
}
