package com.picsauditing.actions.report.oq;

import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResultStage;

@SuppressWarnings("serial")
public class ManageImportData extends PicsActionSupport {
	private AccountDAO accountDAO;
	private AssessmentTestDAO testDAO;
	
	private int id;
	private int stageID;
	private Account center;
	private String subHeading = "Manage Import Data";
	
	public ManageImportData(AccountDAO accountDAO, AssessmentTestDAO testDAO) {
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
			if ("Remove".equals(button)) {
				if (stageID > 0) {
					List<AssessmentResultStage> list = getStaged();
					for (AssessmentResultStage stage : list) {
						if (stage.getId() == stageID) {
							testDAO.remove(stage);
							break;
						}
					}
					
					return redirect("ManageImportData.action" + 
							(permissions.isAssessment() ? "" : "?id=" + id));
				} else
					addActionError("Missing test result");
			}
		}
		
		return SUCCESS;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getStageID() {
		return stageID;
	}
	
	public void setStageID(int stageID) {
		this.stageID = stageID;
	}
	
	public Account getCenter() {
		return center;
	}
	
	public String getSubHeading() {
		return subHeading;
	}
	
	public List<AssessmentResultStage> getStaged() {
		return testDAO.findStaged(id);
	}
}