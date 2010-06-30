package com.picsauditing.actions.report.oq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageUnmappedCompanies extends PicsActionSupport {
	private AccountDAO accountDAO;
	private AssessmentTestDAO testDAO;
	
	private int id;
	private int accountID;
	private Account center;
	private String companyName;
	private String subHeading = "Manage Unmapped Companies";
	
	public ManageUnmappedCompanies(AccountDAO accountDAO, AssessmentTestDAO testDAO) {
		this.accountDAO = accountDAO;
		this.testDAO = testDAO;
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
					
					if (companyName.contains(","))
						companyName = companyName.substring(0, companyName.indexOf(","));
					
					for (AssessmentResultStage stage : staged) {
						if (stage.getCompanyName().equals(companyName)) {
							stage.setPicsAccountID(accountID);
							testDAO.save(stage);
						}
					}
				}
			}
			
			return redirect("ManageUnmappedCompanies.action" + (permissions.isAssessment() ? "" : "?id=" + id));
		}
		
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
	
	// Lists
	public Map<String, List<AssessmentResultStage>> getUnmapped() {
		List<AssessmentResultStage> staged = testDAO.findStagedWhere(id, "a.picsAccountID = 0");
		Map<String, List<AssessmentResultStage>> map = new HashMap<String, List<AssessmentResultStage>>();
		
		for (AssessmentResultStage stage : staged) {
			if (map.get(stage.getCompanyName()) == null)
				map.put(stage.getCompanyName(), new ArrayList<AssessmentResultStage>());
			
			map.get(stage.getCompanyName()).add(stage);
		}
		
		return map;
	}
}