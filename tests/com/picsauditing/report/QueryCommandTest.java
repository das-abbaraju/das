package com.picsauditing.report;

import java.sql.SQLException;

import junit.framework.TestCase;

import com.picsauditing.access.Permissions;

public class QueryCommandTest extends TestCase {

	private Permissions permissions;
	private QueryRunner runner;
	private QueryCommand command;
	private String sql;

	protected void setUp() throws Exception {
		super.setUp();
		permissions = new Permissions();
		// By default create a Contractor QueryRunner
		buildRunner(QueryBase.Contractors);
		command = new QueryCommand();
	}

	private void buildRunner(QueryBase base) {
		runner = new QueryRunner(base, permissions, null);
	}

	private void runBuildQueryWithCommand() throws SQLException {
		sql = runner.buildQuery(command, false).toString();
	}

	public void testLimit() throws SQLException {
		command.setRowsPerPage(10);
		runBuildQueryWithCommand();
		assertTrue(runner.getSQL().endsWith("LIMIT 10"));
	}

	public void testPages() throws SQLException {
		command.setRowsPerPage(10);
		command.setPage(2);
		runBuildQueryWithCommand();
		assertTrue(sql.endsWith("LIMIT 10, 10"));
	}
}
