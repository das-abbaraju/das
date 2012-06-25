package com.picsauditing.selenium;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public class ResetSelenium extends PicsActionSupport {

	private List<SeleniumDeletable> accountsInDB;
	private String userSpecifiedAccount = null;

	private List<Integer> accountsSelectedForDeletion;
	private List<Integer> usersSelectedForDeletion;
	private List<Integer> employeesSelectedForDeletion;
	@Autowired private SeleniumDAO SD;

	@Anonymous
	public String execute() {
		establishAccountsAvailableForDeletion();
		return SUCCESS;
	}

	@Anonymous
	public String delete() throws Exception {
		establishAccountsAvailableForDeletion();

		if (null != userSpecifiedAccount)
			deleteSingleAccount(userSpecifiedAccount);
		else
			performMultipleDeletion();

		return setUrlForRedirect("ResetSelenium.action");
	}

	@Anonymous
	public String deleteAll() throws Exception {
		SD.delete(SD.availableTestingReferences());
		return setUrlForRedirect("ResetSelenium.action");
	}

	private void performMultipleDeletion() throws Exception {
		List<SeleniumDeletable> deletables = new ArrayList<SeleniumDeletable>();
		for (SeleniumDeletable deletable : accountsInDB)
			if ((deletable.IDisIn(accountsSelectedForDeletion) && deletable.isAnAccount())
					|| (deletable.IDisIn(employeesSelectedForDeletion) && deletable.isAnEmployee())
					|| (deletable.IDisIn(usersSelectedForDeletion) && deletable.isUser()))
				deletables.add(deletable);

		if (!deletables.isEmpty()) SD.delete(deletables);
	}

	private void deleteSingleAccount(String name) throws Exception {
		for (SeleniumDeletable account : accountsInDB)
			if (account.getName().equalsIgnoreCase(name)) {
				List<SeleniumDeletable> deleteMe = new ArrayList<SeleniumDeletable>();
				deleteMe.add(account);
				SD.delete(deleteMe);
				return;
			}
	}

	private void establishAccountsAvailableForDeletion() {
		if (null == accountsInDB || accountsInDB.isEmpty())
			accountsInDB = SD.availableTestingReferences();
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

	public void setDeleteAccount(String account) {
		userSpecifiedAccount = account;
	}
	
	private void setSeleniumDAO(SeleniumDAO SD) {
		this.SD = SD;
	}
}