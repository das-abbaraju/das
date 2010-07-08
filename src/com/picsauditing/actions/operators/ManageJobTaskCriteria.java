package com.picsauditing.actions.operators;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	protected int groupNumber;
	protected boolean canEdit = false;

	protected List<String> history;
	protected Date date = new Date();
	protected JobTaskCriteria newJobTaskCriteria = new JobTaskCriteria();
	protected JobTask jobTask;
	protected AssessmentTest assessmentTest;

	public ManageJobTaskCriteria(OperatorAccountDAO operatorDao, JobTaskCriteriaDAO jobTaskCriteriaDAO,
			AssessmentTestDAO assessmentTestDAO, JobTaskDAO jobTaskDAO) {
		super(operatorDao);
		this.jobTaskCriteriaDAO = jobTaskCriteriaDAO;
		this.assessmentTestDAO = assessmentTestDAO;
		this.jobTaskDAO = jobTaskDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findOperator();
		
		if (jobTask == null && jobTaskID > 0) {
			jobTask = jobTaskDAO.find(jobTaskID);
			subHeading = jobTask.getLabel() + " " + jobTask.getName();
		}

		if (button != null) {
			tryPermissions(OpPerms.ManageJobTasks, OpType.Edit);
			
			if ("Save".equalsIgnoreCase(button)) {
				setupNewJobTaskCriteria();
				newJobTaskCriteria.setGroupNumber(groupNumber);
				jobTaskCriteriaDAO.save(newJobTaskCriteria);
				
				addActionMessage("Successfully added "+assessmentTest.getName()+" to group "+groupNumber);
			}
			
			if ("Create".equalsIgnoreCase(button)) {
				setupNewJobTaskCriteria();
				int highestGroupNumber = 0;
				for(int group : jobTask.getJobTaskCriteriaMap().keySet())
					if(group > highestGroupNumber)
						highestGroupNumber = group;
				
				newJobTaskCriteria.setGroupNumber(highestGroupNumber+1);
				jobTaskCriteriaDAO.save(newJobTaskCriteria);
				jobTask.getJobTaskCriteria().add(newJobTaskCriteria);
				
				addActionMessage("Successfully added "+assessmentTest.getName()+" to New Group");
			}

			if ("Remove".equalsIgnoreCase(button)) {
				newJobTaskCriteria = jobTaskCriteriaDAO.find(jobTaskCriteriaID);
				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
				assessmentTest = newJobTaskCriteria.getAssessmentTest();
				
				// If deleting something created today, just remove
				if (sdf.format(newJobTaskCriteria.getCreationDate()).equals(sdf.format(new Date())))
					jobTaskCriteriaDAO.remove(newJobTaskCriteria);
				else {
					newJobTaskCriteria.expire();
					jobTaskCriteriaDAO.save(newJobTaskCriteria);
				}
				
				addActionMessage("Successfully removed "+assessmentTest.getName()+" from group "+groupNumber);
			}

			return redirect("ManageJobTaskCriteria.action?id=" + operator.getId() + "&jobTaskID=" + jobTaskID);
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
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
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
		if (permissions.hasPermission(OpPerms.ManageJobTasks, OpType.Edit) 
				&& (date == null || maskDateFormat(date).equals(maskDateFormat(new Date()))))
			canEdit = true;
		
		return canEdit;
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
	
	public List<String> getHistory() {
		if (history == null) {
			List<Date> dates = jobTaskCriteriaDAO.findHistoryByTask(jobTaskID);
			history = new ArrayList<String>();

			if (!maskDateFormat(dates.get(0)).equals(maskDateFormat(new Date())))
				history.add(maskDateFormat(new Date()));
			
			for (Date date : dates) {
				history.add(maskDateFormat(date));
			}
		}
		
		if (history.size() > 1)
			return history;
		
		return null;
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
	
	public Map<Integer, Set<JobTaskCriteria>> getCriteriaMap() {
		if (jobTask == null)
			jobTask = jobTaskDAO.find(jobTaskID);
		
		if (date == null || date.equals(maskDateFormat(new Date())))
			return jobTask.getJobTaskCriteriaMap();
		else
			return jobTask.getJobTaskCriteriaMap(date);
	}
	
	private void setupNewJobTaskCriteria() throws Exception {
		if (assessmentTest == null)
			assessmentTest = assessmentTestDAO.find(assessmentTestID);
		
		if (jobTask == null)
			jobTask = jobTaskDAO.find(jobTaskID);
		
		newJobTaskCriteria.setAssessmentTest(assessmentTest);
		newJobTaskCriteria.setTask(jobTask);
		newJobTaskCriteria.setAuditColumns(permissions);
		newJobTaskCriteria.defaultDates();
		newJobTaskCriteria.setEffectiveDate(new Date());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		newJobTaskCriteria.setExpirationDate(sdf.parse("4000-01-01"));
	}
}
