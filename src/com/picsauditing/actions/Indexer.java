package com.picsauditing.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.IndexableDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.IndexObject;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

public class Indexer extends PicsActionSupport {

	private int id = 0;
	private String toRun = null;
	private int start = 0;
	private int end = 0;
	private static boolean isRunning = false;
	private static boolean stop = false;
	private static boolean runStats = false;
	private static String[] statsQueryBuilder1 = {
			"TRUNCATE TABLE app_index_stats;",
			"INSERT INTO app_index_stats SELECT indexType, NULL, count(distinct foreignKey) FROM app_index GROUP BY indexType;",
			"INSERT INTO app_index_stats SELECT NULL, value, count(*) FROM app_index GROUP BY value;",
			"INSERT INTO app_index_stats SELECT indexType, value, count(*) FROM app_index GROUP BY indexType, value;",
			"ANALYZE TABLE app_index, app_index_stats;" };

	private AccountDAO accountDAO;
	private UserDAO userDAO;
	private EmployeeDAO empDAO;

	private List<String> list;
	private static Map<String, IndexableDAO> indexTables;
	private static final int RUN_NUM = 50;

	public Indexer(AccountDAO accountDAO, UserDAO userDAO, EmployeeDAO empDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.empDAO = empDAO;
	}

	@Override
	public String execute() throws NoRightsException {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.DevelopmentEnvironment);
		try {
			if (isRunning) {
				addActionMessage("Indexer Already Running");
				return SUCCESS;
			} else
				isRunning = true;
			PicsLogger.start("Indexer", "Starting Indexer");
			setIndexTables(new HashMap<String, IndexableDAO>());
			// if not specified then we will run all tables to check the index
			if (toRun == null) {
				getIndexTables().put("accounts", accountDAO);
				getIndexTables().put("users", userDAO);
				getIndexTables().put("employee", empDAO);
			} else {
				if (toRun.equals("accounts"))
					getIndexTables().put("accounts", accountDAO);
				else if (toRun.equals("users"))
					getIndexTables().put("users", userDAO);
				else if (toRun.equals("employee"))
					getIndexTables().put("employee", empDAO);
			}
			for (Entry<String, IndexableDAO> entry : getIndexTables()
					.entrySet()) {
				// for each table get those rows that need indexing
				// and pass the list of ids in and run the indexer
				runIndexer(getIndexable(entry.getKey()), entry.getValue(),
						entry.getKey());
			}
			if (runStats) {
				runStats = false;
				Database db = new Database();
				for (String s : getStatsQueryBuilder1()) {
					db.executeInsert(s);
				}
			}
		} catch (Exception e) {
			PicsLogger.log("Objection!(Exception)" + e.getMessage());
		} finally {
			PicsLogger.stop();
		}
		return SUCCESS;
	}

	public void runSingle(Indexable table, String tblName) {
		List<IndexObject> l = null; // our list of ids
		StringBuilder queryIndex = new StringBuilder(
				"INSERT IGNORE INTO app_index VALUES ");
		StringBuilder queryStats = new StringBuilder(
				"INSERT IGNORE INTO app_index_stats VALUES ");
		StringBuilder queryDelete = new StringBuilder(
				"DELETE FROM app_index WHERE foreignKey = ");
		Database db = new Database();

		if (table == null)
			return;
		l = table.getIndexValues();
		queryDelete.append(table.getId()).append(" AND indexType = '").append(
				table.getIndexType()).append("'");
		try {
			if (db.executeUpdate(queryDelete.toString()) > 0) {
				System.out.println("deleted using: " + queryDelete.toString());
			}
			for (IndexObject s : l) {
				queryIndex.append("('").append(table.getIndexType()).append(
						"',").append(table.getId()).append(",'").append(
						s.getValue()).append("','").append(s.getWeight())
						.append("'),");
				queryStats.append("('").append(table.getIndexType()).append(
						"','").append(s.getValue()).append("',").append(1)
						.append("),").append("(null,'").append(s.getValue())
						.append("',1),");
			}
			db.executeInsert(queryIndex.substring(0, queryIndex.length() - 1));
			db.executeInsert(queryStats.substring(0, queryStats.length() - 1));
			String updateIndexing = "UPDATE " + tblName
					+ " SET needsIndexing=0 WHERE id = " + table.getId();
			db.executeUpdate(updateIndexing);
			System.out.println("Saved ids");
			db.executeUpdate("ANALYZE TABLE app_index, app_index_stats;");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void runIndexer(List<BasicDynaBean> ids, IndexableDAO dao,
			String tblName) {
		// batch is the number we use to control how many to run
		int batch = 0;
		Long t1 = System.currentTimeMillis();
		if (ids == null || ids.isEmpty()) {
			PicsLogger.log("Nothing to update for " + tblName);
			return;
		}
		// Base string for query, use stringbuilder as we will be building large
		// strings
		// and String.concat would be too slow
		StringBuilder queryIndex = new StringBuilder(
				"INSERT IGNORE INTO app_index VALUES ");
		StringBuilder queryStats = new StringBuilder(
				"INSERT IGNORE INTO app_index_stats VALUES ");
		StringBuilder queryDelete = new StringBuilder(
				"DELETE FROM app_index WHERE foreignKey = ");
		Database db = new Database();
		List<IndexObject> l = null; // our list of ids
		List<Integer> savedIds = new ArrayList<Integer>(); // will store the
		// last ids to be
		// ran per batch
		for (int i = 0; i < ids.size(); i++) {
			if (stop) {
				clearMessages();
				addActionMessage("Indexer Stopped!");
				stop = false;
				isRunning = false;
				return;
			}
			// Retrieve id from bean
			int id = 0;
			try {
				id = Integer.parseInt(ids.get(i).get("id").toString());
				Indexable table = (Indexable) dao.find(id);
				if (table == null) // not a supported entity or could not pull
					// up record
					continue;
				l = table.getIndexValues();
				queryDelete.append(id).append(" AND indexType = '").append(
						table.getIndexType()).append("'");
				if (db.executeUpdate(queryDelete.toString()) > 0) {
					PicsLogger.log("Deleted using " + queryDelete.toString());
				}
				queryDelete.setLength(0);
				queryDelete.append("DELETE FROM app_index WHERE foreignKey = ");
				// build the queries to insert
				for (IndexObject s : l) {
					queryIndex.append("('").append(table.getIndexType())
							.append("',").append(id).append(",'").append(
									s.getValue()).append("','").append(
									s.getWeight()).append("'),");
					queryStats.append("('").append(table.getIndexType())
							.append("','").append(s.getValue()).append("',")
							.append(1).append("),");
				}
				batch++;
				// add the id to the list of ids for saving
				savedIds.add(id);
				if (batch >= RUN_NUM) { // if we have this number of rows added,
					// run the queries
					db.executeInsert(queryIndex.substring(0, queryIndex
							.length() - 1));
					db.executeInsert(queryStats.substring(0, queryStats
							.length() - 1));
					String updateIndexing = "UPDATE " + tblName
							+ " SET needsIndexing=0 WHERE id IN("
							+ Strings.implode(savedIds) + ")";
					db.executeUpdate(updateIndexing);
					PicsLogger.log("Saved ids");
					queryIndex.setLength(0);
					queryStats.setLength(0);
					queryIndex.append("INSERT IGNORE INTO app_index VALUES");
					queryStats
							.append("INSERT IGNORE INTO app_index_stats VALUES");
					savedIds.clear(); // no error, clear list
					batch = 0;
				}
			} catch (SQLException e) {
				PicsLogger.log("Last insert failed");
				e.printStackTrace();
			}
		}
		try {
			if (batch != 0) {
				db.executeInsert(queryIndex.substring(0,
						queryIndex.length() - 1));
				db.executeInsert(queryStats.substring(0,
						queryStats.length() - 1));
				String updateIndexing = "UPDATE " + tblName
						+ " SET needsIndexing=0 WHERE id IN("
						+ Strings.implode(savedIds) + ")";
				db.executeUpdate(updateIndexing);
			}
		} catch (SQLException e) {
			PicsLogger.log("Last insert failed");
			PicsLogger.log(e.toString());
			return;
		} finally {
			PicsLogger.log("Fin");
			Long t2 = System.currentTimeMillis();
			PicsLogger.log("Time to complete: " + (t2 - t1) / 1000f);
			stop = false;
			isRunning = false;
		}
	}

	public List<BasicDynaBean> getIndexable(String tblName) throws SQLException {
		SelectSQL sql = new SelectSQL(tblName);
		sql.addField("id");
		sql.addWhere("needsIndexing = 1");
		if (end > 0 && start >= 0 && (end > start)) {
			sql.addWhere("id > " + start);
			sql.addWhere("id < " + end);
		}
		sql.addOrderBy("id");
		Database db = new Database();
		return db.select(sql.toString(), false);
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getToRun() {
		return toRun;
	}

	public void setToRun(String toRun) {
		this.toRun = toRun;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public static boolean isRunning() {
		return isRunning;
	}

	public static void setRunning(boolean isRunning) {
		Indexer.isRunning = isRunning;
	}

	public static boolean isStop() {
		return stop;
	}

	public static void setStop(boolean stop) {
		Indexer.stop = stop;
	}

	public static void setIndexTables(Map<String, IndexableDAO> indexTables) {
		Indexer.indexTables = indexTables;
	}

	public static Map<String, IndexableDAO> getIndexTables() {
		return indexTables;
	}

	public static void setStatsQueryBuilder1(String[] statsQueryBuilder1) {
		Indexer.statsQueryBuilder1 = statsQueryBuilder1;
	}

	public static String[] getStatsQueryBuilder1() {
		return statsQueryBuilder1;
	}

	public AccountDAO getAccountDAO() {
		return accountDAO;
	}

	public void setAccountDAO(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public EmployeeDAO getEmpDAO() {
		return empDAO;
	}

	public void setEmpDAO(EmployeeDAO empDAO) {
		this.empDAO = empDAO;
	}

	public static boolean isRunStats() {
		return runStats;
	}

	public static void setRunStats(boolean runStats) {
		Indexer.runStats = runStats;
	}

}
