package com.picsauditing.access;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.picsauditing.model.i18n.LanguageModel;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.toggle.FeatureToggle;

@SuppressWarnings("serial")
public class Contact extends PicsActionSupport {
	@Autowired
	private AccountUserDAO accountUserDAO;
	@Autowired
	private LanguageModel languageModel;

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

	public String getMibewChatEnabled() {
		return FeatureToggle.TOGGLE_MIBEW_CHAT;
	}

    public String getDisplayLanguage() {
    	Locale locale = getLocaleStatic();
		return languageModel.getClosestVisibleLocale(locale).getDisplayLanguage();
    }
}
