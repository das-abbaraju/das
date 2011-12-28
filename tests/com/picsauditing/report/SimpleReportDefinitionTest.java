package com.picsauditing.report;

import java.sql.SQLException;

import junit.framework.TestCase;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Report;

public class SimpleReportDefinitionTest extends TestCase {

	private Permissions permissions;
	private QueryRunner runner;
	private SimpleReportDefinition command;
	private String sql;

	protected void setUp() throws Exception {
		super.setUp();
		permissions = new Permissions();
		// By default create a Contractor QueryRunner
		Report report = new Report();
		report.setBase(QueryBase.Contractors);
		buildRunner(report);
		command = new SimpleReportDefinition();
	}

	private void buildRunner(Report report) {
		runner = new QueryRunner(report, permissions, null);
	}

	private void runBuildQueryWithDefinition() throws SQLException {
		sql = runner.buildQuery().toString();
	}

	public void testLimit() throws SQLException {
		command.setRowsPerPage(10);
		runBuildQueryWithDefinition();
		assertTrue(runner.getSQL().endsWith("LIMIT 10"));
	}

	public void testPages() throws SQLException {
		command.setRowsPerPage(10);
		command.setPage(2);
		runBuildQueryWithDefinition();
		assertTrue(sql.endsWith("LIMIT 10, 10"));
	}
}
