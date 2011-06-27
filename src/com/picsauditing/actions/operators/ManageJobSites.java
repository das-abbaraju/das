package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.JobContractor;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageJobSites extends OperatorActionSupport implements Preparable {
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected EmployeeSiteDAO employeeSiteDAO;
	@Autowired
	protected JobSiteDAO siteDAO;
	@Autowired
	protected JobSiteTaskDAO siteTaskDAO;
	@Autowired
	protected JobTaskDAO taskDAO;

	protected int siteID;
	protected int siteTaskID;
	protected int taskID;
	protected int controlSpan;
	protected int conID;
	protected String siteName;
	protected String siteLabel;
	protected String siteCity;
	protected String noteSummary;

	protected State state;
	protected Country siteCountry;
	protected Date siteStart;
	protected Date siteEnd;
	protected Date date = new Date();
	protected JobSite newSite = new JobSite();
	protected JobSiteTask siteTask = new JobSiteTask();
	protected JobTask newTask = new JobTask();

	protected List<String> history;
	protected List<JobTask> addable = new ArrayList<JobTask>();
	protected List<JobSite> activeSites;
	protected List<JobSite> inactiveSites;
	protected List<JobSite> futureSites;
	protected List<JobSiteTask> tasks;
	protected List<ContractorAccount> newContractors;
	protected Map<Account, List<Employee>> siteCompanies;
	private List<JobSite> allSites;

	public ManageJobSites(OperatorAccountDAO operatorDao) {
		super(operatorDao);

		subHeading = "Manage Projects";
		noteCategory = NoteCategory.OperatorQualification;
	}

	@Override
	public void prepare() throws Exception {
		findOperator();
		allSites = siteDAO.findByOperator(operator.getId());
		siteID = this.getParameter("siteID");

		if (siteID > 0) {
			newSite = siteDAO.find(siteID);
			noteSummary = " project with label: " + newSite.getLabel() + " and site name: " + newSite.getName();
		}
	}

	@RequiredPermission(value = OpPerms.ManageProjects)
	public String execute() throws Exception {
		if ("Reactivate".equals(button)) {
			if (siteID > 0) {
				newSite.setProjectStart(new Date());

				Calendar cal = Calendar.getInstance();
				cal.setTime(newSite.getProjectStart());
				cal.add(Calendar.YEAR, 3);
				newSite.setProjectStop(cal.getTime());

				addNote(operator, "Reactivated");
			} else {
				addActionError("Missing project");
			}
			
			return getRedirect();
		}

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Edit)
	public String save() throws Exception {
		// Labels are required
		if (!Strings.isEmpty(siteLabel) && !Strings.isEmpty(siteName)) {
			// Operators are required, but if one isn't set,
			// this operator should be added by default
			if (newSite.getOperator() == null && operator != null)
				newSite.setOperator(operator);

			newSite.setLabel(siteLabel);
			newSite.setName(siteName);
			newSite.setProjectStart(siteStart);
			newSite.setProjectStop(siteEnd);

			if (!Strings.isEmpty(siteCity))
				newSite.setCity(siteCity);
			if (siteCountry != null && !Strings.isEmpty(siteCountry.getIsoCode()))
				newSite.setCountry(null);
			if (state != null && !Strings.isEmpty(state.getIsoCode()))
				newSite.setState(null);

			siteDAO.save(newSite);
			addNote(operator, "Added new" + noteSummary);
		} else {
			addActionError("Please add both label and name to this project.");
		}

		return getRedirect();
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Edit)
	public String update() throws Exception {
		if (siteID > 0 && !Strings.isEmpty(siteLabel) && !Strings.isEmpty(siteName)) {
			newSite.setLabel(siteLabel);
			newSite.setName(siteName);
			newSite.setProjectStart(siteStart);
			newSite.setProjectStop(siteEnd);

			if (!Strings.isEmpty(siteCity))
				newSite.setCity(siteCity);
			if (!siteCountry.getIsoCode().equals("")) {
				newSite.setCountry(siteCountry);

				if (siteCountry.getIsoCode().equals("US") || siteCountry.getIsoCode().equals("CA"))
					newSite.setState(state);
			}

			siteDAO.save(newSite);
			addNote(operator, "Updated" + noteSummary);
		} else {
			addActionError("Please add both label and name to this project.");
		}

		return getRedirect();
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Edit)
	public String remove() throws Exception {
		if (siteID == 0) {
			addActionError("Missing project");
		} else {
			addNote(operator, "Expired" + noteSummary);
			newSite.setProjectStop(new Date());
			siteDAO.save(newSite);
		}

		return getRedirect();
	}

	public String getTasks() throws Exception {
		if (siteID > 0) {
		} else {
			addActionError("Missing project");
		}

		return "getTasks";
	}

	public String editSite() throws Exception {
		if (newSite == null)
			addActionError("Missing project");

		return "editSite";
	}

	public String newTasks() throws Exception {
		if (newSite == null)
			addActionError("Missing project");

		return "newTasks";
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Edit)
	public String addTask() throws Exception {
		if (siteID > 0 && taskID > 0) {
			newTask = taskDAO.find(taskID);
			siteTask.setTask(newTask);
			siteTask.setJob(newSite);
			siteTask.setControlSpan(controlSpan);
			siteTask.setAuditColumns(permissions);
			siteTask.setEffectiveDate(new Date());
			siteTask.setExpirationDate(DateBean.getEndOfTime());
			siteTaskDAO.save(siteTask);

			addNote(operator, "Added new task: " + siteTask.getTask().getLabel() + " to project: " + newSite.getLabel());
		} else {
			addActionError("Missing either project or new task");
		}

		return "getTasks";
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Edit)
	public String removeTask() throws Exception {
		if (siteID > 0 && siteTaskID > 0) {
			siteTask = siteTaskDAO.find(siteTaskID);
			siteTaskDAO.remove(siteTask);

			addNote(operator, "Removed task: " + siteTask.getTask().getLabel() + " from project: " + newSite.getLabel());
		} else {
			addActionError("Missing either project or project task");
		}

		return "getTasks";
	}

	public String addCompany() throws Exception {
		if (conID > 0 && siteID > 0) {
			JobContractor jc = new JobContractor();
			jc.setContractor(contractorAccountDAO.find(conID));
			jc.setJob(siteDAO.find(siteID));
			siteDAO.save(jc);

			addNote(operator, String.format("Added contractor '%s' to job site: %s", jc.getContractor().getName(), jc
					.getJob().getLabel()));
		} else {
			addActionError("Missing contractor and job site");
		}

		return getTasks();
	}

	public boolean isCanEdit() {
		if ((date == null || maskDateFormat(date).equals(maskDateFormat(new Date())))
				&& permissions.hasPermission(OpPerms.ManageProjects, OpType.Edit))
			return true;

		return false;
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

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
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

	public void setSiteName(String siteName) {
		this.siteName = siteName;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDate(String date) {
		this.date = parseDate(date);
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
		if (activeSites == null) {
			activeSites = new ArrayList<JobSite>();
			for (JobSite site : allSites) {
				if (site.isActive(date))
					activeSites.add(site);
			}
		}

		return activeSites;
	}

	public List<JobSite> getInactiveSites() {
		if (inactiveSites == null) {
			inactiveSites = new ArrayList<JobSite>();
			for (JobSite site : allSites) {
				if (!site.isActive(date)) {
					if (site.getProjectStart() != null && site.getProjectStart().before(date))
						inactiveSites.add(site);
					else if (site.getProjectStart() == null && site.getProjectStop() != null
							&& site.getProjectStop().equals(date))
						inactiveSites.add(site);
				}
			}
		}

		return inactiveSites;
	}

	public List<JobSite> getFutureSites() {
		if (futureSites == null) {
			futureSites = new ArrayList<JobSite>();
			for (JobSite site : allSites) {
				if (site.getProjectStart() != null && site.getProjectStart().after(date))
					futureSites.add(site);
			}
		}
		return futureSites;
	}

	public List<JobSiteTask> getTasks(int job) {
		if (tasks == null)
			tasks = siteTaskDAO.findByJob(job);

		return tasks;
	}

	public Map<Account, List<Employee>> getSiteCompanies() {
		if (siteCompanies == null) {
			siteCompanies = new TreeMap<Account, List<Employee>>();

			for (JobContractor jc : newSite.getContractors()) {
				siteCompanies.put(jc.getContractor(), new ArrayList<Employee>());
			}

			List<EmployeeSite> esites = employeeSiteDAO.findWhere("e.jobSite.operator.id = " + operator.getId()
					+ " AND e.jobSite.id = " + siteID);

			for (EmployeeSite es : esites) {
				if (es.isCurrent() && es.getJobSite().isActive(new Date())) {
					Account a = es.getEmployee().getAccount();
					if (siteCompanies.get(a) == null)
						siteCompanies.put(a, new ArrayList<Employee>());

					siteCompanies.get(a).add(es.getEmployee());
				}
			}
		}

		return siteCompanies;
	}

	public List<ContractorAccount> getNewContractors() {
		if (newContractors == null) {
			Set<Account> working = new HashSet<Account>();
			newContractors = new ArrayList<ContractorAccount>();

			if (newSite != null) {
				for (JobContractor jobContractor : newSite.getContractors()) {
					working.add(jobContractor.getContractor());
				}
			}

			working.addAll(getSiteCompanies().keySet());

			for (ContractorOperator co : operator.getContractorOperators()) {
				if (co.getContractorAccount().isRequiresOQ() && !working.contains(co.getContractorAccount()))
					newContractors.add(co.getContractorAccount());
			}
		}

		return newContractors;
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

			addable = taskDAO.findWhere("opID = " + operator.getId() + ids + " ORDER BY label");
		}

		return addable;
	}

	public List<String> getHistory() {
		if (history == null) {
			List<Date> dates = siteDAO.findHistory("opID = " + operator.getId() + " AND projectStart IS NOT NULL");
			history = new ArrayList<String>();

			if (!maskDateFormat(dates.get(0)).equals(maskDateFormat(new Date())))
				history.add(maskDateFormat(new Date()));

			for (Date d : dates)
				history.add(maskDateFormat(d));
		}

		if (history.size() > 1)
			return history;

		return null;
	}

	public String getCompanyLink(Account a) {
		if (a.isContractor() && permissions.hasPermission(OpPerms.ContractorDetails))
			return "<a href=\"ContractorView.action?id=" + a.getId() + "\">" + a.getName() + "</a>";
		if (a.isOperator()
				&& (permissions.hasPermission(OpPerms.ManageOperators) || permissions.getAccountId() == a.getId()))
			return "<a href=\"FacilitiesEdit.action?id=" + a.getId() + "\">" + a.getName() + "</a>";

		return a.getName();
	}

	private String getRedirect() throws Exception {
		if (permissions.isOperator())
			return redirect("ManageProjects.action");
		else
			return redirect("ManageProjects.action?id=" + operator.getId());
	}
}