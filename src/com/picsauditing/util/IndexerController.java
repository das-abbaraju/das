package com.picsauditing.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.Indexer;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.IndexableDAO;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.search.Database;

public class IndexerController {

	@Autowired
	private Indexer indexer;

	public void runAll(String toRun, boolean runStats) throws SQLException {
		if (Indexer.isRunning()) {
			return;
		} else
			Indexer.setRunning(true);
		System.out.println("Starting Indexer");
		Indexer.setIndexTables(new HashMap<String, IndexableDAO>());
		for (Entry<String, IndexableDAO> entry : Indexer.getIndexTables().entrySet()) {
			indexer.runIndexer(indexer.getIndexable(entry.getKey()), entry.getValue(), entry.getKey());
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
