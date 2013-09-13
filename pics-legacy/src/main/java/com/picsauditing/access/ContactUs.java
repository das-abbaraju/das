package com.picsauditing.access;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class ContactUs extends PicsActionSupport {
	@Autowired
	private AccountUserDAO accountUserDAO;

	private OperatorAccount operatorAccount;
	private ContractorAccount contractorAccount;
	private AccountUser accountRep;

	@Anonymous
	public String execute() throws Exception {

		if (permissions.isLoggedIn()) {
			User user = getUser();
			if (permissions.isContractor())
				contractorAccount = (ContractorAccount) user.getAccount();
			else if (permissions.isOperatorCorporate()) {
				operatorAccount = (OperatorAccount) user.getAccount();
				List<AccountUser> accountUsers = accountUserDAO.findByAccount(operatorAccount.getId());
				for (AccountUser accountUser : accountUsers) {
					if (accountUser.getRole().getDescription().equals("Account Manager")) {
						if (accountUser.getEndDate().after(new Date())) {
							accountRep = accountUser;
							break;
						}
					}
				}
			}
		}

		return SUCCESS;
	}

	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public AccountUser getAccountRep() {
		return accountRep;
	}
}
