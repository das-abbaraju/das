package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportCompetencyByAccount extends ReportEmployee {
	public ReportCompetencyByAccount() {
		orderByDefault = "name";
	}

	@Override
	protected void buildQuery() {
		/*
		String sqlString = "select a.id, a.name, IFNULL(jr.total,0) jobRoleCount, IFNULL(e.total,0) employeeCount, "
				+ "IFNULL(totalComp.competencyTotal,0) required, IFNULL(totalComp.skilledTotal,0) skilled,  "
				+ "ca99.id ca99ID, cao99.status ca99status, cao99.statusChangedDate ca99statusChangedDate, "
				// "ca99.creationDate ca99creationDate, ca99.expiresDate ca99expiresDate, "
				+ "ca100.id ca100ID, cao100.status ca100status, cao100.statusChangedDate ca100statusChangedDate "
				// "ca100.creationDate ca100creationDate, ca100.expiresDate ca100expiresDate, "
				+ "from accounts a "
				+ "JOIN contractor_tag ct ON a.id = ct.conID and ct.tagID = 142 "
				+ "left join (select accountID, count(*) total FROM job_role group by accountID) jr on jr.accountID = a.id "
				+ "left join (SELECT e2.accountID, count(*) as total from (select e1.accountID, e1.id, count(*) as totalComp from employee e1 join employee_competency ec on ec.employeeID = e1.id where e1.active = 1 and ec.skilled = 1 group by e1.id) e2 group by e2.accountID) e on e.accountID = a.id "
				+ "left join ( "
				+ "select accountID, count(competencyID) competencyTotal, count(skilled) skilledTotal from ( "
				+ "select e.id, e.accountID, jc.competencyID, ec.id skilled FROM employee e "
				+ "join employee_role er on er.employeeID = e.id "
				+ "JOIN job_competency jc ON jc.jobRoleID = er.jobRoleID "
				+ "LEFT JOIN employee_competency ec ON ec.competencyID = jc.competencyID AND e.id = ec.employeeID and ec.skilled = 1 "
				+ "group by e.id, jc.competencyID "
				+ ") e group by e.accountID) totalComp on totalComp.accountID = a.id "
				+ "LEFT JOIN contractor_audit ca99 ON ca99.auditTypeID = 99 AND ca99.conID = a.id "
				+ "LEFT JOIN contractor_audit_operator cao99 ON cao99.auditID = ca99.id AND cao99.visible = 1 AND cao99.status IN ('Submitted','Complete') "
				+ "LEFT JOIN contractor_audit ca100 ON ca100.auditTypeID = 100 AND ca100.conID = a.id "
				+ "LEFT JOIN contractor_audit_operator cao100 ON cao100.auditID = ca100.id AND cao100.visible = 1 AND cao100.status IN ('Submitted','Complete') "
				+ "GROUP BY a.id ORDER BY " + (Strings.isEmpty(getOrderBy()) ? orderByDefault : getOrderBy());
		 */

		super.buildQuery();

		sql.addJoin("LEFT JOIN job_role jr ON jr.accountID = a.id AND jr.active = 1");
		sql.addJoin(buildAuditJoin(AuditType.HSE_COMPETENCY));
		sql.addJoin(buildAuditJoin(AuditType.SHELL_COMPETENCY_REVIEW));

		sql.addField("COUNT(distinct e.id) eCount");
		sql.addField("COUNT(distinct jr.id) jCount");
		sql.addField(buildAuditField(AuditType.HSE_COMPETENCY));
		sql.addField(buildAuditField(AuditType.SHELL_COMPETENCY_REVIEW));

		PermissionQueryBuilderEmployee permQuery = new PermissionQueryBuilderEmployee(permissions);
		sql.addWhere("1 " + permQuery.toString());
		sql.addWhere("a.status in ('Active'" + (permissions.isAdmin() ? ", 'Demo'" : "") + ")");
		
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
		return "LEFT JOIN (SELECT ca.id, ca.conID, CASE WHEN cao.status = 'Pending' THEN NULL "
				+ "ELSE CONCAT(CASE WHEN cao.status = 'Complete' THEN 'Completed' ELSE cao.status END, "
				+ "' on ', DATE_FORMAT(cao.statusChangedDate, '%c/%e/%Y')) "
				+ "END status, caop.opID FROM contractor_audit ca "
				+ "JOIN contractor_audit_operator cao ON cao.auditID = ca.id "
				+ "JOIN contractor_audit_operator_permission caop ON caop.caoID = cao.id WHERE ca.auditTypeID = "
				+ auditTypeID + " GROUP BY ca.conID ORDER BY ca.creationDate DESC) ca" + auditTypeID + " ON ca"
				+ auditTypeID + ".conID = a.id AND ca" + auditTypeID + ".opID = " + permissions.getAccountId();
	}

	private String buildAuditField(int auditTypeID) {
		return "ca" + auditTypeID + ".id ca" + auditTypeID + "ID, ca" + auditTypeID + ".status ca" + auditTypeID
				+ "status";
	}
}
