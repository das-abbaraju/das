package com.picsauditing.util;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.jpa.entities.Account;

public class NameIndexUpdater extends PicsActionSupport {
	private AccountDAO accountDAO;
	private int count = 0;
	private int currentID = 0;

	public NameIndexUpdater(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}

	public String execute() throws Exception {
		// Get all the accounts 500 at a time and update the nameIndexes
		// Keep track of how long it takes to fetch
		Long start = System.currentTimeMillis();
		List<Account> accounts = accountDAO.findSetWhere("a.id > 0 AND a.nameIndex IS NULL", 1000);
		System.out.println((System.currentTimeMillis() - start) / 1000.0 + " seconds to fetch 1000");

		while (accounts.size() > 0) {
			start = System.currentTimeMillis();
			for (Account account : accounts) {
				account.setNameIndex();
				accountDAO.save(account);
				currentID = account.getId();
				count++;
			}
			System.out.println((System.currentTimeMillis() - start) / 1000.0 + " seconds to update nameindexes");
			// Clear out persistent objects from memory?
			accountDAO.clear();

			start = System.currentTimeMillis();
			accounts = accountDAO.findSetWhere("a.id > " + currentID + " AND a.nameIndex IS NULL", 1000);
			System.out.println((System.currentTimeMillis() - start) / 1000.0 + " seconds to fetch 1000");
		}

		addActionMessage("Updated " + count + " nameindexes");

		return BLANK;
	}
}