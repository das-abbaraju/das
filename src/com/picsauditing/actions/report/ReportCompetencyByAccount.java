package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportCompetencyByAccount extends ReportEmployee {
	public ReportCompetencyByAccount() {
		orderByDefault = "name";
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("LEFT JOIN job_role jr ON jr.accountID = a.id AND jr.active = 1");
		sql.addJoin(buildAuditJoin(AuditType.HSE_COMPETENCY));
		sql.addJoin(buildAuditJoin(AuditType.SHELL_COMPETENCY_REVIEW));

		sql.addField("COUNT(distinct e.id) eCount");
		sql.addField("COUNT(distinct jr.id) jCount");
		sql.addField(buildAuditField(AuditType.HSE_COMPETENCY));
		sql.addField(buildAuditField(AuditType.SHELL_COMPETENCY_REVIEW));

		sql.addWhere("a.requiresCompetency = 1");
		if (permissions.isCorporate()) {
			PermissionQueryBuilderEmployee permQuery = new PermissionQueryBuilderEmployee(permissions);
			sql.addWhere("1 " + permQuery.toString());
		}

		sql.addGroupBy("a.id");

		filter.setShowFirstName(false);
		filter.setShowLastName(false);
		filter.setShowEmail(false);
		filter.setShowSsn(false);
	}

	@Override
	protected void addExcelColumns() {
		excelSheet.setData(data);
		excelSheet.addColumn(new ExcelColumn("name", "Company Name"));
		excelSheet.addColumn(new ExcelColumn("eCount", "# of Employees"));
		excelSheet.addColumn(new ExcelColumn("jCount", "# of Job Roles"));
		excelSheet.addColumn(new ExcelColumn("ca99status", "Job Role Self Assessment"));
		excelSheet.addColumn(new ExcelColumn("ca100status", "HSE Competency Review"));
	}

	private String buildAuditJoin(int auditTypeID) {
		SelectSQL sql2 = new SelectSQL("contractor_audit ca");
		String dateFormat = "DATE_FORMAT(cao.statusChangedDate, '%c/%e/%Y')";
		// Joins
		sql2.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id AND cao.visible = 1");
		sql2.addJoin("JOIN contractor_audit_operator_permission caop ON caop.caoID = cao.id");
		// Fields
		sql2.addField("ca.id");
		sql2.addField("ca.conID");
		sql2.addField("caop.opID");
		sql2.addField(String.format(
				"CASE cao.status WHEN 'Pending' THEN NULL ELSE CONCAT(cao.status, ' on ', %s) END status", dateFormat));
		sql2.addField(String.format("%s changedDate", dateFormat));
		// Wheres
		sql2.addWhere(String.format("ca.auditTypeID = %d", auditTypeID));
		// Order bys
		sql2.addOrderBy("ca.creationDate DESC");

		return String.format("LEFT JOIN (%1$s) ca%2$d ON ca%2$d.conID = a.id AND ca%2$d.opID = %3$d", sql2.toString(),
				auditTypeID, permissions.getAccountId());
	}

	private String buildAuditField(int auditTypeID) {
		return String.format("ca%1$d.id ca%1$dID, ca%1$d.status ca%1$dstatus, ca%1$d.changedDate ca%1$ddate",
				auditTypeID);
	}
}
