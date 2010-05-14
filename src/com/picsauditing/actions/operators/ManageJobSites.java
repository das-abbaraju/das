package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageJobSites extends OperatorActionSupport {
	protected JobSiteDAO siteDAO;
	protected JobSiteTaskDAO siteTaskDAO;
	protected JobTaskDAO taskDAO;
	protected NoteDAO noteDAO;
	
	protected int siteID;
	protected int siteTaskID;
	protected int taskID;
	protected int controlSpan;
	
	protected String siteName;
	protected String siteLabel;
	protected String siteCity;
	protected State state = new State();
	protected Country siteCountry = new Country();
	protected Date siteStart;
	protected Date siteEnd;

	protected JobSite newSite = new JobSite();
	protected JobSiteTask siteTask = new JobSiteTask();
	protected JobTask newTask = new JobTask();
	protected List<JobTask> addable = new ArrayList<JobTask>();
	protected List<JobSite> activeSites;
	protected List<JobSite> inactiveSites;
	protected List<JobSiteTask> tasks;

	public ManageJobSites(OperatorAccountDAO operatorDao, JobSiteDAO siteDAO, JobSiteTaskDAO siteTaskDAO,
			JobTaskDAO taskDAO, NoteDAO noteDAO) {
		super(operatorDao);
		this.siteDAO = siteDAO;
		this.siteTaskDAO = siteTaskDAO;
		this.taskDAO = taskDAO;
		this.noteDAO = noteDAO;

		subHeading = "Manage Projects";
		noteCategory = NoteCategory.OperatorQualification;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findOperator();
		// Check for basic view capabilities
		tryPermissions(OpPerms.ManageProjects);
		
		if (siteID > 0)
			newSite = siteDAO.find(siteID);

		if (button != null) {
			if ("Tasks".equalsIgnoreCase(button)) {
				if (siteID > 0) {
					return SUCCESS;
				} else
					addActionError("Missing project");
			}

			if ("NewTasks".equalsIgnoreCase(button)) {
				if (siteID > 0) {
					return "newTasks";
				} else
					addActionError("Missing project");
			}
			
			if ("EditSite".equalsIgnoreCase(button)) {
				if (siteID > 0) {
					return "editSite";
				} else
					addActionError("Missing project");
			}

			if (getActionErrors().size() > 0)
				return SUCCESS;

			// Check if they have the edit permission here
			tryPermissions(OpPerms.ManageProjects, OpType.Edit);
			// Add a note for every action?
			Note note = new Note();

			// project Tasks
			if ("AddTask".equalsIgnoreCase(button) || "RemoveTask".equalsIgnoreCase(button)) {
				if ("AddTask".equalsIgnoreCase(button)) {
					if (siteID > 0 && taskID > 0) {
						newTask = taskDAO.find(taskID);
						siteTask.setTask(newTask);
						siteTask.setJob(newSite);
						siteTask.setControlSpan(controlSpan);
						siteTask.setAuditColumns(permissions);
						siteTaskDAO.save(siteTask);

						note.setSummary("Added new task: " + siteTask.getTask().getLabel() + " to project: "
								+ newSite.getLabel());
					} else
						addActionError("Missing either project or new task");
				}

				if ("RemoveTask".equalsIgnoreCase(button)) {
					if (siteID > 0 && siteTaskID > 0) {
						siteTask = siteTaskDAO.find(siteTaskID);
						siteTaskDAO.remove(siteTask);

						note.setSummary("Removed task: " + siteTask.getTask().getLabel() + " from project: "
								+ newSite.getLabel());
					} else
						addActionError("Missing either project or project task");
				}

				if (getActionErrors().size() > 0)
					return SUCCESS;

				note.setAuditColumns(permissions);
				note.setAccount(operator);
				note.setNoteCategory(noteCategory);
				note.setPriority(LowMedHigh.Med);
				note.setViewableBy(operator);
				note.setCanContractorView(true);
				// Until we're ready for notes
				//noteDAO.save(note);

				return SUCCESS;
			}
			
			String summary = " project with label: " + newSite.getLabel() + " and site name: " + newSite.getName();

			if ("Save".equalsIgnoreCase(button)) {
				// Labels are required
				if (!Strings.isEmpty(siteLabel) && !Strings.isEmpty(siteName)) {
					// Operators are required, but if one isn't set,
					// this operator should be added by default
					if (newSite.getOperator() == null && operator != null)
						newSite.setOperator(operator);

					newSite.setLabel(siteLabel);
					newSite.setName(siteName);
					newSite.setCity(siteCity);
					newSite.setProjectStart(siteStart);
					newSite.setProjectStop(siteEnd);
					newSite.setCountry(siteCountry);
					newSite.setState(state);
					
					newSite.setActive(true);
					
					summary = "Added new" + summary;
				} else
					addActionError("Please add both label and name to this project.");
			}
			
			if ("Update".equalsIgnoreCase(button)) {
				if (siteID > 0 && !Strings.isEmpty(siteLabel) && !Strings.isEmpty(siteName)) {
					newSite.setLabel(siteLabel);
					newSite.setName(siteName);
					newSite.setCity(siteCity);
					newSite.setProjectStart(siteStart);
					newSite.setProjectStop(siteEnd);
					newSite.setCountry(siteCountry);
					newSite.setState(state);
					
					summary = "Renamed" + summary + " to label: " + newSite.getLabel() + " and project name: "
							+ newSite.getName();
				} else
					addActionError("Please add both label and name to this project.");
			}

			if ("Remove".equalsIgnoreCase(button)) {
				if (siteID > 0) {
					newSite.setActive(false);
					newSite.setProjectStop(new Date());
					summary = "Deactivated" + summary;
				} else
					addActionError("Missing project");
			}

			if ("Reactivate".equalsIgnoreCase(button)) {
				if (siteID > 0) {
					newSite.setActive(true);
					newSite.setProjectStart(new Date());
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(newSite.getProjectStart());
					cal.add(Calendar.YEAR, 3);
					newSite.setProjectStop(cal.getTime());
					
					summary = "Reactivated";
				} else
					addActionError("Missing project");
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
			
			if (!summary.startsWith(" "))
				note.setSummary(summary);
			
			noteDAO.save(note);

			if (permissions.isOperator())
				return redirect("ManageProjects.action");
			else
				return redirect("ManageProjects.action?id=" + operator.getId());
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
	
	public int getControlSpan() {
		return controlSpan;
	}
	
	public void setControlSpan(int controlSpan) {
		this.controlSpan = controlSpan;
	}
	
	public String getSiteLabel() {
		return siteLabel;
	}
	
	public void setSiteLabel(String siteLabel) {
		this.siteLabel = siteLabel;
	}
	
	public String getSiteName() {
		return siteName;
	}
	
	public String getSiteCity() {
		return siteCity;
	}

	public void setSiteCity(String siteCity) {
		this.siteCity = siteCity;
	}

	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}

	public Country getSiteCountry() {
		return siteCountry;
	}

	public void setSiteCountry(Country siteCountry) {
		this.siteCountry = siteCountry;
	}

	public Date getSiteStart() {
		return siteStart;
	}

	public void setSiteStart(Date siteStart) {
		this.siteStart = siteStart;
	}

	public Date getSiteEnd() {
		return siteEnd;
	}

	public void setSiteEnd(Date siteEnd) {
		this.siteEnd = siteEnd;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
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

	public List<JobSite> getActiveSites() {
		if (activeSites == null)
			activeSites = siteDAO.findByOperatorWhere(operator.getId(), "active = 1");
		
		return activeSites;
	}

	public List<JobSite> getInactiveSites() {
		if (inactiveSites == null)
			inactiveSites = siteDAO.findByOperatorWhere(operator.getId(), "active = 0");
		
		return inactiveSites;
	}

	public List<JobSiteTask> getTasks(int job) {
		if (tasks == null)
			tasks = siteTaskDAO.findByJob(job);
		
		return tasks;
	}

	public List<JobTask> getAddableTasks() {
		if (addable.size() == 0) {
			List<JobSiteTask> siteTasks = getTasks(siteID);
			// Skip tasks that have all ready been associated with this site
			List<Integer> skip = new ArrayList<Integer>();
	
			for (JobSiteTask jst : siteTasks) {
				skip.add(jst.getTask().getId());
			}
	
			String ids = "";
			if (skip.size() > 0)
				ids = " AND id NOT IN (" + Strings.implodeForDB(skip, ",") + ")";
			
			addable = taskDAO.findWhere("opID = " + operator.getId() + ids);
		}
		
		return addable;
	}
}
