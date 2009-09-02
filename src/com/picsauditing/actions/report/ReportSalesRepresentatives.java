package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportSalesRepresentatives extends PicsActionSupport {
	/**
	 * The operator cap a sales rep/AM gets credit for each corporate account
	 */

	Database db = new Database();
	private List<BasicDynaBean> data;
	private String where = "1";
	protected int[] operator;
	protected int[] accountUser;
	protected UserAccountRole responsibility;
	protected SummaryData summaryData = null;
	protected boolean showSummary = false;
	protected DataTotals dataTotals;
	private int month = 0;
	private Calendar calCurrent = Calendar.getInstance();
	private Calendar calPrevious = Calendar.getInstance();
	
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
		
		calCurrent.add(Calendar.MONTH, -1 * month);
		calPrevious.add(Calendar.MONTH, -1 * (month + 1));
		
		OperatorSalesSQL sqlAllOperators = new OperatorSalesSQL(calCurrent.getTime(), "0", "0", "0");
		
		// TODO pass in the new Date() effectiveDate
		OperatorContractorSalesSQL sqlRegistrationsThisMonth = new OperatorContractorSalesSQL(calCurrent.getTime(), "count(*)", "0", "0");
		sqlRegistrationsThisMonth.addWhere("c.membershipDate >= au.startDate");
		sqlRegistrationsThisMonth.addWhere("MONTH(c.membershipDate) = " + (calCurrent.get(Calendar.MONTH) + 1));
		sqlRegistrationsThisMonth.addWhere("YEAR(c.membershipDate) = " + calCurrent.get(Calendar.YEAR));

		// TODO change this to previous month!
		OperatorContractorSalesSQL sqlRegistrationsLastMonth = new OperatorContractorSalesSQL(calPrevious.getTime(), "0", "count(*)", "0");
		sqlRegistrationsLastMonth.addWhere("c.membershipDate >= au.startDate");
		sqlRegistrationsLastMonth.addWhere("MONTH(c.membershipDate) = " + (calPrevious.get(Calendar.MONTH) + 1));
		sqlRegistrationsLastMonth.addWhere("YEAR(c.membershipDate) = " + calPrevious.get(Calendar.YEAR));

		OperatorContractorSalesSQL sqlRegistrationsToDate = new OperatorContractorSalesSQL(calCurrent.getTime(), "0", "0", "count(*)");
		sqlRegistrationsToDate.addWhere("c.membershipDate BETWEEN au.startDate AND au.endDate");
		
		// TODO consider putting the dynamic "where" into the OperatorSalesSQL object
		String sql = " SELECT accountID, type, userID, userName, accountName, audited.audited, o.doContractorsPay, " +
					"creationDate, role, ownerPercent, startDate, endDate, SUM(regisThisMonth) regisThisMonth, " +
					"SUM(regisLastMonth) regisLastMonth, SUM(totalCons) totalCons, auID, corp.id AS corporateID "
				+ " FROM (" + sqlAllOperators
				+ " UNION " + sqlRegistrationsThisMonth
				+ " UNION " + sqlRegistrationsLastMonth
				+ " UNION " + sqlRegistrationsToDate
				+ " ) t "
				+ " LEFT JOIN (SELECT distinct opID, 1 audited FROM audit_operator "
				+ " JOIN audit_type at on at.id = audittypeid "
				+ " WHERE (auditTypeID IN (2,3,6) OR at.classType = 'IM') AND minRiskLevel > 0 AND canSee = 1) audited "
				+ " ON audited.opID = accountID "
				+ " JOIN operators o on o.id = accountID "
				+ " LEFT JOIN (SELECT f.opID, c.id FROM facilities f JOIN operators c ON f.corporateID = c.id AND c.primaryCorporate = 1) corp ON o.id = corp.opID "
				+ " WHERE "	+ where
				+ " GROUP BY accountID, auID " 
				+ " ORDER BY userName, role, accountName";

		data = db.select(sql, true);
		
		dataTotals = new DataTotals();
		for(DynaBean bean : data) {
			dataTotals.thisMonthCredited += calcPercentage(bean.get("regisThisMonth"), bean.get("ownerPercent"));
			dataTotals.thisMonthTotal += parse(bean.get("regisThisMonth"));
			dataTotals.lastMonthCredited += calcPercentage(bean.get("regisLastMonth"), bean.get("ownerPercent"));
			dataTotals.lastMonthTotal += parse(bean.get("regisLastMonth"));
			dataTotals.toDate += parse(bean.get("totalCons"));
//			System.out.println("thisMonthCredited = " + dataTotals.thisMonthCredited + " ------ " + bean.get("accountName"));
//			System.out.println("lastMonthCredited = " + dataTotals.lastMonthCredited + " ------ " + bean.get("accountName"));
		}
		
		dataTotals.thisMonthCredited = roundMe(dataTotals.thisMonthCredited);
		dataTotals.lastMonthCredited = roundMe(dataTotals.lastMonthCredited);

		return SUCCESS;
	}
	
	private double roundMe(double value) {
		return ((double)Math.round(value*10))/10;
	}

	public class DataTotals {
		public double thisMonthCredited = 0;
		public double lastMonthCredited = 0;
		public double thisMonthTotal = 0;
		public double lastMonthTotal = 0;
		public int toDate = 0;
	}

	public SummaryData getSummaryData() {
		if (summaryData == null) {
			summaryData = new SummaryData();
			
			for (DynaBean bean : data) {
				if (bean.get("doContractorsPay").toString().equals("Yes")) {
					// Ignore all free operator accounts
					if (bean.get("role").toString().equals(UserAccountRole.PICSAccountRep.toString())) {
						if (bean.get("audited") != null) {
							summaryData.accountManager.audited.setBean(bean);
						} else {
							// Non-audited
							summaryData.accountManager.nonAudited.setBean(bean);
						}
					} else {
						// Sales Reps
						if (bean.get("audited") != null) {
							summaryData.salesRep.audited.setBean(bean);
						} else {
							// Non-audited
							summaryData.salesRep.nonAudited.setBean(bean);
						}
					}
				}
			}
			
		}
		return summaryData;
	}
	
	public class SummaryData {
		public UserRoleType accountManager = new UserRoleType();
		public UserRoleType salesRep = new UserRoleType();
		
		public class UserRoleType {
			public AuditType audited = new AuditType();
			public AuditType nonAudited = new AuditType();
			
			public class AuditType {
				public int accountsManaged = 0;
				public double thisMonth = 0;
				public double lastMonth = 0;
				public double toDate = 0;
				
				private Map<Integer, List<Double>> corporateMap = new HashMap<Integer, List<Double>>();
				private double singleOperators;
				
				public void setBean(DynaBean bean) {
					if (bean.get("corporateID") == null) {
						singleOperators += calcPercentage(1, bean.get("ownerPercent"));
					} else {
						double value = calcPercentage(1, bean.get("ownerPercent"));
						int corporateID = (Integer) bean.get("corporateID");
						if (!corporateMap.containsKey(corporateID))
							corporateMap.put(corporateID, new ArrayList<Double>());
						corporateMap.get(corporateID).add(value);
					}
					
					accountsManaged ++;
					thisMonth += calcPercentage(bean.get("regisThisMonth"), bean.get("ownerPercent"));
					lastMonth += calcPercentage(bean.get("regisLastMonth"), bean.get("ownerPercent"));
					toDate += calcPercentage(bean.get("totalCons"), bean.get("ownerPercent"));
					
					thisMonth = roundMe(thisMonth);
					lastMonth = roundMe(lastMonth);
					toDate = roundMe(toDate);
				}
				
				public double getCappedOperatorCount() {
					
					double total = singleOperators;
					
					for(Integer corporateID : corporateMap.keySet()) {
						int rawCount = corporateMap.get(corporateID).size();
						double totalPercent = 0;
						for(Double ownerPercent : corporateMap.get(corporateID)) {
							totalPercent += ownerPercent;
						}
						if (rawCount > 20)
							total += 20 * totalPercent / rawCount;
						else {
							// rawCount factors out
							// total += rawCount * totalPercent / rawCount;
							total += totalPercent;
						}
					}

					return roundMe(total);
				}

			}
		}
	}
	
	public DataTotals getDataTotals() {
		return dataTotals;
	}

	public List<BasicDynaBean> getData() {
		return data;
	}

	public boolean isShowSummary() {
		return showSummary;
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		return operatorAccountDAO.findWhere(true, "active='Y'");
	}

	public List<User> getUserList() throws Exception {
		// Get PICS Sales Reps
		return userDAO.findByGroup(10801);
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

	public Map<Integer, String> getMonthsList() {
		Map<Integer, String> list = new TreeMap<Integer, String>();
		Calendar cal = Calendar.getInstance();
		for(int m=0; m < 6; m++) {
			list.put(m, cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + cal.get(Calendar.YEAR));
			cal.add(Calendar.MONTH, -1);
		}
		return list;
	}

	public int getMonth() {
		return month;
	}
	
	public String getCurrentMonthName() {
		return calCurrent.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
	}

	public String getPreviousMonthName() {
		return calPrevious.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
	}
	
	public final String getUrlCurrentMonth(String accountID, String startDate) {
		return getContractorListUrl(accountID) + 
			" AND c.membershipDate >= '" + startDate + "'" +
			" AND MONTH(c.membershipDate) = " + (calCurrent.get(Calendar.MONTH) + 1) +
			" AND YEAR(c.membershipDate) = " + calCurrent.get(Calendar.YEAR);
	}

	public final String getUrlPreviousMonth(String accountID, String startDate) {
		return getContractorListUrl(accountID) + 
			" AND c.membershipDate >= '" + startDate + "'" +
			" AND MONTH(c.membershipDate) = " + (calPrevious.get(Calendar.MONTH) + 1) +
			" AND YEAR(c.membershipDate) = " + calPrevious.get(Calendar.YEAR);
	}

	public final String getUrlToDate(String accountID, String startDate, String endDate) {
		return getContractorListUrl(accountID) + 
			" AND c.membershipDate BETWEEN '" + startDate + "' AND '" + endDate + "'";
	}
	
	private String getContractorListUrl(String accountID) {
		return "ContractorList.action?filter.visible=Y&filter.customAPI=c.mustPay='Yes'" + 
			" AND c.requestedbyid=" + accountID;
	}

	public void setMonth(int month) {
		this.month = month;
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
		double out = parse(total) * parse(percent) / 100;
		// System.out.println("calcPercentage: " + parse(total) + " * " + parse(percent) + " / 100 = " + out);
		return out;
	}
	
	private double parse(Object value) {
		return Double.parseDouble(value.toString());
	}
	
	private class OperatorSalesSQL extends SelectSQL {
		public OperatorSalesSQL(Date effectiveDate, String regisThisMonth, String regisLastMonth, String totalCons) {
			super("users u");
			addJoin("JOIN account_user au ON au.userid = u.id");
			addJoin("JOIN accounts a ON a.id = au.accountid");
			addWhere("a.active = 'Y'");
			
			String effectiveDateString;
			try {
				effectiveDateString = "'" + DateBean.toDBFormat(effectiveDate) + "'";
			} catch (Exception e) {
				effectiveDateString = "NOW()";
			}
			addWhere("au.startDate IS NULL OR au.startDate <= " + effectiveDateString);
			addWhere("au.endDate IS NULL OR au.endDate >= " + effectiveDateString);
			addField("a.id as accountID");
			addField("a.type");
			addField("u.id AS userID");
			addField("u.name AS userName");
			addField("a.name AS accountName");
			addField("a.creationDate");
			addField("au.role");
			addField("au.ownerPercent");
			addField("au.startDate");
			addField("au.endDate");
			addField("au.id as auID");
			addField(regisThisMonth + " AS regisThisMonth");
			addField(regisLastMonth + " AS regisLastMonth");
			addField(totalCons + " AS totalCons");
		}
	}
	
	private class OperatorContractorSalesSQL extends OperatorSalesSQL {
		public OperatorContractorSalesSQL(Date effectiveDate, String regisThisMonth, String regisLastMonth, String totalCons) {
			super(effectiveDate, regisThisMonth, regisLastMonth, totalCons);
			addJoin("JOIN contractor_info c ON c.requestedbyid = a.id");
			addJoin("JOIN accounts con ON con.id = c.id");
			addWhere("con.active = 'Y'");
			addWhere("c.mustPay = 'Yes'");
			addGroupBy("a.id");
			addGroupBy("au.id");
		}
	}
	
}
