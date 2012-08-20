package com.picsauditing.actions.qa;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;

public class QaAnalyzeDatabaseDifferences extends PicsActionSupport {
	private static final long serialVersionUID = -8805462784582182659L;

	private String reportName;
	private String leftDatabase = "pics_qa";
	private String rightDatabase = "pics_live";
	private FlagAnalyzer flagAnalyzer;
	private AuditAnalyzer auditAnalyzer;
	private FlagChangeVelocityAnalyzer flagChangeVelocityAnalyzer;

	public QaAnalyzeDatabaseDifferences() {
	}

	public QaAnalyzeDatabaseDifferences(FlagAnalyzer flagAnalyzer, AuditAnalyzer auditAnalyzer) {
		this.flagAnalyzer = flagAnalyzer;
		this.auditAnalyzer = auditAnalyzer;
	}

	public String executeFlagChangeVelocity() throws Exception {
		checkPermissions();

		reportName = "Flag Change Velocity";
		flagChangeVelocityAnalyzer = flagChangeVelocityAnalyzer();
		flagChangeVelocityAnalyzer.run();
		return SUCCESS;
	}

	public String executeFlags() throws Exception {
		checkPermissions();

		reportName = "Flag Analyzer";
		flagAnalyzer = flagAnalyzer();
		flagAnalyzer.run();
		return SUCCESS;
	}

	public String executeAudits() throws Exception {
		checkPermissions();

		reportName = "Audit Analyzer";
		auditAnalyzer = auditAnalyzer();
		auditAnalyzer.run();
		return SUCCESS;
	}

	private FlagChangeVelocityAnalyzer flagChangeVelocityAnalyzer() {
		if (flagChangeVelocityAnalyzer == null) {
			flagChangeVelocityAnalyzer = new FlagChangeVelocityAnalyzer(leftDatabase, rightDatabase);
		}
		return flagChangeVelocityAnalyzer;
	}

	private FlagAnalyzer flagAnalyzer() {
		if (flagAnalyzer == null) {
			flagAnalyzer = new FlagAnalyzer(leftDatabase, rightDatabase);
		}
		return flagAnalyzer;
	}

	private AuditAnalyzer auditAnalyzer() {
		if (auditAnalyzer == null) {
			auditAnalyzer = new AuditAnalyzer(leftDatabase, rightDatabase);
		}
		return auditAnalyzer;
	}

	private void checkPermissions() throws NoRightsException {
		loadPermissions();

		if (!permissions.isAdmin()) {
			throw new NoRightsException("Admin");
		}
	}

	public String getLeftDatabase() {
		return leftDatabase;
	}

	public void setLeftDatabase(String leftDatabase) {
		this.leftDatabase = leftDatabase;
	}

	public String getRightDatabase() {
		return rightDatabase;
	}

	public void setRightDatabase(String rightDatabase) {
		this.rightDatabase = rightDatabase;
	}

	public String getReportName() {
		return reportName;
	}

	public FlagChangeVelocityAnalyzer getFlagChangeVelocityAnalyzer() {
		return flagChangeVelocityAnalyzer;
	}

	public FlagAnalyzer getFlagAnalyzer() {
		return flagAnalyzer;
	}

	public AuditAnalyzer getAuditAnalyzer() {
		return auditAnalyzer;
	}
}
