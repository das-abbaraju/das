package com.picsauditing.actions.report;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportSalesRepresentatives extends PicsActionSupport {
	private List<BasicDynaBean> data;
	protected String where = "1";
	protected int[] operator;
	protected int[] accountUser;
	protected UserAccountRole responsibility;
	protected OperatorAccountDAO operatorAccountDAO = null;
	protected UserDAO userDAO = null;

	public ReportSalesRepresentatives(OperatorAccountDAO operatorAccountDAO, UserDAO userDAO) {
		this.operatorAccountDAO = operatorAccountDAO;
		this.userDAO = userDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.UserRolePicsOperator);

		if (!permissions.hasPermission(OpPerms.UserRolePicsOperator, OpType.Edit)) {
			accountUser = new int[1];
			accountUser[0] = permissions.getUserId();
		}

		if (responsibility != null) {
			where += " AND role = '" + responsibility.toString() + "'";
		}

		if (filterOn(accountUser)) {
			String userlist = Strings.implode(getAccountUser(), ",");
			where += " AND userID IN (" + userlist + ")";
		}

		if (filterOn(operator)) {
			Set<Integer> operatorsIds = new HashSet<Integer>();
			for (int opID : operator) {
				OperatorAccount oAccount = operatorAccountDAO.find(opID);
				if (oAccount.getType().equals("Corporate")) {
					for (Facility facility : oAccount.getOperatorFacilities()) {
						operatorsIds.add(facility.getOperator().getId());
					}
				} else {
					operatorsIds.add(oAccount.getId());
				}
			}
			where += " AND accountID IN (" + Strings.implode(operatorsIds, ",") + ")";
		}

		String sql = " SELECT accountID, type, userID, userName, accountName, audited.audited, o.doContractorsPay ,creationDate, role, ownerPercent, startDate, endDate, SUM(regisThisMonth) regisThisMonth, SUM(regisLastMonth) regisLastMonth, SUM(totalCons) totalCons, requestedbyid, auID"
				+ " FROM ("
				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, count(*) AS regisThisMonth, 0 regisLastMonth, 0 totalCons, c.requestedbyid, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " JOIN contractor_info c ON c.requestedbyid = a.id "
				+ " JOIN accounts con ON con.id = c.id "
				+ " AND con.active = 'Y' "
				+ " AND c.membershipDate > au.startDate " + " AND MONTH(c.membershipDate) = "
				+ (getMonth() + 1)
				+ " AND year(c.membershipDate) = "
				+ getYear()
				+ " GROUP BY c.requestedbyid, au.id "
				+

				" UNION "
				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, 0 regisThisMonth, count(*) AS regisLastMonth, 0 totalCons , c.requestedbyid, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " JOIN contractor_info c ON c.requestedbyid = a.id "
				+ " JOIN accounts con ON con.id = c.id "
				+ " AND con.active = 'Y' "
				+ " AND c.membershipDate > au.startDate "
				+ " AND MONTH(c.membershipDate) = "
				+ getMonth()
				+ " AND year(c.membershipDate) = "
				+ getYear()
				+ " GROUP BY c.requestedbyid, au.id "
				+

				" UNION "
				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, 0 regisThisMonth, 0 regisLastMonth, COUNT(*) AS totalCons , c.requestedbyid, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " JOIN contractor_info c ON c.requestedbyid = a.id "
				+ " JOIN accounts con ON con.id = c.id "
				+ " AND con.active = 'Y' "
				+ " AND c.membershipDate BETWEEN au.startDate AND au.endDate "
				+ " GROUP BY c.requestedbyid, au.id ) t "

				+ " LEFT JOIN (SELECT distinct opID, 1 audited FROM audit_operator "
				+ " JOIN audit_type at on at.id = audittypeid "
				+ " WHERE (auditTypeID IN (2,3,6) OR at.classType = 'IM') AND minRiskLevel > 0 AND canSee = 1) audited "
				+ " ON audited.opID = accountID "
				+ " JOIN operators o on o.id = accountID "
				+ " WHERE " + where
				+ " GROUP BY requestedbyid, auID " + " ORDER BY userName, role, accountName";

		Database db = new Database();
		data = db.select(sql, true);

		return SUCCESS;
	}

	public List<BasicDynaBean> getData() {
		return data;
	}

	public int getYear() {
		return DateBean.getCurrentYear();
	}

	public int getMonth() {
		return DateBean.getCurrentMonth();
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		return operatorAccountDAO.findWhere(true, "active='Y'");
	}

	public List<User> getUserList() throws Exception {
		return userDAO.findWhere("isActive='Yes' AND isGroup = 'No' AND account.id = 1100");
	}

	public UserAccountRole[] getRoleList() {
		return UserAccountRole.values();
	}

	public int[] getOperator() {
		return operator;
	}

	public void setOperator(int[] operator) {
		this.operator = operator;
	}

	public int[] getAccountUser() {
		return accountUser;
	}

	public void setAccountUser(int[] accountUser) {
		this.accountUser = accountUser;
	}

	public UserAccountRole getResponsibility() {
		return responsibility;
	}

	public void setResponsibility(UserAccountRole responsibility) {
		this.responsibility = responsibility;
	}

	public boolean filterOn(int[] value) {
		if (value == null)
			return false;
		if (value.length == 1) {
			if (value[0] == 0)
				return false;
		}
		return value.length > 0;
	}
}
