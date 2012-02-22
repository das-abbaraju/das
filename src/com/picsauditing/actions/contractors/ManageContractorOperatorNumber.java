package com.picsauditing.actions.contractors;

import com.picsauditing.jpa.entities.ContractorOperatorNumber;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class ManageContractorOperatorNumber extends ContractorActionSupport {
	private ContractorOperatorNumber number;

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String edit() {
		return INPUT;
	}

	public String save() {
		addErrorIfNullContractor();
		addErrorIfNullOperator();

		if (getActionErrors().size() == 0) {
			setContractorIfMissing();
			setOperatorByPermissionsIfMissing();

			number.setAuditColumns(permissions);
			dao.save(number);
		}

		if (getActionErrors().size() > 0)
			return INPUT;

		return SUCCESS;
	}

	public ContractorOperatorNumber getNumber() {
		return number;
	}

	public void setNumber(ContractorOperatorNumber number) {
		this.number = number;
	}

	private void addErrorIfNullContractor() {
		if (contractor == null)
			addActionError(getText("ManageContractorOperatorNumber.MissingContractor"));
	}

	private void addErrorIfNullOperator() {
		if (number.getOperator() == null && !permissions.isOperatorCorporate())
			addActionError(getText("ManageContractorOperatorNumber.MissingOperator"));
	}

	private void setContractorIfMissing() {
		if (number.getContractor() == null)
			number.setContractor(contractor);
	}

	private void setOperatorByPermissionsIfMissing() {
		if (number.getOperator() == null && permissions.isOperatorCorporate())
			number.setOperator(dao.find(OperatorAccount.class, permissions.getAccountId()));
	}
}
