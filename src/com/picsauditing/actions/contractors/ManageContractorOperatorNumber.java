package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.picsauditing.jpa.entities.ContractorOperatorNumber;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

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
		addErrorIfNonActiveDemoOperator();
		addErrorIfExistsSameTypeAndOperator();

		if (!hasActionErrors()) {
			setContractorIfMissing();
			setOperatorByPermissionsIfMissing();

			number.setAuditColumns(permissions);
			dao.save(number);
		} else {
			return INPUT;
		}

		return SUCCESS;
	}

	public String delete() {
		if (number == null || number.getId() == 0) {
			addActionError(getText("ManageContractorOperatorNumber.MissingNumberToDelete"));
		} else {
			dao.remove(number);
		}

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public List<OperatorAccount> getViewableOperators() {
		List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
		if (permissions.isCorporate()) {
			operators = (List<OperatorAccount>) dao.findWhere(OperatorAccount.class,
					"id IN (" + Strings.implode(permissions.getOperatorChildren()) + ")", 0);
			Iterator<OperatorAccount> iterator = operators.iterator();

			while (iterator.hasNext()) {
				OperatorAccount operator = iterator.next();
				if (!operator.getStatus().isActiveDemo())
					iterator.remove();
			}
		} else {
			operators = (List<OperatorAccount>) dao.findWhere(OperatorAccount.class,
					"type = 'Operator' AND status IN ('Active', 'Demo')", 0);
		}

		Collections.sort(operators);
		return operators;
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

	private void addErrorIfNonActiveDemoOperator() {
		if (number.getOperator() == null && !permissions.isOperatorCorporate())
			addActionError(getText("ManageContractorOperatorNumber.MissingOperator"));
		if (number.getOperator() != null && !number.getOperator().getStatus().isActiveDemo())
			addActionError(getText("ManageContractorOperatorNumber.ActiveDemoOperator"));
	}

	private void addErrorIfExistsSameTypeAndOperator() {
		if (contractor != null && number != null && number.getOperator() != null) {
			for (ContractorOperatorNumber contractorOperatorNumber : contractor.getContractorOperatorNumbers()) {
				if (contractorOperatorNumber.getOperator().equals(number.getOperator())) {
					if (contractorOperatorNumber.getType() == number.getType()) {
						addActionError(getText("ManageContractorOperatorNumber.NumberTypeAlreadyExists"));
					}
				}
			}
		}
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
