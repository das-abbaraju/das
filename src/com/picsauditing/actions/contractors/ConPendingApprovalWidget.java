package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;

@SuppressWarnings("serial")
public class ConPendingApprovalWidget extends PicsActionSupport {
	@Autowired
	ContractorOperatorDAO contractorOperatorDAO;

	public String execute() throws Exception {
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public Map<ContractorAccount, Date> getPendingApprovalContractors() {
		
		List<ContractorOperator> coList = new ArrayList <ContractorOperator>();
				
		if (permissions.isPicsEmployee()){
			coList = contractorOperatorDAO.findPendingApprovalContractors(
				permissions.getAccountId(), false, permissions.isCorporate());			
		} else {						
			coList = contractorOperatorDAO.findPendingApprovalContractorsNoDemo(
				permissions.getAccountId(), false, permissions.isCorporate());
		}
		

		Map<ContractorAccount, Date> contractorPendingApproval = new TreeMap<ContractorAccount, Date>();
		ListIterator<ContractorOperator> coIterator = coList.listIterator(); 
		while (coIterator.hasNext() && contractorPendingApproval.size() < 10) {
			ContractorOperator co = coIterator.next();
			contractorPendingApproval.put(co.getContractorAccount(), co.getCreationDate());
		}

		return contractorPendingApproval;
	}
}
