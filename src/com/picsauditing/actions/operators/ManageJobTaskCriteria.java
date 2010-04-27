package com.picsauditing.actions.operators;

//import com.picsauditing.access.OpPerms;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
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
	protected List<Date> history;
	protected int jobTaskID;
	protected int jobTaskCriteriaID;
	protected int assessmentTestID;
	protected int groupNumber;
	protected String date;

	protected Date effectiveDate = new Date(); // Set to today
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

		tryPermissions(OpPerms.ManageJobSites);
		findOperator();

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
				int highestGroupNumber = 0;
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
		
		history = jobTaskCriteriaDAO.findHistoryByTask(jobTaskID);
		
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
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public Date getEffectiveDate() {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			try {
				effectiveDate = sdf.parse(date);
			} catch (ParseException e) {
				effectiveDate = new Date();
			}
		}
		
		return effectiveDate;
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

	public List<JobTaskCriteria> getCriterias() {
		if (jobTask == null)
			jobTask = jobTaskDAO.find(jobTaskID);
		
		List<JobTaskCriteria> inEffect = jobTask.getJobTaskCriteria();
		Iterator<JobTaskCriteria> iterator = inEffect.iterator();
		
		while(iterator.hasNext()) {
			if (iterator.next().getEffectiveDate().after(getEffectiveDate()))
				iterator.remove();
		}
		
		return inEffect;
	}

	public Set<AssessmentTest> getAllAssessments() {
		if (jobTask == null)
			jobTask = jobTaskDAO.find(jobTaskID);
		
		return new HashSet<AssessmentTest>(assessmentTestDAO.findAll());
	}
	
	public List<Date> getHistory() {
		if (history == null)
			history = jobTaskCriteriaDAO.findHistoryByTask(jobTaskID);
		
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
}
