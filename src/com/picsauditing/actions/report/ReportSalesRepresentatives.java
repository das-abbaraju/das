package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaBean;

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
	Database db = new Database();
	private List<BasicDynaBean> data;
	private List<BasicDynaBean> summary;
	protected String where = "1";
	protected int[] operator;
	protected int[] accountUser;
	protected UserAccountRole responsibility;
	protected OperatorAccountDAO operatorAccountDAO = null;
	protected UserDAO userDAO = null;
	protected Map<String, Object> summaryData = null;
	protected boolean showSummary = false;
	protected String month = null;

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

		if (accountUser != null && accountUser.length == 1) {
			showSummary = true;
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

		String sql = " SELECT accountID, type, userID, userName, accountName, audited.audited, o.doContractorsPay ,creationDate, role, ownerPercent, startDate, endDate, SUM(regisThisMonth) regisThisMonth, SUM(regisLastMonth) regisLastMonth, SUM(totalCons) totalCons, auID"
				+ " FROM ("

				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, 0 regisThisMonth, 0 regisLastMonth, 0 totalCons, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " AND a.active = 'Y' "
				+ " AND (au.startDate IS NULL OR au.startDate <= Now()) "
				+ " AND (au.endDate IS NULL OR au.endDate >= NOW()) "
				+ " UNION "

				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, count(*) AS regisThisMonth, 0 regisLastMonth, 0 totalCons, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " JOIN contractor_info c ON c.requestedbyid = a.id "
				+ " JOIN accounts con ON con.id = c.id "
				+ " AND a.active = 'Y' "
				+ " AND (au.startDate IS NULL OR au.startDate <= Now()) "
				+ " AND (au.endDate IS NULL OR au.endDate >= NOW()) "
				+ " AND con.active = 'Y' "
				+ " AND c.membershipDate > au.startDate " + " AND MONTHNAME(c.membershipDate) = '"
				+ getMonth()
				+ "'"
				+ " AND YEAR(c.membershipDate) = "
				+ getYear()
				+ " GROUP BY a.id, au.id "
				+

				" UNION "
				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, 0 regisThisMonth, count(*) AS regisLastMonth, 0 totalCons, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " JOIN contractor_info c ON c.requestedbyid = a.id "
				+ " JOIN accounts con ON con.id = c.id "
				+ " AND a.active = 'Y' "
				+ " AND (au.startDate IS NULL OR au.startDate <= Now()) "
				+ " AND (au.endDate IS NULL OR au.endDate >= NOW()) "
				+ " AND con.active = 'Y' "
				+ " AND c.membershipDate > au.startDate "
				+ " AND MONTHNAME(c.membershipDate) = '"
				+ getMonth()
				+ "'"
				+ " AND YEAR(c.membershipDate) = "
				+ getYear()
				+ " GROUP BY a.id, au.id "
				+

				" UNION "
				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, 0 regisThisMonth, 0 regisLastMonth, COUNT(*) AS totalCons, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " JOIN contractor_info c ON c.requestedbyid = a.id "
				+ " JOIN accounts con ON con.id = c.id "
				+ " AND a.active = 'Y' "
				+ " AND (au.startDate IS NULL OR au.startDate <= Now()) "
				+ " AND (au.endDate IS NULL OR au.endDate >= NOW()) "
				+ " AND con.active = 'Y' "
				+ " AND c.membershipDate BETWEEN au.startDate AND au.endDate "
				+ " GROUP BY a.id, au.id ) t "

				+ " LEFT JOIN (SELECT distinct opID, 1 audited FROM audit_operator "
				+ " JOIN audit_type at on at.id = audittypeid "
				+ " WHERE (auditTypeID IN (2,3,6) OR at.classType = 'IM') AND minRiskLevel > 0 AND canSee = 1) audited "
				+ " ON audited.opID = accountID "
				+ " JOIN operators o on o.id = accountID "
				+ " WHERE "
				+ where
				+ " GROUP BY accountID, auID " + " ORDER BY userName, role, accountName";

		data = db.select(sql, true);

		return SUCCESS;
	}

	public Map<String, Object> getSummaryData() throws SQLException {
		if (summaryData == null) {
			summary = db.select(getSummarySql(), false);

			summaryData = new HashMap<String, Object>();

			Map<Integer, Double> auAccCap = new HashMap<Integer, Double>();
			Map<Integer, Double> naAccountCap = new HashMap<Integer, Double>();
			Map<Integer, Double> auSalesCap = new HashMap<Integer, Double>();
			Map<Integer, Double> naSalesCap = new HashMap<Integer, Double>();

			double auAccountReps = 0;
			double naAccountReps = 0;
			double auSalesReps = 0;
			double naSalesReps = 0;

			double auAccThisMonth = 0;
			double naAccThisMonth = 0;
			double auSalThisMonth = 0;
			double naSalThisMonth = 0;

			double auAccLastMonth = 0;
			double naAccLastMonth = 0;
			double auSalLastMonth = 0;
			double naSalLastMonth = 0;

			double auAccTotal = 0;
			double naAccTotal = 0;
			double auSalTotal = 0;
			double naSalTotal = 0;

			summaryData.put("userName", summary.get(0).get("userName"));
			for (DynaBean bean : summary) {
				if (bean.get("role").toString().equals(UserAccountRole.PICSAccountRep.toString())) {
					if (bean.get("doContractorsPay").toString().equals("Yes")) {
						if (bean.get("audited") != null) {
							if (bean.get("isCorporate") == null) {
								auAccCap.put((Integer) bean.get("accountID"), calcPercentage(1, bean
										.get("ownerPercent")));
							} else if (bean.get("isCorporate").toString().equals("1")) {
								auAccCap.put((Integer) bean.get("corporateID"), calcPercentage(1, bean
										.get("ownerPercent")));
							}
							auAccountReps += calcPercentage(1, bean.get("ownerPercent"));
							auAccThisMonth += calcPercentage(bean.get("regisThisMonth"), bean.get("ownerPercent"));
							auAccLastMonth += calcPercentage(bean.get("regisLastMonth"), bean.get("ownerPercent"));
							auAccTotal += calcPercentage(bean.get("totalCons"), bean.get("ownerPercent"));
						} else {
							if (bean.get("isCorporate") == null) {
								naAccountCap.put((Integer) bean.get("accountID"), calcPercentage(1, bean
										.get("ownerPercent")));
							} else if (bean.get("isCorporate").equals("1")) {
								naAccountCap.put((Integer) bean.get("corporateID"), calcPercentage(1, bean
										.get("ownerPercent")));
							}
							naAccountReps += calcPercentage(1, bean.get("ownerPercent"));
							naAccThisMonth += calcPercentage(bean.get("regisThisMonth"), bean.get("ownerPercent"));
							naAccLastMonth += calcPercentage(bean.get("regisLastMonth"), bean.get("ownerPercent"));
							naAccTotal += calcPercentage(bean.get("totalCons"), bean.get("ownerPercent"));
						}
					}
				} else {
					if (bean.get("doContractorsPay").toString().equals("Yes")) {
						if (bean.get("audited") != null) {
							if (bean.get("isCorporate") == null) {
								auSalesCap.put((Integer) bean.get("accountID"), calcPercentage(1, bean
										.get("ownerPercent")));
							} else if (bean.get("isCorporate").equals("1")) {
								auSalesCap.put((Integer) bean.get("corporateID"), calcPercentage(1, bean
										.get("ownerPercent")));
							}
							auSalesReps += calcPercentage(1, bean.get("ownerPercent"));
							auSalThisMonth += calcPercentage(bean.get("regisThisMonth"), bean.get("ownerPercent"));
							auSalLastMonth += calcPercentage(bean.get("regisLastMonth"), bean.get("ownerPercent"));
							auSalTotal += calcPercentage(bean.get("totalCons"), bean.get("ownerPercent"));
						} else {
							if (bean.get("isCorporate") == null) {
								naSalesCap.put((Integer) bean.get("accountID"), calcPercentage(1, bean
										.get("ownerPercent")));
							} else if (bean.get("isCorporate").equals("1")) {
								naSalesCap.put((Integer) bean.get("corporateID"), calcPercentage(1, bean
										.get("ownerPercent")));
							}
							naSalesReps += calcPercentage(1, bean.get("ownerPercent"));
							naSalThisMonth += calcPercentage(bean.get("regisThisMonth"), bean.get("ownerPercent"));
							naSalLastMonth += calcPercentage(bean.get("regisLastMonth"), bean.get("ownerPercent"));
							naSalTotal += calcPercentage(bean.get("totalCons"), bean.get("ownerPercent"));
						}
					}
				}
			}

			summaryData.put("AuditedAccountReps", auAccountReps);
			summaryData.put("NonAuditedAccountReps", naAccountReps);
			summaryData.put("AuditedSalesReps", auSalesReps);
			summaryData.put("NonAuditedSalesReps", naSalesReps);

//			summaryData.put("AuditedAccountCapReps", auAccountReps);
//			summaryData.put("NonAuditedAccountCapReps", naAccountReps);
//			summaryData.put("AuditedSalesCapsReps", auSalesReps);
//			summaryData.put("NonAuditedSalesCapsReps", naSalesReps);

			summaryData.put("auAccThisMonth", auAccThisMonth);
			summaryData.put("naAccThisMonth", naAccThisMonth);
			summaryData.put("auSalThisMonth", auSalThisMonth);
			summaryData.put("naSalThisMonth", naSalThisMonth);

			summaryData.put("auAccLastMonth", auAccLastMonth);
			summaryData.put("naAccLastMonth", naAccLastMonth);
			summaryData.put("auSalLastMonth", auSalLastMonth);
			summaryData.put("naSalLastMonth", naSalLastMonth);

			summaryData.put("auAccTotal", auAccTotal);
			summaryData.put("naAccTotal", naAccTotal);
			summaryData.put("auSalTotal", auSalTotal);
			summaryData.put("naSalTotal", naSalTotal);
		}
		return summaryData;
	}

	public List<BasicDynaBean> getData() {
		return data;
	}

	public List<BasicDynaBean> getSummary() {
		return summary;
	}

	public boolean isShowSummary() {
		return showSummary;
	}

	public int getYear() {
		return DateBean.getCurrentYear();
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

	public double calcPercentage(Object total, Object percent) {
		return (Double.parseDouble(total.toString()) * Double.parseDouble(percent.toString())) / 100;
	}

	public String getMonth() {
		if (month == null) {
			return DateBean.getCurrentMonthName();
		}
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String[] getMonthsList() {
		return DateBean.MonthNames;
	}

	private String getSummarySql() {
		String sql = " SELECT accountID, type, userID, userName, accountName, audited.audited, o.doContractorsPay ,creationDate, role, ownerPercent, startDate, endDate, SUM(regisThisMonth) regisThisMonth, SUM(regisLastMonth) regisLastMonth, SUM(totalCons) totalCons, auID, corporate.id corporateID, corporate.primarycorporate isCorporate"
				+ " FROM ("

				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, 0 regisThisMonth, 0 regisLastMonth, 0 totalCons, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " AND a.active = 'Y' "
				+ " AND (au.startDate IS NULL OR au.startDate <= Now()) "
				+ " AND (au.endDate IS NULL OR au.endDate >= NOW()) "
				+ " UNION "

				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, count(*) AS regisThisMonth, 0 regisLastMonth, 0 totalCons, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " JOIN contractor_info c ON c.requestedbyid = a.id "
				+ " JOIN accounts con ON con.id = c.id "
				+ " AND a.active = 'Y' "
				+ " AND (au.startDate IS NULL OR au.startDate <= Now()) "
				+ " AND (au.endDate IS NULL OR au.endDate >= NOW()) "
				+ " AND con.active = 'Y' "
				+ " AND c.membershipDate > au.startDate " + " AND MONTHNAME(c.membershipDate) = '"
				+ getMonth()
				+ "'"
				+ " AND YEAR(c.membershipDate) = "
				+ getYear()
				+ " GROUP BY a.id, au.id "
				+

				" UNION "
				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, 0 regisThisMonth, count(*) AS regisLastMonth, 0 totalCons, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " JOIN contractor_info c ON c.requestedbyid = a.id "
				+ " JOIN accounts con ON con.id = c.id "
				+ " AND a.active = 'Y' "
				+ " AND (au.startDate IS NULL OR au.startDate <= Now()) "
				+ " AND (au.endDate IS NULL OR au.endDate >= NOW()) "
				+ " AND con.active = 'Y' "
				+ " AND c.membershipDate > au.startDate "
				+ " AND MONTHNAME(c.membershipDate) = '"
				+ getMonth()
				+ "'"
				+ " AND YEAR(c.membershipDate) = "
				+ getYear()
				+ " GROUP BY a.id, au.id "
				+

				" UNION "
				+ "Select a.id as accountID, a.type, u.id AS userID, u.name AS userName, a.name AS accountName, a.creationDate, au.role, au.ownerPercent, au.startDate, au.endDate, 0 regisThisMonth, 0 regisLastMonth, COUNT(*) AS totalCons, au.id AS auID"
				+ " FROM Users u JOIN account_user au ON au.userid = u.id "
				+ " JOIN accounts a ON a.id = au.accountid "
				+ " JOIN contractor_info c ON c.requestedbyid = a.id "
				+ " JOIN accounts con ON con.id = c.id "
				+ " AND a.active = 'Y' "
				+ " AND (au.startDate IS NULL OR au.startDate <= Now()) "
				+ " AND (au.endDate IS NULL OR au.endDate >= NOW()) "
				+ " AND con.active = 'Y' "
				+ " AND c.membershipDate BETWEEN au.startDate AND au.endDate "
				+ " GROUP BY a.id, au.id ) t "

				+ " LEFT JOIN (SELECT distinct opID, 1 audited FROM audit_operator "
				+ " JOIN audit_type at on at.id = audittypeid "
				+ " WHERE (auditTypeID IN (2,3,6) OR at.classType = 'IM') AND minRiskLevel > 0 AND canSee = 1) audited "
				+ " ON audited.opID = accountID "
				+ " JOIN operators o on o.id = accountID "
				+ " LEFT JOIN operators corporate on corporate.id = o.parentid "
				+ " WHERE "
				+ where
				+ " GROUP BY accountID, auID " + " ORDER BY userName, role, accountName";

		return sql;
	}
}
