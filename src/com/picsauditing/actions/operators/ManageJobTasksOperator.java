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
		tryPermissions(OpPerms.ManageJobSites);

		if (button != null) {
			if ("Tasks".equalsIgnoreCase(button)) {
				newTask = jobTaskDAO.find(jobTaskID);
				return SUCCESS;
			}

			if ("Save".equalsIgnoreCase(button)) {
				// Labels are required
				if (Strings.isEmpty(newTask.getLabel()))
					addActionError("Please add a label to this job site.");
				// Operators are required, but if one isn't set,
				// this operator should be added by default
				if (newTask.getOperator() == null && operator != null)
					newTask.setOperator(operator);

				if (getActionErrors().size() > 0)
					return SUCCESS;
			}

			if ("Remove".equalsIgnoreCase(button)) {
				newTask = jobTaskDAO.find(jobTaskID);
				jobTaskDAO.remove(newTask);
			} else {
				jobTaskDAO.save(newTask);
			}

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

	public JobTask getNewTask() {
		return newTask;
	}

	public void setNewTask(JobTask newTask) {
		this.newTask = newTask;
	}

	public boolean isCanEdit() {
		return permissions.hasPermission(OpPerms.ManageJobSites, OpType.Edit);
	}

	public List<JobTask> getTasks() {
		return jobTaskDAO.findOperatorTasks(operator.getId());
	}
}
