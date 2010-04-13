package com.picsauditing.actions.operators;

//import com.picsauditing.access.OpPerms;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.Preparable;
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
public class ManageJobTaskCriteria extends OperatorActionSupport implements Preparable {
	protected JobTaskDAO jobTaskDAO;
	protected JobTaskCriteriaDAO jobTaskCriteriaDAO;
	protected AssessmentTestDAO assessmentTestDAO;
	
	protected int jobTaskID;
	protected int jobTaskCriteriaID;
	protected int assessmentTestID;
	protected int groupNumber;

	protected JobTaskCriteria newJobTaskCriteria = new JobTaskCriteria();
	protected JobTask jobTask;
	protected AssessmentTest assessmentTest;

	@Override
	public void prepare() throws Exception {
		if (jobTask == null)
			jobTask = jobTaskDAO.find(jobTaskID);
	}
	
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
				if (assessmentTest == null)
					assessmentTest = assessmentTestDAO.find(assessmentTestID);
				
				if (jobTask == null)
					jobTask = jobTaskDAO.find(jobTaskID);
				
				newJobTaskCriteria.setAssessmentTest(assessmentTest);
				newJobTaskCriteria.setTask(jobTask);
				newJobTaskCriteria.setGroupNumber(groupNumber);

				jobTaskCriteriaDAO.save(newJobTaskCriteria);
				
				addActionMessage("Successfully added "+assessmentTest.getName()+" to group "+groupNumber);
				
				return SUCCESS;
			}
			
			if ("Create".equalsIgnoreCase(button)) {
				if (assessmentTest == null)
					assessmentTest = assessmentTestDAO.find(assessmentTestID);
				
				if (jobTask == null)
					jobTask = jobTaskDAO.find(jobTaskID);
				
				newJobTaskCriteria.setAssessmentTest(assessmentTest);
				newJobTaskCriteria.setTask(jobTask);
				int highestGroupNumber = Integer.MIN_VALUE;
				for(int group : jobTask.getJobTaskCriteriaMap().keySet())
					if(group > highestGroupNumber)
						highestGroupNumber = group;
				newJobTaskCriteria.setGroupNumber(highestGroupNumber+1);

				jobTaskCriteriaDAO.save(newJobTaskCriteria);
				jobTask.getJobTaskCriteria().add(newJobTaskCriteria);
				
				addActionMessage("Successfully added "+assessmentTest.getName()+" to New Group");
				
				return SUCCESS;
			}

			if ("Remove".equalsIgnoreCase(button)) {
				newJobTaskCriteria = jobTaskCriteriaDAO.find(jobTaskCriteriaID);
				assessmentTest = newJobTaskCriteria.getAssessmentTest();
				jobTaskCriteriaDAO.remove(newJobTaskCriteria);
				
				addActionMessage("Successfully removed "+assessmentTest.getName()+" from group "+groupNumber);
				
				return SUCCESS;
			}

			if (permissions.isOperator())
				return redirect("ManageJobTaskCriteria.action");
			else
				return redirect("ManageJobTaskCriteria.action?id=" + operator.getId());
		}

		return SUCCESS;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
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
		if (jobTask == null)
			jobTask = jobTaskDAO.find(jobTaskID);
		
		return jobTask.getJobTaskCriteria();
	}

	public Set<AssessmentTest> getAllAssessments() {
		if (jobTask == null)
			jobTask = jobTaskDAO.find(jobTaskID);
		
		return new HashSet<AssessmentTest>(assessmentTestDAO.findAll());
	}
	
	public Set<AssessmentTest> getUsedAssessmentsByGroup(int groupNumber) {
		if (jobTask == null)
			jobTask = jobTaskDAO.find(jobTaskID);
		
		List<AssessmentTest> remainingAssessments = assessmentTestDAO.findAll();
		for(JobTaskCriteria criteria : jobTask.getJobTaskCriteriaMap().get(groupNumber))
			if(remainingAssessments.contains(criteria.getAssessmentTest()))
				remainingAssessments.remove(criteria.getAssessmentTest());
		
		return new HashSet<AssessmentTest>(remainingAssessments);
	}
}
