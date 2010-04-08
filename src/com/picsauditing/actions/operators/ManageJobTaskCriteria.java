package com.picsauditing.actions.operators;

//import com.picsauditing.access.OpPerms;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.dao.JobTaskCriteriaDAO;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.JobTaskCriteria;

@SuppressWarnings("serial")
public class ManageJobTaskCriteria extends OperatorActionSupport {
	protected JobTaskDAO jobTaskDAO;
	protected JobTaskCriteriaDAO jobTaskCriteriaDAO;
	protected AssessmentTestDAO assessmentTestDAO;

	protected int jobTaskID;
	protected int jobTaskCriteriaID;
	protected int assessmentTestID;

	protected JobTaskCriteria newJobTaskCriteria = new JobTaskCriteria();
	protected JobTask jobTask = new JobTask();
	protected AssessmentTest assessmentTest = new AssessmentTest();

	public ManageJobTaskCriteria(OperatorAccountDAO operatorDao, JobTaskCriteriaDAO jobTaskCriteriaDAO,
			AssessmentTestDAO assessmentTestDAO, JobTaskDAO jobTaskDAO) {
		super(operatorDao);
		this.jobTaskCriteriaDAO = jobTaskCriteriaDAO;
		this.assessmentTestDAO = assessmentTestDAO;
		this.jobTaskDAO = jobTaskDAO;

		subHeading = "Manage Job Task Criteria";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findOperator();
		tryPermissions(OpPerms.ManageJobSites);

		if (button != null) {
			if ("Save".equalsIgnoreCase(button)) {
				// Labels are required
				if (newJobTaskCriteria.getAssessmentTest() == null)
					addActionError("Please add an Assessment for this Criteria.");
				// Operators are required, but if one isn't set,
				// this operator should be added by default

				assessmentTest = assessmentTestDAO.find(assessmentTestID);
				if (assessmentTest != null)
					newJobTaskCriteria.setAssessmentTest(assessmentTest);

				jobTask = jobTaskDAO.find(jobTaskID);
				if (jobTask != null)
					newJobTaskCriteria.setTask(jobTask);

				if (getActionErrors().size() > 0)
					return SUCCESS;
			}

			if ("Remove".equalsIgnoreCase(button)) {
				newJobTaskCriteria = jobTaskCriteriaDAO.find(jobTaskCriteriaID);
				jobTaskCriteriaDAO.remove(newJobTaskCriteria);
			} else {
				jobTaskCriteriaDAO.save(newJobTaskCriteria);
			}

			if (permissions.isOperator())
				return redirect("ManageJobTaskCriteria.action");
			else
				return redirect("ManageJobTaskCriteria.action?id=" + operator.getId());
		}

		return SUCCESS;
	}

	public int getJobTaskID() {
		return jobTaskID;
	}

	public void setJobTaskID(int jobTaskID) {
		this.jobTaskID = jobTaskID;
	}

	public int getJobTaskCriteriaID() {
		return jobTaskCriteriaID;
	}

	public void setJobTaskCriteriaID(int jobTaskCriteriaID) {
		this.jobTaskCriteriaID = jobTaskCriteriaID;
	}

	public JobTaskCriteria getNewJobTaskCriteria() {
		return newJobTaskCriteria;
	}

	public void setNewJobTaskCriteria(JobTaskCriteria newJobTaskCriteria) {
		this.newJobTaskCriteria = newJobTaskCriteria;
	}

	public int getAssessmentTestID() {
		return assessmentTestID;
	}

	public void setAssessmentTestID(int assessmentTestID) {
		this.assessmentTestID = assessmentTestID;
	}

	public JobTask getJobTask() {
		return jobTask;
	}

	public void setJobTask(JobTask jobTask) {
		this.jobTask = jobTask;
	}

	public AssessmentTest getAssessmentTest() {
		return assessmentTest;
	}

	public void setAssessmentTest(AssessmentTest assessmentTest) {
		this.assessmentTest = assessmentTest;
	}

	public boolean isCanEdit() {
		return permissions.hasPermission(OpPerms.ManageJobSites, OpType.Edit);
	}

	public List<JobTaskCriteria> getCriterias() {
		return jobTaskCriteriaDAO.findByTask(jobTaskID);
	}

	public List<AssessmentTest> getAllAssessments() {
		return assessmentTestDAO.findAll();
	}
}
