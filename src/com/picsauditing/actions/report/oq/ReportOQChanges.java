package com.picsauditing.actions.report.oq;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportOQChanges extends PicsActionSupport {

	private List<BasicDynaBean> dataCriteria;
	private List<BasicDynaBean> dataSites;
	private int daysAgo = 60;
	private Database db = new Database();

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		dataCriteria = getChangedCriteriaList(daysAgo);
		dataSites = getChangedSitesList(daysAgo);

		return super.execute();
	}

	public List<BasicDynaBean> getChangedCriteriaList(int daysAgo) throws SQLException {
		SelectSQL sql = buildSQL("job_task_criteria");

		sql.addJoin("JOIN assessment_test test ON j.assessmentTestID = test.id");
		sql.addJoin("JOIN accounts ac ON test.assessmentCenterID = ac.id");
		sql.addField("CONCAT(test.qualificationType, ': ', test.qualificationMethod, "
				+ "' from ', ac.name) AS criteria");
		sql.addOrderBy("test.qualificationType, test.qualificationMethod");

		if (permissions.isContractor()) {
			SelectSQL subSelect = new SelectSQL("employee e");
			subSelect.addJoin("JOIN employee_site es ON es.employeeID = e.id AND es.jobSiteID > 0 "
					+ "AND es.effectiveDate <= NOW() and es.expirationDate > NOW()");
			subSelect.addJoin("JOIN job_site_task jst ON jst.jobID = es.jobSiteID "
					+ "AND jst.effectiveDate <= NOW() and jst.expirationDate > NOW()");
			subSelect.addField("jst.taskID");
			subSelect.addWhere("e.active = 1");

			sql.addWhere("jt.id IN (" + subSelect.toString() + ")");
		}

		return db.select(sql.toString(), false);
	}

	public List<BasicDynaBean> getChangedSitesList(int daysAgo) throws SQLException {
		SelectSQL sql = buildSQL("job_site_task");

		sql.addJoin("JOIN job_site js ON j.jobID = js.id");
		sql.addField("js.label AS label");
		sql.addField("js.name AS name");

		sql.addOrderBy("js.label");

		if (permissions.isContractor()) {
			SelectSQL subSelect = new SelectSQL("employee e");
			subSelect.addJoin("JOIN employee_site es ON es.employeeID = e.id AND es.jobSiteID > 0 "
					+ "AND es.effectiveDate <= NOW() and es.expirationDate > NOW()");
			subSelect.addField("es.jobSiteID");
			subSelect.addWhere("e.active = 1");

			sql.addWhere("js.id IN (" + subSelect.toString() + ")");
		}

		return db.select(sql.toString(), false);
	}

	private SelectSQL buildSQL(String tableName) {
		SelectSQL sql = new SelectSQL(tableName + " j");
		sql.addField("j.effectiveDate");
		sql.addField("j.expirationDate");
		sql.addField("DATEDIFF(j.expirationDate,now()) daysFromExpiration");

		sql.addJoin("JOIN job_task jt ON j.taskID = jt.id");
		sql.addField("jt.id taskID");
		sql.addField("jt.taskType");
		sql.addField("CONCAT(jt.label,' - ', jt.name) task");

		sql.addJoin("JOIN accounts op ON jt.opID = op.id");
		sql.addField("op.name AS opName");

		if (permissions.isOperator()) {
			sql.addWhere("op.id = " + permissions.getAccountId());
		} else if (permissions.isCorporate()) {
			sql.addWhere("op.id IN (" + Strings.implode(permissions.getVisibleAccounts()) + ")");
		}

		if (permissions.getAccountStatus().isDemo())
			sql.addWhere("op.status IN ('Active','Demo')");
		else
			sql.addWhere("op.status = 'Active'");

		sql.addWhere("(j.effectiveDate BETWEEN SUBDATE(NOW(), INTERVAL " + daysAgo + " DAY) and NOW() "
				+ "AND j.expirationDate > NOW())" + "OR (j.expirationDate BETWEEN SUBDATE(NOW(), INTERVAL " + daysAgo
				+ " DAY) and NOW() " + "AND j.effectiveDate < SUBDATE(NOW(), INTERVAL " + daysAgo + " DAY))");

		sql.addOrderBy("op.name, jt.displayOrder");
		return sql;
	}

	public List<BasicDynaBean> getDataCriteria() {
		return dataCriteria;
	}

	public List<BasicDynaBean> getDataSites() {
		return dataSites;
	}

	public int getDaysAgo() {
		return daysAgo;
	}

	public void setDaysAgo(int daysAgo) {
		this.daysAgo = daysAgo;
	}

}
