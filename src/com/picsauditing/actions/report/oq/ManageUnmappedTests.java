package com.picsauditing.actions.report.oq;

import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilter;

@SuppressWarnings("serial")
public class ManageUnmappedTests extends ReportActionSupport {
	private AccountDAO accountDAO;
	private AssessmentTestDAO testDAO;
	private ReportFilter filter;
	
	private int id;
	private int stageID;
	private Account center;
	private SelectSQL sql = new SelectSQL();
	private String subHeading = "Test Mapping";
	
	public ManageUnmappedTests(AccountDAO accountDAO, AssessmentTestDAO testDAO) {
		this.accountDAO = accountDAO;
		this.testDAO = testDAO;
	}
	
	private void buildQuery() {
		sql.setFromTable("assessment_result_stage a");
		sql.addJoin("LEFT JOIN assessment_test t USING (qualificationType, qualificationMethod, description)");
		sql.addField("a.qualificationType");
		sql.addField("a.qualificationMethod");
		sql.addField("a.description");
		sql.addField("a.id");
		sql.addField("COUNT(*) AS records");
		sql.addWhere("a.centerID = " + id);
		sql.addWhere("t.qualificationType IS NULL");
		sql.addWhere("t.qualificationMethod IS NULL");
		sql.addWhere("t.description IS NULL");
		sql.addGroupBy("a.qualificationType, a.qualificationMethod, a.description");
		sql.addOrderBy("records DESC, a.qualificationType, a.qualificationMethod, a.description");
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
			if ("Add".equals(button) && stageID > 0) {
				List<AssessmentResultStage> staged = testDAO.findStaged(id);
				
				for (AssessmentResultStage stage : staged) {
					if (stage.getId() == stageID) {
						AssessmentTest test = new AssessmentTest();
						test.setAssessmentCenter(center);
						test.setAuditColumns(permissions);
						test.setDescription(stage.getDescription());
						test.setQualificationMethod(stage.getQualificationMethod());
						test.setQualificationType(stage.getQualificationType());
						
						testDAO.save(test);
						break;
					}
				}
			}
			
			return redirect("ManageUnmappedTests.action" + (permissions.isAssessment() ? "" : "?id=" + id));
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
	
	public ReportFilter getFilter() {
		return filter;
	}
	
	public void setFilter(ReportFilter filter) {
		this.filter = filter;
	}
}