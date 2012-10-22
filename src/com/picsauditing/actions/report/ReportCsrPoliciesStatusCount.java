package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportCsrPoliciesStatusCount extends ReportAccount {

	@Autowired
	protected UserDAO userDAO = null;

	protected List<User> csrs = null;
	protected int[] csrIds;

	public void prepare() throws Exception {
		super.prepare();

		getFilter().setShowPolicyChangedDate(true);
		getFilter().setShowAccountName(false);
		getFilter().setShowCcOnFile(false);
		getFilter().setShowStatus(false);
		getFilter().setShowOperator(false);
		getFilter().setShowTrade(false);
		getFilter().setShowLocation(false);
		getFilter().setShowTaxID(false);
		getFilter().setShowRiskLevel(false);
		getFilter().setShowProductRiskLevel(false);
		getFilter().setShowService(false);
		getFilter().setShowRegistrationDate(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowSoleProprietership(false);
		getFilter().setNonContactUser(true);
	}

	public void buildQuery() {
		if (!filterOn(csrIds)) {
			if (permissions.hasGroup(User.GROUP_MANAGER)) {
				List<User> csrList = getCsrs();
				csrIds = new int[csrList.size()];
				int i = 0;
				for (User u : csrList) {
					csrIds[i] = u.getId();
					i++;
				}
			} else {
				csrIds = new int[1];
				csrIds[0] = permissions.getUserId();
			}
		}

		sql = new SelectAccount();
		sql.addField("u.id AS csrID");
		sql.addField("u.name AS csr");
		sql.addField("caow.status AS policyStatus");
		sql.addField("COUNT(DISTINCT cao.id) AS cnt");
		sql.addJoin("JOIN contractor_audit ca ON a.id = ca.conID");
		sql.addJoin("JOIN audit_type at ON ca.auditTypeId = at.id");
		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		sql.addJoin("JOIN contractor_audit_operator_workflow caow ON caow.caoID = cao.id");
		sql.addJoin("JOIN users u ON caow.createdBy = u.id AND u.accountID = "+Account.PicsID);
		String csrs = " u.id IN (" + csrIds[0];
		for (int i = 1; i < csrIds.length; i++) {
			csrs += "," + csrIds[i];
		}
		csrs += ")";
		sql.addWhere(csrs);
		sql.addWhere("at.classType = 'Policy'");
		sql.addWhere("caow.status NOT IN ('Submitted','Resubmitted')");
		sql.addGroupBy("u.id, caow.status");

		SelectAccount unionSql = new SelectAccount();
		unionSql.addField("0 AS csrID");
		unionSql.addField("'System Wide' AS csr");
		unionSql.addField("caow.status AS policyStatus");
		unionSql.addField("COUNT(DISTINCT cao.id) AS cnt");
		unionSql.addJoin("JOIN contractor_audit ca ON a.id = ca.conID");
		unionSql.addJoin("JOIN audit_type at ON ca.auditTypeId = at.id");
		unionSql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		unionSql.addJoin("JOIN contractor_audit_operator_workflow caow ON caow.caoID = cao.id");
		unionSql.addWhere("at.classType = 'Policy'");
		unionSql.addGroupBy("caow.status");

		addUnion(unionSql);

		orderByDefault = "CASE WHEN csrID = 0 THEN 1 ELSE 2 END, csr, policyStatus";
		filteredDefault = true;

		addFilterToSQL();
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		// also change the filter for union
		if (filterOn(getFilter().getConAuditorId())) {
			String list = Strings.implode(getFilter().getConAuditorId(), ",");
			setFiltered(true);
			
			for (SelectSQL union : unionSql) {
				union.addWhere("0 IN (" + list + ")");
			}
		}
	}
	
	
	public List<User> getCsrs() {
		if (csrs == null) {
			csrs = new ArrayList<User>();
			csrs = userDAO.findByGroup(User.GROUP_CSR);
			
			User systemWide = new User();
			systemWide.setId(0);
			systemWide.setName("System Wide");
			csrs.add(systemWide);
		}
		return csrs;
	}

	public int[] getCsrIds() {
		return csrIds;
	}

	public void setCsrIds(int[] csrIds) {
		this.csrIds = csrIds;
	}
}