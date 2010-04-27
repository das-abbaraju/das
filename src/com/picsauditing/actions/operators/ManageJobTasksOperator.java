package com.picsauditing.actions.operators;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageJobTasksOperator extends OperatorActionSupport {
	protected JobTaskDAO jobTaskDAO;

	protected int jobTaskID;
	protected boolean taskActive;
	protected String jobTaskLabel;
	protected String jobTaskName;
	protected String taskType;

	protected JobTask newTask = new JobTask();

	public ManageJobTasksOperator(OperatorAccountDAO operatorDao, JobTaskDAO jobTaskDAO) {
		super(operatorDao);
		this.jobTaskDAO = jobTaskDAO;

		subHeading = "Manage Job Tasks";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findOperator();
		tryPermissions(OpPerms.ManageJobTasks);

		if (button != null) {
			if ("Tasks".equalsIgnoreCase(button)) {
				newTask = jobTaskDAO.find(jobTaskID);
				return SUCCESS;
			}
			
			// Check if they can edit here
			tryPermissions(OpPerms.ManageJobTasks, OpType.Edit);

			if ("Save".equalsIgnoreCase(button)) {
				// Labels are required
				if (Strings.isEmpty(newTask.getLabel()))
					addActionError("Please add a label to this job site.");
				// Operators are required, but if one isn't set,
				// this operator should be added by default
				if (newTask.getOperator() == null && operator != null)
					newTask.setOperator(operator);
			}
			
			if ("Edit".equalsIgnoreCase(button)) {
				if (jobTaskID > 0 && !Strings.isEmpty(jobTaskLabel)) {
					newTask = jobTaskDAO.find(jobTaskID);
					newTask.setLabel(jobTaskLabel);
					
					if (!Strings.isEmpty(jobTaskName))
						newTask.setName(jobTaskName);
					
					newTask.setActive(taskActive);
					newTask.setTaskType(taskType);
				} else
					addActionError("Missing job task ID or label");
			}

			if ("Remove".equalsIgnoreCase(button)) {
				newTask = jobTaskDAO.find(jobTaskID);
				jobTaskDAO.remove(newTask);
			}
			
			if (getActionErrors().size() > 0)
				return SUCCESS;
			
			newTask.setAuditColumns(permissions);
			jobTaskDAO.save(newTask);

			if (permissions.isOperator())
				return redirect("ManageJobTasksOperator.action");
			else
				return redirect("ManageJobTasksOperator.action?id=" + operator.getId());
		}

		return SUCCESS;
	}

	public int getJobTaskID() {
		return jobTaskID;
	}

	public void setJobTaskID(int jobTaskID) {
		this.jobTaskID = jobTaskID;
	}
	
	public boolean isTaskActive() {
		return taskActive;
	}
	
	public void setTaskActive(boolean taskActive) {
		this.taskActive = taskActive;
	}
	
	public String getJobTaskLabel() {
		return jobTaskLabel;
	}
	
	public void setJobTaskLabel(String jobTaskLabel) {
		this.jobTaskLabel = jobTaskLabel;
	}
	
	public String getJobTaskName() {
		return jobTaskName;
	}
	
	public void setJobTaskName(String jobTaskName) {
		this.jobTaskName = jobTaskName;
	}
	
	public String getTaskType() {
		return taskType;
	}
	
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public JobTask getNewTask() {
		return newTask;
	}

	public void setNewTask(JobTask newTask) {
		this.newTask = newTask;
	}

	public List<JobTask> getTasks() {
		return jobTaskDAO.findOperatorTasks(operator.getId());
	}
}
