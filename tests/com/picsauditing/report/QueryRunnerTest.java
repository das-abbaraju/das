package com.picsauditing.report;

import java.sql.SQLException;
import java.util.Map;

import junit.framework.TestCase;

import com.picsauditing.access.Permissions;

public class QueryRunnerTest extends TestCase {

	private Permissions permissions;
	private QueryRunner runner;
	private QueryCommand command;
	private String sql;

	protected void setUp() throws Exception {
		super.setUp();
		permissions = new Permissions();
		buildRunner(QueryBase.Contractors);
		command = new QueryCommand();
	}

	private void buildRunner(QueryBase base) {
		runner = new QueryRunner(base, permissions);
	}

	private void runner() throws SQLException {
		runner.run(command, null);
		sql = runner.getSQL();
	}

	public void testAvailableFieldSize() {
		Map<String, String> availableFields = runner.getAvailableFields();
		assertEquals(3, availableFields.size());
	}

	public void testSimpleContractorQuery() throws SQLException {
		runner();
		assertEquals("SELECT SQL_CALC_FOUND_ROWS a.id AS accountID, a.name AS accountName, a.status AS accountStatus "
				+ "FROM accounts a JOIN contractor_info c ON a.id = c.id WHERE 1 AND (a.type='Contractor') "
				+ "ORDER BY a.name LIMIT 100", runner.getSQL());
	}

	public void testLimit() throws SQLException {
		command.setRowsPerPage(10);
		runner();
		assertTrue(runner.getSQL().endsWith("LIMIT 10"));
	}

	public void testPages() throws SQLException {
		command.setRowsPerPage(10);
		command.setPage(2);
		runner();
		assertTrue(sql.endsWith("LIMIT 10, 10"));
	}
}
