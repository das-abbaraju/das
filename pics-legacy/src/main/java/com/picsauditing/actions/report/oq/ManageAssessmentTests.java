package com.picsauditing.actions.report.oq;

import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilter;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageAssessmentTests extends ReportActionSupport {
	private AccountDAO accountDAO;
	private AssessmentTestDAO testDAO;

	private int id;
	private int stageID;
	private int testID;
	private Account center;
	private AssessmentTest test;
	private ReportFilter filter = new ReportFilter();
	private SelectSQL sql = new SelectSQL("assessment_test t");
	private String subHeading = "Manage Assessment Tests";

	public ManageAssessmentTests(AccountDAO accountDAO, AssessmentTestDAO testDAO) {
		this.accountDAO = accountDAO;
		this.testDAO = testDAO;
		
		orderByDefault = "t.qualificationType, t.qualificationMethod";
	}
	
	protected void buildQuery() {
		sql.addField("t.id");
		sql.addField("t.qualificationType");
		sql.addField("t.qualificationMethod");
		sql.addField("t.description");
		sql.addField("DATE_FORMAT(t.effectiveDate, '%m/%d/%Y') AS effectiveDate");
		sql.addField("t.verifiable");
		sql.addField("t.monthsToExpire");
		
		sql.addWhere("t.assessmentCenterID = " + id);
		sql.addOrderBy(getOrderBy());
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (!permissions.isAdmin() && !permissions.isAssessment())
			throw new NoRightsException("Administrator or Assessment Center");

		if (permissions.isAdmin() && id == 0) {
			addActionError("Missing Assessment Center ID");
			return SUCCESS;
		}

		if (permissions.isAssessment())
			id = permissions.getAccountId();
		center = accountDAO.find(id);

		if (center == null) {
			addActionError("Could not find assessment center");
			return SUCCESS;
		}

		if (button != null) {
			if (button.equals("Load")) {
				if (testID > 0)
					test = testDAO.find(testID);
				else
					test = new AssessmentTest();

				return SUCCESS;
			}

			if (button.equals("Save")) {
				if (stageID > 0) {
					// Unmapped test being saved as a new test?
					AssessmentResultStage result = null;
					List<AssessmentResultStage> staged = testDAO.findStaged(id);
					
					for (AssessmentResultStage stage : staged) {
						if (stage.getId() == stageID) {
							result = stage;
							break;
						}
					}
					
					if (result != null) {
						test = new AssessmentTest();
						test.setAuditColumns(permissions);
						test.setAssessmentCenter(center);
						test.setDescription(result.getDescription());
						test.setQualificationMethod(result.getQualificationMethod());
						test.setQualificationType(result.getQualificationType());
					} else
						addActionError("Could not create new assessment test from unmapped test.");
				} else {
					test.setAssessmentCenter(center);
					test.setAuditColumns(permissions);
	
					if (test.getMonthsToExpire() > 0
							&& test.getExpirationDate() == null
							&& test.getEffectiveDate() != null)
						test.setExpirationDate(DateBean.addMonths(test
								.getEffectiveDate(), test.getMonthsToExpire()));
	
					if (Strings.isEmpty(test.getQualificationType()))
						addActionError("Please fill in the qualification type.");
				}
				
				if (getActionErrors().size() == 0) {
					testDAO.save(test);
					test = new AssessmentTest();
				}
			}
			
			if (button.equals("Remove")) {
				if (testID > 0)
					test = testDAO.find(testID);
				else
					addActionError("Missing test ID");
				
				if (getActionErrors().size() == 0)
					testDAO.remove(test);
				
				return setUrlForRedirect("ManageAssessmentTests.action" + 
						(permissions.isAssessment() ? "" : "?id=" + id));
			}
		}
		
		buildQuery();
		run(sql);

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

	public int getTestID() {
		return testID;
	}

	public void setTestID(int testID) {
		this.testID = testID;
	}

	public Account getCenter() {
		return center;
	}

	public AssessmentTest getTest() {
		return test;
	}

	public void setTest(AssessmentTest test) {
		this.test = test;
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

	// LISTS	
	public List<AssessmentResultStage> getUnmapped() {
		return testDAO.findUnmappedTests(id);
	}
}
