package com.picsauditing.actions.operators;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.dao.JobTaskCriteriaDAO;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.JobTaskCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class ManageJobTaskCriteria extends OperatorActionSupport {
	@Autowired
	protected AssessmentTestDAO assessmentTestDAO;
	@Autowired
	protected JobTaskDAO jobTaskDAO;
	@Autowired
	protected JobTaskCriteriaDAO jobTaskCriteriaDAO;
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;

	protected AssessmentTest assessmentTest;
	protected JobTask jobTask;
	protected OperatorAccount operator;
	protected int groupNumber;
	protected int jobTaskCriteriaID;
	
	@Override
	public String execute() throws Exception {
		subHeading = String.format("%s: %s", jobTask.getLabel(), jobTask.getName());
		
		if (ActionContext.getContext().getSession().get("actionErrors") != null) {
			setActionErrors((Collection<String>) ActionContext.getContext().getSession().get("actionErrors"));
			ActionContext.getContext().getSession().remove("actionErrors");
		}
		
		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageJobTasks, type = OpType.Edit)
	public String create() throws Exception {
		JobTaskCriteria jobTaskCriteria = setupNewJobTaskCriteria();

		int highestGroupNumber = 0;
		for (int group : jobTask.getJobTaskCriteriaMap().keySet())
			if (group > highestGroupNumber)
				highestGroupNumber = group;

		jobTaskCriteria.setGroupNumber(highestGroupNumber + 1);
		jobTaskCriteriaDAO.save(jobTaskCriteria);
		jobTask.getJobTaskCriteria().add(jobTaskCriteria);

		return getRedirect(getText(String.format("%s.message.AddedNewGroup", getScope()),
				new Object[] { assessmentTest.getName() }));
	}

	@RequiredPermission(value = OpPerms.ManageJobTasks, type = OpType.Edit)
	public String save() throws Exception {
		JobTaskCriteria jobTaskCriteria = setupNewJobTaskCriteria();
		jobTaskCriteria.setGroupNumber(groupNumber);
		jobTaskCriteriaDAO.save(jobTaskCriteria);

		return getRedirect(getText(String.format("%s.message.AddedToGroup", getScope()),
				new Object[] { assessmentTest.getName(), (Integer) groupNumber }));
	}

	@RequiredPermission(value = OpPerms.ManageJobTasks, type = OpType.Delete)
	public String remove() throws Exception {
		JobTaskCriteria jobTaskCriteria = jobTaskCriteriaDAO.find(jobTaskCriteriaID);
		assessmentTest = jobTaskCriteria.getAssessmentTest();

		// If deleting something created today, just remove
		if (DateBean.format(jobTaskCriteria.getCreationDate(), "yyyy-MM-dd").equals(
				DateBean.format(new Date(), "yyyy-MM-dd")))
			jobTaskCriteriaDAO.remove(jobTaskCriteria);
		else {
			jobTaskCriteria.expire();
			jobTaskCriteriaDAO.save(jobTaskCriteria);
		}

		return getRedirect(getText(String.format("%s.message.RemovedFromGroup", getScope()), new Object[] {
				assessmentTest.getName(), groupNumber }));
	}

	private String getRedirect(String actionMessage) throws Exception {
		addActionMessage(actionMessage);
		ActionContext.getContext().getSession().put("actionErrors", getActionErrors());
		return redirect("ManageJobTaskCriteria.action?operator=" + operator.getId() + "&jobTask=" + jobTask.getId());
	}

	public boolean isCanEdit() {
		Date date = new Date();
		if (permissions.hasPermission(OpPerms.ManageJobTasks, OpType.Edit)
				&& (date == null || maskDateFormat(date).equals(maskDateFormat(new Date()))))
			return true;

		return false;
	}

	public Set<AssessmentTest> getAllAssessments() {
		return new HashSet<AssessmentTest>(assessmentTestDAO.findAll());
	}

	public Set<AssessmentTest> getUsedAssessmentsByGroup(int groupNumber) {
		List<AssessmentTest> remainingAssessments = assessmentTestDAO.findAll();
		for (JobTaskCriteria criteria : jobTask.getJobTaskCriteriaMap().get(groupNumber))
			if (remainingAssessments.contains(criteria.getAssessmentTest()))
				remainingAssessments.remove(criteria.getAssessmentTest());

		return new HashSet<AssessmentTest>(remainingAssessments);
	}

	public Map<Integer, Set<JobTaskCriteria>> getCriteriaMap() {
		Date date = new Date();
		if (date == null || date.equals(maskDateFormat(new Date())))
			return jobTask.getJobTaskCriteriaMap();
		else
			return jobTask.getJobTaskCriteriaMap(date);
	}

	private JobTaskCriteria setupNewJobTaskCriteria() throws Exception {
		JobTaskCriteria jobTaskCriteria = new JobTaskCriteria();
		jobTaskCriteria.setAssessmentTest(assessmentTest);
		jobTaskCriteria.setTask(jobTask);
		jobTaskCriteria.setAuditColumns(permissions);
		jobTaskCriteria.defaultDates();
		jobTaskCriteria.setEffectiveDate(new Date());
		jobTaskCriteria.setExpirationDate(DateBean.getEndOfTime());

		return jobTaskCriteria;
	}

	public AssessmentTest getAssessmentTest() {
		return assessmentTest;
	}

	public void setAssessmentTest(AssessmentTest assessmentTest) {
		this.assessmentTest = assessmentTest;
	}

	public JobTask getJobTask() {
		return jobTask;
	}

	public void setJobTask(JobTask jobTask) {
		this.jobTask = jobTask;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public int getJobTaskCriteriaID() {
		return jobTaskCriteriaID;
	}

	public void setJobTaskCriteriaID(int jobTaskCriteriaID) {
		this.jobTaskCriteriaID = jobTaskCriteriaID;
	}
}
