package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.Strings;
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
		
		if (permissions.isOperator()) {
			String accountStatus = "'Active'";
			if (permissions.getAccountStatus().isDemo())
				accountStatus += ", 'Demo'";

			sql.addJoin(String.format("JOIN generalcontractors gc ON gc.subID = a.id AND (gc.genID IN "
					+ "(SELECT f.opID FROM facilities f JOIN facilities c ON c.corporateID = f.corporateID "
					+ "AND c.corporateID NOT IN (%s) AND c.opID = %d) OR gc.genID = %2$d)",
					Strings.implode(Account.PICS_CORPORATE), permissions.getAccountId()));
			sql.addJoin(String.format("JOIN accounts o ON o.id = gc.genID AND o.status IN (%s)", accountStatus));
			sql.addJoin(String.format(
					"LEFT JOIN (SELECT subID FROM generalcontractors WHERE genID = %d) gcw ON gcw.subID = a.id",
					permissions.getAccountId()));
			sql.addField("ISNULL(gcw.subID) notWorksFor");
		}

		sql.addField("COUNT(distinct e.id) eCount");
		sql.addField("COUNT(distinct jr.id) jCount");
		sql.addField(buildAuditField(AuditType.HSE_COMPETENCY));
		sql.addField(buildAuditField(AuditType.SHELL_COMPETENCY_REVIEW));

		sql.addWhere("a.requiresCompetencyReview = 1");
		if (permissions.isCorporate()) {
			PermissionQueryBuilderEmployee permQuery = new PermissionQueryBuilderEmployee(permissions);
			sql.addWhere("1 " + permQuery.toString());
		}

		sql.addGroupBy("a.id");

		getFilter().setShowFirstName(false);
		getFilter().setShowLastName(false);
		getFilter().setShowEmail(false);
		getFilter().setShowSsn(false);
		getFilter().setShowOperators(true);
	}

	@Override
	protected void addExcelColumns() {
		excelSheet.setData(data);
		excelSheet.addColumn(new ExcelColumn("name", getText("global.Company")));
		excelSheet.addColumn(new ExcelColumn("eCount", getText(getScope() + ".label.NumberOfEmployees")));
		excelSheet.addColumn(new ExcelColumn("jCount", getText(getScope() + ".label.NumberOfJobRoles")));
		excelSheet.addColumn(new ExcelColumn("ca99status", getText("AuditType.99.name")));
		excelSheet.addColumn(new ExcelColumn("ca100status", getText("AuditType.100.name")));
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
