package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class SubcontractorsFlagMatrixWidget extends PicsActionSupport {
	private OperatorAccount operator;
	private List<ContractorAccount> subcontractors = null;
	private List<OperatorAccount> gcContractorOperators = null;
	
	public String execute() throws Exception {
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;
		
		operator = dao.find(OperatorAccount.class, permissions.getAccountId());
		return SUCCESS;
	}
	
	public OperatorAccount getOperator() {
		return operator;
	}
	
	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public List<ContractorAccount> getSubcontractors() {
		if (subcontractors == null) {
			subcontractors = new ArrayList<ContractorAccount>();
			
			for (ContractorOperator conOp : operator.getContractorOperators()) {
				subcontractors.add(conOp.getContractorAccount());
			}
		}
		
		Collections.sort(subcontractors, new Comparator<ContractorAccount>() {
			public int compare(ContractorAccount contractorOne, ContractorAccount contractorTwo) {
				return contractorOne.getName().toLowerCase().compareTo(contractorTwo.getName().toLowerCase());
			}
		});
		
		return subcontractors;
	}
	
	public void setSubcontractors(List<ContractorAccount> subcontractors) {
		this.subcontractors = subcontractors;
	}
	
	public List<OperatorAccount> getGcContractorOperators() {
		if (gcContractorOperators == null) {
			gcContractorOperators = operator.getGcContractorOperators();
		}
		return gcContractorOperators;
	}

	public void setGcContractorOperators(List<OperatorAccount> gcContractorOperators) {
		this.gcContractorOperators = gcContractorOperators;
	}

}
