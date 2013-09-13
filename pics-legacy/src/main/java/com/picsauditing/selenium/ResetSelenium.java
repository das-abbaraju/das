package com.picsauditing.selenium;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.ApiRequired;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsApiSupport;
import org.apache.struts2.interceptor.ParameterAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public class ResetSelenium extends PicsApiSupport implements ParameterAware {

	private List<SeleniumDeletable> accountsInDB;
	private String userSpecifiedAccount = null;

	private List<Integer> accountsSelectedForDeletion;
	private List<Integer> usersSelectedForDeletion;
	private List<Integer> employeesSelectedForDeletion;
	@Autowired
	private SeleniumDAO seleniumDao;

    @Override
    @ApiRequired
    // TODO When the permission is actually available on live... @RequiredPermission(value = OpPerms.SeleniumTest)
	public String execute() throws Exception {
		establishAccountsAvailableForDeletion();

        if (null != userSpecifiedAccount) {
            if (userSpecifiedAccount.equalsIgnoreCase("all")) {
                seleniumDao.delete(seleniumDao.availableTestingReferences());
            } else {
                deleteSingleAccount(userSpecifiedAccount);
            }
            return setUrlForRedirect("ResetSelenium.action?apiKey=" + this.getApiKey());
        } else {
            return SUCCESS;
        }
	}

	@ApiRequired
    // TODO When the permission is actually available on live... @RequiredPermission(value = OpPerms.SeleniumTest)
	public String delete() throws Exception {
		establishAccountsAvailableForDeletion();

		if (null != userSpecifiedAccount) {
			deleteSingleAccount(userSpecifiedAccount);
		} else {
			performMultipleDeletion();
		}

		return setUrlForRedirect("ResetSelenium.action");
	}

    @ApiRequired
    // TODO When the permission is actually available on live... @RequiredPermission(value = OpPerms.SeleniumTest)
	public String deleteAll() throws Exception {
		seleniumDao.delete(seleniumDao.availableTestingReferences());
		return setUrlForRedirect("ResetSelenium.action");
	}

	private void performMultipleDeletion() throws Exception {
		List<SeleniumDeletable> deletables = new ArrayList<SeleniumDeletable>();
		for (SeleniumDeletable deletable : accountsInDB) {
			if ((deletable.IDisIn(accountsSelectedForDeletion) && deletable.isAnAccount())
					|| (deletable.IDisIn(employeesSelectedForDeletion) && deletable.isAnEmployee())
					|| (deletable.IDisIn(usersSelectedForDeletion) && deletable.isUser())) {
				deletables.add(deletable);
			}
		}
		if (!deletables.isEmpty()) {
			seleniumDao.delete(deletables);
		}
	}

	private void deleteSingleAccount(String name) throws Exception {
		for (SeleniumDeletable account : accountsInDB)
			if (account.getName().equalsIgnoreCase(name)) {
				List<SeleniumDeletable> deleteMe = new ArrayList<SeleniumDeletable>();
				deleteMe.add(account);
				seleniumDao.delete(deleteMe);
				return;
			}
	}

	private void establishAccountsAvailableForDeletion() {
		if (null == accountsInDB || accountsInDB.isEmpty()) {
			accountsInDB = seleniumDao.availableTestingReferences();
		}
	}

	public List<SeleniumDeletable> getDBAccounts() {
		return accountsInDB;
	}

	public void setDBUsers(List<Integer> users) {
		usersSelectedForDeletion = users;
	}

	public void setDBEmployees(List<Integer> emp) {
		employeesSelectedForDeletion = emp;
	}

	public void setDBAccounts(List<Integer> accounts) {
		accountsSelectedForDeletion = accounts;
	}

    public String getDeleteAccount() {
        return this.userSpecifiedAccount;
    }

	public void setDeleteAccount(String account) {
		userSpecifiedAccount = account;
	}
	
	private void setSeleniumDAO(SeleniumDAO seleniumDao) {
		this.seleniumDao = seleniumDao;
	}
}