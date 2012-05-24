package com.picsauditing.access;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class Contact extends PicsActionSupport {
	@Autowired
	private AccountUserDAO accountUserDAO;

	private ContractorAccount contractorAccount;
	private User accountRepUser;

	@Anonymous
	public String execute() throws Exception {
		loadPermissions(false);
		if (permissions.isLoggedIn()) {
			User user = getUser();
			if (permissions.isContractor())
				contractorAccount = (ContractorAccount) user.getAccount();
			else if (permissions.isOperatorCorporate()) {
				List<AccountUser> accountUsers = accountUserDAO.findByAccount(user.getAccount().getId());
				for (AccountUser accountUser : accountUsers) {
					if (accountUser.getRole().getDescription().equals("Account Manager")) {
						if (accountUser.getEndDate().after(new Date())) {
							accountRepUser = accountUser.getUser();
							break;
						}
					}
				}
			}
		}

		return SUCCESS;
	}


	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public User getAccountRepUser() {
		return accountRepUser;
	}
}
