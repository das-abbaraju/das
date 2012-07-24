package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
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
public class ManageJobSites extends OperatorActionSupport {
	@Autowired
	protected EmployeeSiteDAO employeeSiteDAO;
	@Autowired
	protected JobSiteDAO jobSiteDAO;
	@Autowired
	protected JobSiteTaskDAO jobSiteTaskDAO;
	@Autowired
	protected JobTaskDAO jobTaskDAO;
	@Autowired
	protected CountrySubdivisionDAO countrySubdivisionDAO;

	protected ContractorAccount contractor;
	protected JobSite jobSite;
	protected JobTask jobTask;
	protected JobSiteTask jobSiteTask;
	protected String siteLabel;
	protected String siteName;
	protected String siteCity;
	protected Country siteCountry;
	protected State siteState;
	protected Date siteStart;
	protected Date siteEnd;
	protected Date date = new Date();
	protected int controlSpan;

	protected String noteSummary = "%s project with label: %s and name: %s";

	protected List<JobTask> addable;
	protected List<JobSite> activeSites;
	protected List<JobSite> inactiveSites;
	protected List<JobSite> futureSites;
	protected List<JobSiteTask> tasks;
	protected List<ContractorAccount> newContractors;
	protected Map<Account, List<Employee>> siteCompanies;

	public ManageJobSites() {
		noteCategory = NoteCategory.OperatorQualification;
	}

	@RequiredPermission(value = OpPerms.ManageProjects)
	public String execute() throws Exception {
		if (operator == null && permissions.isOperatorCorporate()) {
			operator = operatorDao.find(permissions.getAccountId());
		}

		subHeading = getText("ManageProjects.title");

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Edit)
	public String save() throws Exception {
		jobSite = new JobSite();
		// Labels are required
		if (!Strings.isEmpty(siteLabel) && !Strings.isEmpty(siteName)) {
			// Operators are required, but if one isn't set,
			// this operator should be added by default
			if (jobSite.getOperator() == null && operator != null)
				jobSite.setOperator(operator);

			jobSite.setLabel(siteLabel);
			jobSite.setName(siteName);
			jobSite.setProjectStart(siteStart);
			jobSite.setProjectStop(siteEnd);
			jobSite.setCity(siteCity);
			jobSite.setCountry(siteCountry);
			jobSite.setState(siteState);
			if (isCountrySubdivision()) {
				jobSite.setCountrySubdivision(getCountrySubdivision());
			}
			jobSiteDAO.save(jobSite);
			addNote(operator, String.format(noteSummary, "Added new", jobSite.getLabel(), jobSite.getName()));
		} else {
			addActionError("Please add both label and name to this project.");
		}

		return getRedirect();
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Edit)
	public String update() throws Exception {
		if (jobSite != null && !Strings.isEmpty(siteLabel) && !Strings.isEmpty(siteName)) {
			jobSite.setLabel(siteLabel);
			jobSite.setName(siteName);
			jobSite.setProjectStart(siteStart);
			jobSite.setProjectStop(siteEnd);
			jobSite.setCity(siteCity);

			if (!Strings.isEmpty(siteCountry.getIsoCode()))
				jobSite.setCountry(siteCountry);

			jobSite.setState(siteState);
			if (isCountrySubdivision()) {
				jobSite.setCountrySubdivision(getCountrySubdivision());
			}
			jobSiteDAO.save(jobSite);
			addNote(operator, String.format(noteSummary, "Updated", jobSite.getLabel(), jobSite.getName()));
		} else {
			addActionError("Please add both label and name to this project.");
		}

		return getRedirect();
	}

	private boolean isCountrySubdivision() {
		return countrySubdivisionDAO.exist(siteCountry.getIsoCode() + "-" + siteState.getIsoCode());
	}

	private CountrySubdivision getCountrySubdivision() {
		CountrySubdivision countrySubdivision = new CountrySubdivision();
		String stateIso = siteState.getIsoCode();
		String countryIso = siteCountry.getIsoCode();
		// TODO: Remove in Clean up Phase
		stateIso = StringUtils.remove(stateIso, "GB_");
		countrySubdivision.setIsoCode(countryIso + "-" + stateIso);
		return countrySubdivision;
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Edit)
	public String reactivate() throws Exception {
		if (jobSite != null) {
			jobSite.setProjectStart(new Date());

			Calendar cal = Calendar.getInstance();
			cal.setTime(jobSite.getProjectStart());
			cal.add(Calendar.YEAR, 3);
			jobSite.setProjectStop(cal.getTime());

			addNote(operator, String.format(noteSummary, "Reactivated", jobSite.getLabel(), jobSite.getName()));
		} else {
			addActionError("Missing project");
		}

		return getRedirect();
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Delete)
	public String remove() throws Exception {
		if (jobSite == null) {
			addActionError("Missing project");
		} else {
			addNote(operator, "Expired" + noteSummary);
			jobSite.setProjectStop(new Date());
			jobSiteDAO.save(jobSite);
		}

		return getRedirect();
	}

	public String getTasks() throws Exception {
		if (jobSite == null)
			addActionError("Missing project");

		return "getTasks";
	}

	public String editSite() throws Exception {
		if (jobSite == null)
			addActionError("Missing project");

		return "editSite";
	}

