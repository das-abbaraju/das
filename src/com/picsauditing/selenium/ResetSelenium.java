package com.picsauditing.selenium;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public class ResetSelenium extends PicsActionSupport {
	
	private List<SeleniumTestingAccount> accountsInDB;
	private List<String> accountsSelectedForDeletion; //This should always be a list of account IDs.
	private String userSpecifiedAccount = null; //This will be an account name passed in as a request parameter.
	
	@Anonymous
	public String execute() {
//		if (null != accountsInDB) accountsInDB.clear();
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
		List<String> allAccountIDs = new ArrayList<String>();
		for (SeleniumTestingAccount account : accountsInDB)
			allAccountIDs.add(account.getId());
		accountsSelectedForDeletion = allAccountIDs;
		return delete();
	}

	private void performMultipleDeletion() throws Exception {
		List<SeleniumTestingAccount> deletables = new ArrayList<SeleniumTestingAccount>();
		for (SeleniumTestingAccount account : accountsInDB)
			if (accountsSelectedForDeletion.contains(account.getId())) 
				deletables.add(account);
		
		SeleniumDAO.delete(deletables);
	}

	private void deleteSingleAccount(String name) throws Exception {
		for (SeleniumTestingAccount account : accountsInDB)
			if (account.getName().equalsIgnoreCase(name)) {
				List<SeleniumTestingAccount> deleteMe = new ArrayList<SeleniumTestingAccount>();
				deleteMe.add(account);
				SeleniumDAO.delete(deleteMe);
				return;
			}
	}
		
	private void establishAccountsAvailableForDeletion () {
		if (null == accountsInDB || accountsInDB.isEmpty()) accountsInDB = SeleniumDAO.AvailableTestingAccounts();
	}
	
	public List<SeleniumTestingAccount> getDBAccounts () {
		return accountsInDB;
	}
	
	public void setDBAccounts (List<String> accounts) {
		accountsSelectedForDeletion = accounts;
	}
	
	public void setDeleteAccount (String account) {
		userSpecifiedAccount = account;
	}
}


//Database db = new Database();
//
//SelectSQL accountSQL = new SelectSQL("accounts");
//accountSQL.addField("id");
//accountSQL.addField("type");
//accountSQL.addField("name");
//accountSQL.addWhere("name LIKE 'Selenium Test%'");
//List<BasicDynaBean> select = db.select(accountSQL.toString(), false);
//
//Set<Integer> accounts = new HashSet<Integer>();
//for (BasicDynaBean row : select) {
//	accounts.add(Integer.parseInt(row.get("id").toString()));
//	addActionMessage("Deleting " + row.get("type") + " - " + row.get("name"));
//}
//
//if (accounts.size() == 0) {
//	addActionMessage("Found 0 Selenium test accounts");
//	return SUCCESS;
//}
//String accountIDs = Strings.implodeForDB(accounts, ",");
//{
//	Delete t = new Delete("contractor_audit_operator_permission");
//	t.addJoin("JOIN contractor_audit_operator cao ON cao.id = t.caoID");
//	t.addJoin("JOIN contractor_audit ca ON ca.id = cao.auditID");
//	t.addJoin("WHERE ca.conID IN (" + accountIDs + ")");
//	t.delete(db);
//	t.table = "contractor_audit_operator_workflow";
//	t.delete(db);
//}
//{
//	Delete t = new Delete("audit_cat_data");
//	t.addJoin("JOIN contractor_audit ca ON ca.id = t.auditID");
//	t.addJoin("WHERE ca.conID IN (" + accountIDs + ")");
//	t.delete(db);
//	t.table = "contractor_audit_operator";
//	t.delete(db);
//	t.table = "pqfdata";
//	t.delete(db);
//}
//{
//	Delete t = new Delete("contractor_audit");
//	t.addJoin("WHERE t.conID IN (" + accountIDs + ")");
//	t.delete(db);
//	t.table = "contractor_fee";
//	t.delete(db);
//	t.table = "email_queue";
//	t.delete(db);
//	t.table = "contractor_trade";
//	t.delete(db);
//}
//{
//	Delete t = new Delete("generalcontractors");
//	t.addJoin("WHERE t.genID IN (" + accountIDs + ") OR t.subID IN (" + accountIDs + ")");
//	t.delete(db);
//}
//{
//	Delete t = new Delete("facilities");
//	t.addJoin("WHERE t.corporateID IN (" + accountIDs + ") OR t.opID IN (" + accountIDs + ")");
//	t.delete(db);
//}
//{
//	Delete t = new Delete("invoice_item");
//	t.addJoin("JOIN invoice i ON i.id = t.invoiceID");
//	t.addJoin("WHERE i.accountID IN (" + accountIDs + ")");
//	t.delete(db);
//}
//{
//	Delete t = new Delete("invoice");
//	t.addJoin("WHERE t.accountID IN (" + accountIDs + ")");
//	t.delete(db);
//	t.table = "users";
//	t.delete(db);
//}
//{
//	Delete t = new Delete("app_index");
//	t.addJoin("WHERE t.foreignKey IN (" + accountIDs + ")");
//	t.delete(db);
//}
//{
//	Delete t = new Delete("accounts");
//	t.addJoin("WHERE t.id IN (" + accountIDs + ")");
//	t.delete(db);
//	// Looks like these might cascade delete
//	t.table = "operators";
//	t.delete(db);
//	t.table = "contractor_info";
//	t.delete(db);
//}