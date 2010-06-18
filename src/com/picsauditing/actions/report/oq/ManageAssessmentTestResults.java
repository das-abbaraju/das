package com.picsauditing.actions.report.oq;

import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResultStage;

@SuppressWarnings("serial")
public class ManageAssessmentTestResults extends PicsActionSupport {
	private AccountDAO accountDAO;
	private AssessmentTestDAO testDAO;
	
	private int id;
	private Account center;
	
	public ManageAssessmentTestResults(AccountDAO accountDAO, AssessmentTestDAO testDAO) {
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
		
		return SUCCESS;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Account getCenter() {
		return center;
	}
	
	public List<AssessmentResultStage> getStaged() {
		return testDAO.findStaged(id);
	}
}