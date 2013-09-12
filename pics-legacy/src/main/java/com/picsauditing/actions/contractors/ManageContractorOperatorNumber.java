package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorNumber;
import com.picsauditing.jpa.entities.ContractorOperatorNumberType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageContractorOperatorNumber extends ContractorActionSupport {
	@Autowired
	private UserSwitchDAO userSwitchDAO;

	private ContractorOperatorNumber number = new ContractorOperatorNumber();
	private int clientSite;
	private String clientType;

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String edit() {
		if (number.getId() > 0 && Strings.isEmpty(clientType) && clientSite == 0) {
			clientSite = number.getOperator().getId();
			clientType = number.getType().toString();
		}

		return INPUT;
	}

	public String save() {
		OperatorAccount operator = operatorDAO.find(clientSite);

		number.setOperator(operator);
		number.setType(ContractorOperatorNumberType.valueOf(clientType));

		checkForErrors();

		if (!hasActionErrors()) {
			setContractorIfMissing();
			setOperatorByPermissionsIfMissing();

			number.setAuditColumns(permissions);
			dao.save(number);

			if (!contractor.getContractorOperatorNumbers().contains(number)) {
				contractor.getContractorOperatorNumbers().add(number);
			}
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

	public List<OperatorAccount> getViewableOperators() {
		List<OperatorAccount> operators = new ArrayList<OperatorAccount>();

		for (ContractorOperator contractorOperator : contractor.getOperators()) {
			if (contractorOperator.getOperatorAccount().getStatus().isActiveOrDemo()) {
				if (isViewableByPermissions(contractorOperator)) {
					operators.add(contractorOperator.getOperatorAccount());
				}
			}
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

	public List<User> getUsersList() {

		List<User> usersAndSwitchTos = userDAO.findByAccountID(clientSite, "Yes", "No");
		List<User> switchTos = userSwitchDAO.findUsersBySwitchToAccount(clientSite);

		usersAndSwitchTos.addAll(switchTos);
		return usersAndSwitchTos;
	}

	private void checkForErrors() {
		addErrorIfNullContractor();
		addErrorIfNonActiveDemoOperator();
		addErrorIfNullType();
		addErrorIfExistsSameTypeAndOperator();
		addErrorIfNullValue();
	}

	private void addErrorIfNullContractor() {
		if (contractor == null)
			addActionError(getText("ManageContractorOperatorNumber.MissingContractor"));
	}

	private void addErrorIfNonActiveDemoOperator() {
		if (number.getOperator() == null && !permissions.isOperatorCorporate())
			addActionError(getText("ManageContractorOperatorNumber.MissingOperator"));
		if (number.getOperator() != null && !number.getOperator().getStatus().isActiveOrDemo())
			addActionError(getText("ManageContractorOperatorNumber.ActiveDemoOperator"));
	}

	private void addErrorIfNullType() {
		if (number.getType() == null)
			addActionError(getText("ManageContractorOperatorNumber.MissingType"));
	}

	private void addErrorIfExistsSameTypeAndOperator() {
		if (contractor != null && number != null && number.getOperator() != null) {
			for (ContractorOperatorNumber contractorOperatorNumber : contractor.getContractorOperatorNumbers()) {
				if (contractorOperatorNumber.getOperator().equals(number.getOperator())) {
					if (contractorOperatorNumber.getType() == number.getType()
							&& contractorOperatorNumber.getId() != number.getId()) {
						addActionError(getText("ManageContractorOperatorNumber.NumberTypeAlreadyExists"));
					}
				}
			}
		}
	}

	private void addErrorIfNullValue() {
		if (Strings.isEmpty(number.getValue()))
			addActionError(getText("ManageContractorOperatorNumber.MissingNumber"));
	}

	private void setContractorIfMissing() {
		if (number.getContractor() == null)
			number.setContractor(contractor);
	}

	private void setOperatorByPermissionsIfMissing() {
		if (number.getOperator() == null && permissions.isOperatorCorporate())
			number.setOperator(dao.find(OperatorAccount.class, permissions.getAccountId()));
	}

	private boolean isViewableByPermissions(ContractorOperator contractorOperator) {
		return (permissions.isCorporate() && permissions.getOperatorChildren().contains(
				contractorOperator.getOperatorAccount().getId()))
				|| permissions.isPicsEmployee();
	}

	public int getClientSite() {
		return clientSite;
	}

	public void setClientSite(int clientSite) {
		this.clientSite = clientSite;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
}
