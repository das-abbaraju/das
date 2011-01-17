package com.picsauditing.mail;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class OQChangesSubscription extends SubscriptionBuilder {
	private Database db = new Database();
	private int daysAgo;
	private Account a;
	
	public OQChangesSubscription(Subscription subscription, SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO) {
		super(subscription, timePeriod, subscriptionDAO);
		this.templateID = 130;
	}

	@Override
	protected void setup(Account a) {
		try {
			daysAgo = DateBean.getDateDifference(timePeriod.getComparisonDate(), new Date());
			this.a = a;
			
			List<BasicDynaBean> dataCriteria = getChangedCriteriaList();
			List<BasicDynaBean> dataSites = getChangedSitesList();
			
			if (dataCriteria.size() > 0)
				tokens.put("criteriaList", dataCriteria);
			if (dataSites.size() > 0)
				tokens.put("sitesList", dataSites);
			if (dataCriteria.size() > 0 || dataSites.size() > 0)
				tokens.put("date", timePeriod.getComparisonDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<BasicDynaBean> getChangedCriteriaList() throws SQLException {
		SelectSQL sql = buildSQL("job_task_criteria");

		sql.addJoin("JOIN assessment_test test ON j.assessmentTestID = test.id");
		sql.addJoin("JOIN accounts ac ON test.assessmentCenterID = ac.id");
		sql.addField("CONCAT(test.qualificationType, ': ', test.qualificationMethod, "
				+ "' from ', ac.name) AS criteria");
		sql.addGroupBy("criteria HAVING effectiveDate <> expirationDate");
		sql.addOrderBy("test.qualificationType, test.qualificationMethod");

		SelectSQL subSelect = new SelectSQL("employee e");
		subSelect.addJoin("JOIN employee_site es ON es.employeeID = e.id AND es.jobSiteID > 0 "
				+ "AND es.effectiveDate <= NOW() and es.expirationDate > NOW()");
		subSelect.addJoin("JOIN job_site_task jst ON jst.jobID = es.jobSiteID "
				+ "AND jst.effectiveDate <= NOW() and jst.expirationDate > NOW()");
		subSelect.addField("jst.taskID");
		subSelect.addWhere("e.active = 1");

		sql.addWhere("jt.id IN (" + subSelect.toString() + ")");

		return db.select(sql.toString(), false);
	}

	private List<BasicDynaBean> getChangedSitesList() throws SQLException {
		SelectSQL sql = buildSQL("job_site_task");

		sql.addJoin("JOIN job_site js ON j.jobID = js.id");
		sql.addField("js.label AS label");
		sql.addField("js.name AS name");

		sql.addOrderBy("js.label");

		SelectSQL subSelect = new SelectSQL("employee e");
		subSelect.addJoin("JOIN employee_site es ON es.employeeID = e.id AND es.jobSiteID > 0 "
				+ "AND es.effectiveDate <= NOW() and es.expirationDate > NOW()");
		subSelect.addField("es.jobSiteID");
		subSelect.addWhere("e.active = 1");

		sql.addWhere("js.id IN (" + subSelect.toString() + ")");

		return db.select(sql.toString(), false);
	}

	private SelectSQL buildSQL(String tableName) {
		SelectSQL sql = new SelectSQL(tableName + " j");
		sql.addField(tableName.equals("job_task_criteria") ? "MAX(j.effectiveDate) effectiveDate" : "j.effectiveDate");
		sql.addField(tableName.equals("job_task_criteria") ? "MIN(j.expirationDate) expirationDate" : "j.expirationDate");
		sql.addField("DATEDIFF(j.expirationDate,now()) daysFromExpiration");

		sql.addJoin("JOIN job_task jt ON j.taskID = jt.id");
		sql.addField("jt.id taskID");
		sql.addField("jt.taskType");
		sql.addField("CONCAT(jt.label,' - ', jt.name) task");

		sql.addJoin("JOIN accounts op ON jt.opID = op.id");
		sql.addField("op.name AS opName");

		if (a.isOperator()) {
			sql.addWhere("op.id = " + a.getId());
		} else if (a.isCorporate()) {
			OperatorAccount corp = (OperatorAccount) a;
			
			Set<Integer> opIDs = new HashSet<Integer>();
			for (OperatorAccount child : corp.getOperatorChildren()) {
				opIDs.add(child.getId());
			}
			
			sql.addWhere("op.id IN (" + Strings.implode(opIDs) + ")");
		}

		if (a.isDemo())
			sql.addWhere("op.status IN ('Active','Demo')");
		else
			sql.addWhere("op.status = 'Active'");

		sql.addWhere("(j.effectiveDate BETWEEN SUBDATE(NOW(), INTERVAL " + daysAgo + " DAY) and NOW() "
				+ "AND j.expirationDate > NOW())" + "OR (j.expirationDate BETWEEN SUBDATE(NOW(), INTERVAL " + daysAgo
				+ " DAY) and NOW() " + "AND j.effectiveDate < SUBDATE(NOW(), INTERVAL " + daysAgo + " DAY))");

		sql.addOrderBy("op.name, jt.displayOrder");
		return sql;
	}
}
