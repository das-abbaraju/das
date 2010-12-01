package com.picsauditing.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.picsauditing.actions.Indexer;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.IndexableDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.search.Database;

public class IndexerController {

	private AccountDAO accountDAO;
	private UserDAO userDAO;
	private EmployeeDAO empDAO;
	private Indexer indexer;

	public IndexerController(AccountDAO accountDAO, UserDAO userDAO,
			EmployeeDAO empDao) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.empDAO = empDao;
		this.indexer = new Indexer(accountDAO, userDAO, empDao);
	}

	public void runAll(String toRun, boolean runStats) throws SQLException {
		if (Indexer.isRunning()) {
			return;
		} else
			Indexer.setRunning(true);
		System.out.println("Starting Indexer");
		Indexer.setIndexTables(new HashMap<String, IndexableDAO>());
		if (toRun == null) {
			Indexer.getIndexTables().put("accounts", accountDAO);
			Indexer.getIndexTables().put("users", userDAO);
			Indexer.getIndexTables().put("employee", empDAO);
		} else {
			if (toRun.equals("accounts"))
				Indexer.getIndexTables().put("accounts", accountDAO);
			else if (toRun.equals("users"))
				Indexer.getIndexTables().put("users", userDAO);
			else if (toRun.equals("employee"))
				Indexer.getIndexTables().put("employee", empDAO);
		}
		for (Entry<String, IndexableDAO> entry : Indexer.getIndexTables()
				.entrySet()) {
			indexer.runIndexer(indexer.getIndexable(entry.getKey()), entry
					.getValue(), entry.getKey());
		}
		if (runStats) {
			runStats = false;
			Database db = new Database();
			for (String s : Indexer.getStatsQueryBuilder1()) {
				db.executeInsert(s);
			}
		}
	}

}
