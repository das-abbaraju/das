package com.picsauditing.actions.report.oq;

import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageAssessmentTests extends PicsActionSupport {
	private AccountDAO accountDAO;
	private AssessmentTestDAO testDAO;

	private int id;
	private int testID;
	private Account center;
	private AssessmentTest test;
	private String subHeading = "Manage Assessment Tests";

	public ManageAssessmentTests(AccountDAO accountDAO,
			AssessmentTestDAO testDAO) {
		this.accountDAO = accountDAO;
		this.testDAO = testDAO;
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
				test.setAssessmentCenter(center);
				test.setAuditColumns(permissions);

				if (test.getMonthsToExpire() > 0
						&& test.getExpirationDate() == null
						&& test.getEffectiveDate() != null)
					test.setExpirationDate(DateBean.addMonths(test
							.getEffectiveDate(), test.getMonthsToExpire()));

				if (Strings.isEmpty(test.getQualificationType()))
					addActionError("Please fill in the qualification type.");

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
				
				return redirect("ManageAssessmentTests.action" + 
						(permissions.isAssessment() ? "" : "?id=" + id));
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

	// LISTS
	public List<AssessmentTest> getTests() {
		return testDAO.findByAssessmentCenter(id);
	}
}
