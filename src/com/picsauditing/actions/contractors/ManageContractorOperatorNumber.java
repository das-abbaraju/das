package com.picsauditing.actions.contractors;

import com.picsauditing.jpa.entities.ContractorOperatorNumber;

@SuppressWarnings("serial")
public class ManageContractorOperatorNumber extends ContractorActionSupport {
	private ContractorOperatorNumber number;
	
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}
	
	public String save() {
		if (contractor != null) {
			number.setContractor(contractor);
			dao.save(number);
		}
		
		return BLANK;
	}

	public ContractorOperatorNumber getNumber() {
		return number;
	}

	public void setNumber(ContractorOperatorNumber number) {
		this.number = number;
	}
}
