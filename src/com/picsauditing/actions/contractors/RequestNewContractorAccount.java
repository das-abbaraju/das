package com.picsauditing.actions.contractors;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractorAccount extends ContractorActionSupport {
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;

	private ContractorAccount requestedContractor = new ContractorAccount();
	private ContractorOperator requestRelationship = new ContractorOperator();
	// Email
	private EmailBuilder emailBuilder = new EmailBuilder();

	@Override
	public String execute() throws Exception {
		checkPermission();

		setRequestedBy();

		initializeRequest();

		return SUCCESS;
	}

	private void setRequestedBy() {
		if (permissions.isOperatorCorporate()) {
			OperatorAccount requestedBy = operatorDAO.find(permissions.getAccountId());

			if (permissions.isOperator() && requestedContractor.getId() == 0
					&& requestedContractor.getRequestedBy() == null) {
				requestedContractor.setRequestedBy(requestedBy);
			}

			for (ContractorOperator contractorOperator : requestedContractor.getOperators()) {
				if (contractorOperator.getOperatorAccount().equals(requestedContractor.getRequestedBy())) {
					requestRelationship = contractorOperator;
				}
			}
		}
	}

	private void initializeRequest() {
		if (requestedContractor.getId() == 0) {
			requestedContractor.setStatus(AccountStatus.Requested);

			ContractorRegistrationRequest request = new ContractorRegistrationRequest();
			requestedContractor.getRegistrationRequests().add(request);
		}
	}

	public String save() {
		return SUCCESS;
	}

	public ContractorAccount getRequestedContractor() {
		return requestedContractor;
	}

	public void setRequestedContractor(ContractorAccount requestedContractor) {
		this.requestedContractor = requestedContractor;
	}

	public ContractorOperator getRequestRelationship() {
		return requestRelationship;
	}

	public void setRequestRelationship(ContractorOperator requestRelationship) {
		this.requestRelationship = requestRelationship;
	}

	public String getEmailPreview() {
		return null;
	}

	public boolean isContactable() {
		if (permissions.isOperatorCorporate()) {
			return false;
		}

		if (requestedContractor.getId() == 0) {
			return false;
		}

		if (requestedContractor.getPrimaryContact() == null) {
			return false;
		}

		if (Strings.isEmpty(requestedContractor.getPrimaryContact().getPhone())) {
			return false;
		}

		return true;
	}

	private void checkPermission() throws NoRightsException {
		if (!permissions.isOperatorCorporate() && !permissions.isPicsEmployee()) {
			throw new NoRightsException(getText("global.Operators"));
		}

		if (permissions.isOperatorCorporate() && requestedContractor.getRequestedBy() != null
				&& !permissions.getVisibleAccounts().contains(requestedContractor.getRequestedBy().getId())) {
			throw new NoRightsException(getText("AccountType.Admin"));
		}
	}

	private OperatorForm getContractorLetter() {
		return null;
	}

	private OperatorAccount findOperator() {
		OperatorAccount operator = new OperatorAccount();

		if (requestRelationship.getOperatorAccount() != null) {
			operator = requestRelationship.getOperatorAccount();
		}

		if (requestedContractor.getRequestedBy() != null) {
			operator = requestedContractor.getRequestedBy();
		}

		if (permissions.isOperatorCorporate() && operator == null) {
			operator = operatorDAO.find(permissions.getAccountId());
		}

		return operator;
	}
}
