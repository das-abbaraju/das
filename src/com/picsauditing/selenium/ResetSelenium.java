package com.picsauditing.selenium;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Account;

@SuppressWarnings("serial")
public class ResetSelenium extends PicsActionSupport {
	
	private List<Account> accountsInDB;
	private List<Integer> accountsSelectedForDeletion; //This should always be a list of account IDs.
	private String userSpecifiedAccount = null; //This will be an account name passed in as a request parameter.
	
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
		else if (null != accountsSelectedForDeletion) 
			performMultipleDeletion();
		
		return redirect("ResetSelenium.action");
	}
	
	@Anonymous
	public String deleteAll () throws Exception {
		establishAccountsAvailableForDeletion();
		List<Integer> allAccountIDs = new ArrayList<Integer>();
		for (Account account : accountsInDB)
			allAccountIDs.add(account.getId());
		accountsSelectedForDeletion = allAccountIDs;
		return delete();
	}

	private void performMultipleDeletion() throws Exception {
		List<Account> deletables = new ArrayList<Account>();
		for (Account account : accountsInDB)
			if (accountsSelectedForDeletion.contains(account.getId())) 
				deletables.add(account);
		
		SeleniumDAO.delete(deletables);
	}

	private void deleteSingleAccount(String name) throws Exception {
		for (Account account : accountsInDB)
			if (account.getName().equalsIgnoreCase(name)) {
				List<Account> deleteMe = new ArrayList<Account>();
				deleteMe.add(account);
				SeleniumDAO.delete(deleteMe);
				return;
			}
	}
		
	private void establishAccountsAvailableForDeletion () {
		if (null == accountsInDB || accountsInDB.isEmpty()) accountsInDB = SeleniumDAO.AvailableTestingAccounts();
	}
	
	public List<Account> getDBAccounts () {
		return accountsInDB;
	}
	
	public void setDBAccounts (List<Integer> accounts) {
		accountsSelectedForDeletion = accounts;
	}
	
	public void setDeleteAccount (String account) {
		userSpecifiedAccount = account;
	}
}