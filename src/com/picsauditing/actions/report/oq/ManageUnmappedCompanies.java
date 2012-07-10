package com.picsauditing.actions.report.oq;

import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilter;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageUnmappedCompanies extends ReportActionSupport {
	private AccountDAO accountDAO;
	private AssessmentTestDAO testDAO;
	
	private int id;
	private int accountID;
	private Account center;
	private ReportFilter filter = new ReportFilter();
	private SelectSQL sql = new SelectSQL();
	private String companyName;
	private String subHeading = "Company Mapping";
	
	public ManageUnmappedCompanies(AccountDAO accountDAO, AssessmentTestDAO testDAO) {
		this.accountDAO = accountDAO;
		this.testDAO = testDAO;
	}
	
	private void buildQuery() {
		sql.setFromTable("assessment_result_stage a");
		sql.addField("a.companyName");
		sql.addField("COUNT(*) AS records");
		sql.addField("a.id");
		sql.addWhere("a.centerID = " + id);
		sql.addWhere("a.picsAccountID IS NULL OR a.picsAccountID = 0");
		sql.addGroupBy("a.companyName");
		sql.addOrderBy("COUNT(*) DESC");
	}
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (!permissions.isAdmin() && !permissions.isAssessment())
			throw new NoRightsException("Administrator or Assessment Center");
		
		if (permissions.isAssessment() && id != permissions.getAccountId())
			id = permissions.getAccountId();
		
		if (id > 0)
			center = accountDAO.find(id);
		
		if (button != null) {
			if ("Save".equals(button)) {
				if (!Strings.isEmpty(companyName) && accountID > 0) {
					List<AssessmentResultStage> staged = testDAO.findStaged(id);
					
					for (AssessmentResultStage stage : staged) {
						if (stage.getCompanyName().equals(companyName)) {
							Account account = new Account();
							account.setId(accountID);
							stage.setPicsAccount(account);
							
							testDAO.save(stage);
						}
					}
				}
			}
			
			return redirect("ManageUnmappedCompanies.action" + (permissions.isAssessment() ? "" : "?id=" + id));
		}
		
		buildQuery();
		run(sql);
		
		return SUCCESS;
	}
	
	// Getters and Setters
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getAccountID() {
		return accountID;
	}
	
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}
	
	public Account getCenter() {
		return center;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getSubHeading() {
		return subHeading;
	}
	
	public ReportFilter getFilter() {
		return filter;
	}
	
	public void setFilter(ReportFilter filter) {
		this.filter = filter;
	}
}