package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.EmployeeQualificationDAO;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.EmployeeQualification;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageJobSites extends OperatorActionSupport {
	protected EmployeeQualificationDAO qualDAO;
	protected JobSiteDAO siteDAO;
	protected JobSiteTaskDAO siteTaskDAO;
	protected JobTaskDAO taskDAO;
	protected NoteDAO noteDAO;

	protected int siteID;
	protected int siteTaskID;
	protected int taskID;

	protected JobSite newSite = new JobSite();
	protected JobSiteTask siteTask = new JobSiteTask();
	protected JobTask newTask = new JobTask();

	public ManageJobSites(OperatorAccountDAO operatorDao, EmployeeQualificationDAO qualDAO, JobSiteDAO siteDAO,
			JobSiteTaskDAO siteTaskDAO, JobTaskDAO taskDAO, NoteDAO noteDAO) {
		super(operatorDao);
		this.qualDAO = qualDAO;
		this.siteDAO = siteDAO;
		this.siteTaskDAO = siteTaskDAO;
		this.taskDAO = taskDAO;
		this.noteDAO = noteDAO;

		subHeading = "Manage Job Sites";
		// When we need more detailed notes about OQ
		// noteCategory = NoteCategory.OperatorQualification;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findOperator();
		// Check for basic view capabilities
		tryPermissions(OpPerms.ManageJobSites);

		if (button != null) {
			if ("Tasks".equalsIgnoreCase(button)) {
				if (siteID > 0) {
					newSite = siteDAO.find(siteID);
					return SUCCESS;
				} else
					addActionError("Missing job site");
			}

			if ("Employees".equalsIgnoreCase(button)) {
				if (siteTaskID > 0) {
					siteTask = siteTaskDAO.find(siteTaskID);
					return "employees";
				} else
					addActionError("Missing job site task");
			}

			if ("NewTasks".equalsIgnoreCase(button)) {
				if (siteID > 0) {
					newSite = siteDAO.find(siteID);
					return "newTasks";
				} else
					addActionError("Missing job site");
			}

			if (getActionErrors().size() > 0)
				return SUCCESS;

			// Check if they have the edit permission here
			tryPermissions(OpPerms.ManageJobSites, OpType.Edit);
			// Add a note for every action?
			Note note = new Note();

			// Job Site Tasks
			if ("AddTask".equalsIgnoreCase(button) || "RemoveTask".equalsIgnoreCase(button)) {
				if ("AddTask".equalsIgnoreCase(button)) {
					if (siteID > 0 && taskID > 0) {
						newTask = taskDAO.find(taskID);
						newSite = siteDAO.find(siteID);
						siteTask.setTask(newTask);
						siteTask.setJob(newSite);
						siteTask.setAuditColumns(permissions);
						siteTaskDAO.save(siteTask);

						note.setSummary("Added new task: " + siteTask.getTask().getLabel() + " to job site: "
								+ newSite.getLabel());
					} else
						addActionError("Missing either job site or new task");
				}

				if ("RemoveTask".equalsIgnoreCase(button)) {
					if (siteID > 0 && siteTaskID > 0) {
						newSite = siteDAO.find(siteID);
						siteTask = siteTaskDAO.find(siteTaskID);
						siteTaskDAO.remove(siteTask);

						note.setSummary("Removed task: " + siteTask.getTask().getLabel() + " from job site: "
								+ newSite.getLabel());
					} else
						addActionError("Missing either job site or job site task");
				}

				if (getActionErrors().size() > 0)
					return SUCCESS;

				note.setAuditColumns(permissions);
				note.setAccount(operator);
				note.setNoteCategory(noteCategory);
				note.setPriority(LowMedHigh.Med);
				note.setViewableBy(operator);
				note.setCanContractorView(true);
				noteDAO.save(note);

				return SUCCESS;
			}

			if ("Save".equalsIgnoreCase(button)) {
				// Labels are required
				if (!Strings.isEmpty(newSite.getLabel())) {
					// Operators are required, but if one isn't set,
					// this operator should be added by default
					if (newSite.getOperator() == null && operator != null)
						newSite.setOperator(operator);

					newSite.setActive(true);
					note.setSummary("Added new job site with label: " + newSite.getLabel() + " and site name: "
							+ newSite.getName());
				} else
					addActionError("Please add a label to this job site.");
			}

			if ("Remove".equalsIgnoreCase(button)) {
				if (siteID > 0) {
					newSite = siteDAO.find(siteID);
					newSite.setActive(false);
					note.setSummary("Deactivated job site with label: " + newSite.getLabel() + " and site name: "
							+ newSite.getName());
				} else
					addActionError("Missing job site");
			}

			if ("Reactivate".equalsIgnoreCase(button)) {
				if (siteID > 0) {
					newSite = siteDAO.find(siteID);
					newSite.setActive(true);
					note.setSummary("Reactivated job site with label: " + newSite.getLabel() + " and site name: "
							+ newSite.getName());
				} else
					addActionError("Missing job site");
			}

			if (getActionErrors().size() > 0)
				return SUCCESS;

			newSite.setAuditColumns(permissions);
			siteDAO.save(newSite);
			// Save the note
			note.setAuditColumns(permissions);
			note.setAccount(operator);
			note.setNoteCategory(noteCategory);
			note.setPriority(LowMedHigh.Med);
			note.setViewableBy(operator);
			note.setCanContractorView(true);
			noteDAO.save(note);

			if (permissions.isOperator())
				return redirect("ManageJobSites.action");
			else
				return redirect("ManageJobSites.action?id=" + operator.getId());
		}

		return SUCCESS;
	}

	public int getSiteID() {
		return siteID;
	}

	public void setSiteID(int siteID) {
		this.siteID = siteID;
	}

	public int getSiteTaskID() {
		return siteTaskID;
	}

	public void setSiteTaskID(int siteTaskID) {
		this.siteTaskID = siteTaskID;
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public JobSite getNewSite() {
		return newSite;
	}

	public void setNewSite(JobSite newSite) {
		this.newSite = newSite;
	}

	public JobSiteTask getSiteTask() {
		return siteTask;
	}

	public boolean isCanEdit() {
		return permissions.hasPermission(OpPerms.ManageJobSites, OpType.Edit);
	}

	public List<JobSite> getActiveSites() {
		return siteDAO.findByOperatorWhere(operator.getId(), "active = 1");
	}

	public List<JobSite> getInactiveSites() {
		return siteDAO.findByOperatorWhere(operator.getId(), "active = 0");
	}

	public List<JobSiteTask> getTasks(int job) {
		return siteTaskDAO.findByJob(job);
	}

	public List<JobTask> getAddableTasks() {
		List<JobSiteTask> siteTasks = getTasks(siteID);
		// Skip tasks that have all ready been associated with this site
		List<Integer> skip = new ArrayList<Integer>();

		for (JobSiteTask jst : siteTasks) {
			skip.add(jst.getTask().getId());
		}

		String ids = "";
		if (skip.size() > 0)
			ids = " AND id NOT IN (" + Strings.implodeForDB(skip, ",") + ")";

		return taskDAO.findWhere("opID = " + operator.getId() + ids);
	}

	public List<EmployeeQualification> getEmployeesByTask(int siteTaskID) {
		// Sort list by employer name?
		List<EmployeeQualification> employees = qualDAO.findByTask(siteTaskID);

		if (employees.size() > 1)
			Collections.sort(employees, new Comparator<EmployeeQualification>() {
				@Override
				public int compare(EmployeeQualification o1, EmployeeQualification o2) {
					return 0;
				}
			});

		return employees;
	}
}