	public String newTasks() throws Exception {
		if (jobSite == null)
			addActionError("Missing project");

		return "newTasks";
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Edit)
	public String addTask() throws Exception {
		if (jobSite != null && jobTask != null) {
			JobSiteTask jobSiteTask = new JobSiteTask();
			jobSiteTask.setJob(jobSite);
			jobSiteTask.setTask(jobTask);
			jobSiteTask.setControlSpan(controlSpan);
			jobSiteTask.setAuditColumns(permissions);
			jobSiteTask.setEffectiveDate(new Date());
			jobSiteTask.setExpirationDate(DateBean.getEndOfTime());
			jobSiteTaskDAO.save(jobSiteTask);

			addNote(operator,
					String.format("Added new task: %s to project: %s", jobSiteTask.getTask().getLabel(),
							jobSite.getLabel()));
		} else {
			addActionError("Missing either project or new task");
		}

		return "getTasks";
	}

	@RequiredPermission(value = OpPerms.ManageProjects, type = OpType.Edit)
	public String removeTask() throws Exception {
		if (jobSite != null && jobSiteTask != null) {
			jobSiteTaskDAO.remove(jobSiteTask);

			addNote(operator,
					String.format("Removed task: %s from project: %s", jobSiteTask.getTask().getLabel(),
							jobSite.getLabel()));
		} else {
			addActionError("Missing either project or project task");
		}

		return "getTasks";
	}

	public String addCompany() throws Exception {
		if (contractor != null && jobSite != null) {
			JobContractor jc = new JobContractor();
			jc.setContractor(contractor);
			jc.setJob(jobSite);
			jobSiteDAO.save(jc);

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

	public List<JobSite> getActiveSites() {
		if (activeSites == null) {
			activeSites = new ArrayList<JobSite>();
			for (JobSite site : operator.getJobSites()) {
				if (site.isActive(date))
					activeSites.add(site);
			}
		}

		return activeSites;
	}

	public List<JobSite> getInactiveSites() {
		if (inactiveSites == null) {
			inactiveSites = new ArrayList<JobSite>();
			for (JobSite site : operator.getJobSites()) {
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
			for (JobSite site : operator.getJobSites()) {
				if (site.getProjectStart() != null && site.getProjectStart().after(date))
					futureSites.add(site);
			}
		}
		return futureSites;
	}

	public Map<Account, List<Employee>> getSiteCompanies() {
		if (siteCompanies == null) {
			siteCompanies = new TreeMap<Account, List<Employee>>();

			for (JobContractor jc : jobSite.getContractors()) {
				siteCompanies.put(jc.getContractor(), new ArrayList<Employee>());
			}

			List<EmployeeSite> esites = employeeSiteDAO.findWhere("e.jobSite.operator.id = " + operator.getId()
					+ " AND e.jobSite.id = " + jobSite.getId());

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

			if (jobSite != null) {
				for (JobContractor jobContractor : jobSite.getContractors())
					working.add(jobContractor.getContractor());
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
		if (addable == null) {
			addable = new ArrayList<JobTask>();
			// Skip tasks that have all ready been associated with this site
			List<Integer> skip = new ArrayList<Integer>();

			for (JobSiteTask jst : jobSite.getTasks()) {
				skip.add(jst.getTask().getId());
			}

			String ids = "";
			if (skip.size() > 0)
				ids = String.format(" AND id NOT IN (%s)", Strings.implodeForDB(skip, ","));

			addable = jobTaskDAO.findWhere(String.format("opID = %d %s ORDER BY label", operator.getId(), ids));
		}

		return addable;
	}

	public boolean isLinkable(Account a) {
		if (permissions.isOperatorCorporate()) {
			if (operator == null)
				operator = operatorDao.find(permissions.getAccountId());

			for (ContractorOperator co : operator.getContractorOperators()) {
				if (co.getContractorAccount().getId() == a.getId())
					return true;
			}
		}

		if (permissions.isAdmin() && a.isContractor() && permissions.hasPermission(OpPerms.ContractorDetails))
			return true;

		return false;
	}

	private String getRedirect() throws Exception {
		if (permissions.isOperator())
			return setUrlForRedirect("ManageProjects.action");
		else
			return setUrlForRedirect("ManageProjects.action?id=" + operator.getId());
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public JobSite getJobSite() {
		return jobSite;
	}

	public void setJobSite(JobSite jobSite) {
		this.jobSite = jobSite;
	}

	public JobTask getJobTask() {
		return jobTask;
	}

	public void setJobTask(JobTask jobTask) {
		this.jobTask = jobTask;
	}

	public JobSiteTask getJobSiteTask() {
		return jobSiteTask;
	}

	public void setJobSiteTask(JobSiteTask jobSiteTask) {
		this.jobSiteTask = jobSiteTask;
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

	public Country getSiteCountry() {
		return siteCountry;
	}

	public void setSiteCountry(Country siteCountry) {
		this.siteCountry = siteCountry;
	}

	public State getSiteState() {
		return siteState;
	}

	public void setSiteState(State siteState) {
		this.siteState = siteState;
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

	public int getControlSpan() {
		return controlSpan;
	}

	public void setControlSpan(int controlSpan) {
		this.controlSpan = controlSpan;
	}
}