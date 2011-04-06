package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportCsrActivity extends ReportActionSupport {
	Database db = new Database();
	private List<BasicDynaBean> data;
	protected List<User> csrs = null;
	protected UserDAO userDAO = null;
	protected int[] csrIds;
	protected String year = "";


	public ReportCsrActivity(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if(!filterOn(csrIds)) {
			csrIds = new int[getCsrs().size()];
			if(permissions.hasGroup(User.GROUP_MANAGER)) {
				int i = 0;
				for(User u : getCsrs()) {
					csrIds[i] = u.getId();
					i++;
				}
			}
			else {
				csrIds[0] = permissions.getUserId();
			}
		}
		
		if(Strings.isEmpty(year)) 
			year = Integer.toString(DateBean.getCurrentYear());
		
		CsrActivitySQL aUVerified = new CsrActivitySQL("count(*)", "0", "0", "0", "0");
		aUVerified
				.addWhere("(n.summary like '%verified% Annual% Update%' or n.summary like '%Annual Update% to Complete%' "
						+ " or n.summary like '%verified% PQF%' or n.summary like '%PQF% to Complete%')");

		CsrActivitySQL aURejected = new CsrActivitySQL("0", "count(*)", "0", "0", "0");
		aURejected
				.addWhere("(summary like '%rejected% Annual% Update%' or summary like '%Annual Update% to InComplete%'"
						+ " or summary like '%rejected% PQF%' or summary like '%PQF% to InComplete%')");

		CsrActivitySQL policyVerified = new CsrActivitySQL("0", "0", "count(*)", "0", "0");
		policyVerified.addJoin("join audit_type at on n.summary like concat('%',at.auditname,'%')");
		policyVerified.addWhere("(n.summary like '%Verified%' or n.summary like '%Complete%')");
		policyVerified.addWhere("at.classType = 'Policy'");

		CsrActivitySQL policyRejected = new CsrActivitySQL("0", "0", "0", "count(*)", "0");
		policyRejected.addJoin("join audit_type at on n.summary like concat('%',at.auditname,'%')");
		policyRejected.addWhere("(n.summary like '%rejected%' or n.summary like '%InComplete%')");
		policyRejected.addWhere("at.classType = 'Policy'");

		CsrActivitySQL notesCreated = new CsrActivitySQL("0", "0", "0", "0", "count(*)");
		String sql = "SELECT sum(AUVerified) as AUVerified, sum(AURejected) as AURejected, sum(InsuranceVerified) as InsuranceVerified, sum(InsuranceRejected) as InsuranceRejected,sum(notesCreated) as notesCreated, u.name, month_name FROM "
				+ " ( "
				+ aUVerified
				+ " UNION "
				+ aURejected
				+ " UNION "
				+ policyVerified
				+ " UNION "
				+ policyRejected
				+ " UNION " + notesCreated + ") t " +
						" JOIN users u on u.id = t.createdBy" +
				" Group By t.month_name,t.createdBy " +
				" Order By u.name, t.month_number" ;

		Database db = new Database();
		data = db.select(sql, true);

		return SUCCESS;
	}

	public List<BasicDynaBean> getData() {
		return data;
	}

	private class CsrActivitySQL extends SelectSQL {
		public CsrActivitySQL(String AUVerified, String AURejected, String InsuranceVerified, String InsuranceRejected,
				String notesCreated) {
			super("note n");
			String list = Strings.implode(csrIds, ",");
			addWhere("n.createdBy IN (" + list + ")");
			addWhere("n.status = 2");
			addWhere("year(n.creationDate) = " + year);
			addGroupBy("month(n.creationDate), n.createdBy");
			addField(AUVerified + " as AUVerified");
			addField(AURejected + " as AURejected");
			addField(InsuranceVerified + " as InsuranceVerified");
			addField(InsuranceRejected + " as InsuranceRejected");
			addField(notesCreated + " as notesCreated");
			addField("n.createdBy");
			addField("monthname(n.creationDate) as month_name");
			addField("month(n.creationDate) as month_number");
		}
	}

	public List<User> getCsrs() {
		if(csrs == null) {
			csrs = new ArrayList<User>();
			csrs = userDAO.findByGroup(User.GROUP_CSR);
		}
		return csrs;
	}
	public int[] getCsrIds() {
		return csrIds;
	}
	public void setCsrIds(int[] csrIds) {
		this.csrIds = csrIds;
	}
	public List<String> getYearsList() {
		List<String> yearsList = new ArrayList<String>();
		int lastYear = DateBean.getCurrentYear();
		for(int i = lastYear; i > 2000; i--) {
			yearsList.add(Integer.toString(i));
		}
		return yearsList;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
}
