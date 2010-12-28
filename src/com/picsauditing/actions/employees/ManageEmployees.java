package com.picsauditing.actions.employees;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.actions.Indexer;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.BaseHistory;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeQualification;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.JobContractor;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageEmployees extends AccountActionSupport implements Preparable {

	private AccountDAO accountDAO;
	private EmployeeDAO employeeDAO;
	private JobRoleDAO roleDAO;
	private EmployeeRoleDAO employeeRoleDAO;
	private JobSiteDAO jobSiteDAO;
	private EmployeeSiteDAO employeeSiteDAO;
	private OperatorAccountDAO operatorAccountDAO;
	private JobSiteTaskDAO siteTaskDAO;
	private ContractorAccountDAO conDAO;
	private Indexer indexer;

	protected Employee employee;
	protected String ssn;

	private String effective;
	private String expiration;
	private String orientation;
	private int monthsToExp;

	protected int childID;
	protected Set<JobRole> unusedJobRoles;
	protected List<OperatorSite> oqOperators;
	protected List<OperatorSite> hseOperators;

	// Add site
	private String siteLabel;
	private String siteName;
	private Date siteStart;
	private Date siteStop;
	private int opID;
	private int jobID;

	public ManageEmployees(AccountDAO accountDAO, EmployeeDAO employeeDAO, JobRoleDAO roleDAO,
			EmployeeRoleDAO employeeRoleDAO, EmployeeSiteDAO employeeSiteDAO, OperatorAccountDAO operatorAccountDAO,
			JobSiteDAO jobSiteDAO, JobSiteTaskDAO siteTaskDAO, ContractorAccountDAO conDAO, Indexer indexer) {
		this.accountDAO = accountDAO;
		this.employeeDAO = employeeDAO;
		this.roleDAO = roleDAO;
		this.employeeRoleDAO = employeeRoleDAO;
		this.jobSiteDAO = jobSiteDAO;
		this.employeeSiteDAO = employeeSiteDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.siteTaskDAO = siteTaskDAO;
		this.conDAO = conDAO;
		this.indexer = indexer;
	}

	@Override
	public void prepare() throws Exception {
		int employeeID = getParameter("employee.id");
		if (employeeID > 0) {
			employee = employeeDAO.find(employeeID);
		}

		if (employee != null) {
			account = employee.getAccount();
		} else {
			int accountID = getParameter("id");
			if (accountID > 0)
				account = accountDAO.find(accountID);
		}
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else
			permissions.tryPermission(OpPerms.ManageEmployees);

		if (employee == null && account == null) {
			account = accountDAO.find(permissions.getAccountId());
		}

		if (account == null)
			throw new RecordNotFoundException("Account " + id + " not found");

		if (permissions.getAccountId() != account.getId())
			permissions.tryPermission(OpPerms.AllOperators);

		this.subHeading = account.getName();

		if ("Add".equals(button))
			employee = new Employee();

		if ("Save".equals(button)) {
			if (employee.getAccount() == null) {
				employee.setAccount(account);
			}

			if (ssn != null) {
				if (ssn.length() == 9)
					employee.setSsn(ssn);
				else if (!ssn.matches("X{5}\\d{4}"))
					addActionError("Invalid social security number entered.");
			}

			employee.setAuditColumns(permissions);

			// employee.setNeedsIndexing(true);
			employeeDAO.save(employee);
			indexer.runSingle(employee, "employee");

			redirect("ManageEmployees.action?employee.id=" + employee.getId());
		}

		if ("Delete".equals(button)) {
			employeeDAO.remove(employee);
			addActionMessage("Employee " + employee.getDisplayName() + " Successfully Deleted.");
			File f = new File(getFtpDir() + "/files/" + FileUtils.thousandize(employee.getId()) + "emp_"
					+ employee.getId() + ".jpg");
			if (f != null) {
				f.delete();
			}
			employee = null;
		}

		if ("addRole".equals(button)) {
			JobRole jobRole = roleDAO.find(childID);

			if (employee != null && jobRole != null) {
				EmployeeRole e = new EmployeeRole();
				e.setEmployee(employee);
				e.setJobRole(jobRole);
				e.setAuditColumns(permissions);

				if (!employee.getEmployeeRoles().contains(e)) {
					employee.getEmployeeRoles().add(e);
					employeeRoleDAO.save(e);
				} else
					addActionError("Employee already has " + jobRole.getName() + " as a Job Role");
			}
		}

		if ("removeRole".equals(button)) {
			if (employee != null && childID > 0) {
				EmployeeRole e = employeeRoleDAO.find(childID);

				if (e != null) {
					employee.getEmployeeRoles().remove(e);
					employeeRoleDAO.remove(e);
				}
			}
		}

		if ("addSite".equals(button)) {
			if (employee != null && childID != 0) {
				EmployeeSite es = new EmployeeSite();
				es.setEmployee(employee);

				if (childID > 0) {
					es.setOperator(operatorAccountDAO.find(childID));
				} else {
					es.setJobSite(jobSiteDAO.find(-1 * childID));
					es.setOperator(es.getJobSite().getOperator());
				}
				es.setAuditColumns(permissions);
				es.defaultDates();
				employeeSiteDAO.save(es);
			}

			return "sites";
		}

		if ("removeSite".equals(button)) {
			if (employee != null && childID > 0) {
				EmployeeSite es = employeeSiteDAO.find(childID);

				if (es != null) {
					if (es.getEffectiveDate() != null && es.getEffectiveDate().before(EmployeeSite.getMidnightToday())) {
						es.expire();
						employeeSiteDAO.save(es);
					} else {
						employee.getEmployeeSites().remove(es);
						employeeSiteDAO.remove(es);
					}
				}
			}

			return "sites";
		}
		if ("editSite".equals(button)) {
			if (employee != null && childID != 0) {
				EmployeeSite es = employeeSiteDAO.find(childID);
				Date effDate = DateBean.parseDate(effective);
				if (effDate == null) {
					addActionMessage(effective + " is not a valid start date. Use the format 'MM/DD/YYYY'");
					return "sites";
				}
				Date expDate = DateBean.parseDate(expiration);
				if (expDate == null) {
					addActionError(expiration + " is not a valid end date. Use the format 'MM/DD/YYYY'");
					return "sites";
				}
				Date orDate = DateBean.parseDate(orientation);
				// if field is blank, there is no orientation date to update
				if (!orientation.equals("") && orDate == null) {
					addActionError(orientation + " is not a valid orientation date. Use the format 'MM/DD/YYYY'");
					return "sites";
				}
				es.setEffectiveDate(effDate);
				es.setExpirationDate(expDate);
				if (es.getOrientationDate() == null)
					monthsToExp = 0;
				es.setOrientationDate(orDate);
				if (orDate != null) {
					es.setMonthsToExp(monthsToExp);
					es.setOrientationExpiration();
				} else {
					es.setOrientationDate(null);
					es.setOrientationExpiration(null);
				}
				employeeSiteDAO.save(es);
			}

			return "sites";
		}

		if ("newSite".equals(button)) {
			if (!Strings.isEmpty(siteLabel) && !Strings.isEmpty(siteName) && employee != null && opID > 0) {
				OperatorAccount op = operatorAccountDAO.find(opID);

				JobSite site = new JobSite();
				site.setLabel(siteLabel);
				site.setName(siteName);
				site.setOperator(op);
				site.setProjectStart(siteStart);
				site.setProjectStop(siteStop);
				site = jobSiteDAO.save(site);

				EmployeeSite es = new EmployeeSite();
				es.setAuditColumns(permissions);
				es.setJobSite(site);
				es.setEmployee(employee);
				es.setOperator(op);
				es.setEffectiveDate(siteStart);
				es.setExpirationDate(BaseHistory.END_OF_TIME);
				employeeSiteDAO.save(es);
			}

			return "sites";
		}

		return SUCCESS;
	}

	public String getFileName(int eID) {
		return PICSFileType.emp + "_" + eID;
	}

	public void setMonthsToExp(int monthsToExp) {
		this.monthsToExp = monthsToExp;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setEffective(String effective) {
		this.effective = effective;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getSsn() {
		return Strings.maskSSN(employee.getSsn());
	}

	public void setSsn(String ssn) {
		ssn = ssn.replaceAll("[^X0-9]", "");
		if (ssn.length() <= 9)
			this.ssn = ssn;
	}

	public int getChildID() {
		return childID;
	}

	public void setChildID(int childID) {
		this.childID = childID;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteLabel() {
		return siteLabel;
	}

	public void setSiteLabel(String siteLabel) {
		this.siteLabel = siteLabel;
	}

	public Date getSiteStart() {
		if (siteStart == null)
			siteStart = new Date();

		return siteStart;
	}

	public void setSiteStart(Date siteStart) {
		this.siteStart = siteStart;
	}

	public Date getSiteStop() {
		if (siteStop == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, 3);
			siteStop = cal.getTime();
		}

		return siteStop;
	}

	public void setSiteStop(Date siteStop) {
		this.siteStop = siteStop;
	}

	public int getJobID() {
		return jobID;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public Set<JobRole> getUnusedJobRoles() {
		if (unusedJobRoles == null) {
			unusedJobRoles = new LinkedHashSet<JobRole>(account.getJobRoles());

			for (EmployeeRole employeeRole : employee.getEmployeeRoles()) {
				if (unusedJobRoles.contains(employeeRole.getJobRole()))
					unusedJobRoles.remove(employeeRole.getJobRole());
			}

			Iterator<JobRole> roleIter = unusedJobRoles.iterator();
			while (roleIter.hasNext())
				if (!roleIter.next().isActive())
					roleIter.remove();
		}

		return unusedJobRoles;
	}

	private void getOperators() {
		Set<OperatorSite> returnList = new HashSet<OperatorSite>();
		oqOperators = new ArrayList<OperatorSite>();
		hseOperators = new ArrayList<OperatorSite>();

		if (employee.getAccount() instanceof ContractorAccount) {
			// if contractor employee, return site list of non-corporate sites
			ContractorAccount contractor = (ContractorAccount) employee.getAccount();

			for (ContractorOperator co : contractor.getNonCorporateOperators()) {
				if (employee.getAccount().isRequiresCompetencyReview()
						&& co.getOperatorAccount().isRequiresCompetencyReview()) {
					fillSites(returnList, co.getOperatorAccount());
				}
			}

			for (JobContractor jc : contractor.getJobSites()) {
				returnList.add(new OperatorSite(jc.getJob()));
			}

		} else if (employee.getAccount() instanceof OperatorAccount) {
			// if operator employee return list of self, and if corporate, child
			// facilities
			OperatorAccount operator = (OperatorAccount) employee.getAccount();

			if (operator.isCorporate()) {
				fillSites(returnList, operator);
				for (Facility facility : operator.getCorporateFacilities())
					fillSites(returnList, facility.getOperator());
			} else {
				// just add self
				fillSites(returnList, operator);
			}
		}

		// trimming return list by used entries
		for (EmployeeSite used : employee.getEmployeeSites()) {
			if (used.isCurrent()) {
				Iterator<OperatorSite> iterator = returnList.iterator();
				
				while (iterator.hasNext()) {
					OperatorSite os = iterator.next();
					
					if ((os.getSite() != null && used.getJobSite() != null && used.getJobSite().equals(os.getSite()))
							|| (os.getSite() == null && used.getJobSite() == null && os.getOperator().equals(
									used.getOperator())))
						iterator.remove();
				}
			}
		}

		for (OperatorSite os : returnList) {
			if (os.getSite() != null)
				oqOperators.add(os);
			else
				hseOperators.add(os);
		}

		Collections.sort(oqOperators);
		Collections.sort(hseOperators);
	}

	public List<OperatorSite> getOqOperators() {
		if (oqOperators == null)
			getOperators();

		return oqOperators;
	}

	public List<OperatorSite> getHseOperators() {
		if (hseOperators == null)
			getOperators();

		return hseOperators;
	}

	private void fillSites(Set<OperatorSite> returnList, OperatorAccount operator) {
		if (operator.getJobSites().size() == 0) {
			boolean found = false;
			for (OperatorSite os : returnList) {
				if (os.getOperator().equals(operator) && os.getSite() == null)
					found = true;
			}

			if (!found)
				returnList.add(new OperatorSite(operator));
		} else {
			for (JobSite site : operator.getJobSites()) {
				if (!site.getProjectStop().before(new Date())) {
					boolean found = false;
					for (OperatorSite os : returnList) {
						if (os.getSite() != null && os.getSite().equals(site))
							found = true;
					}

					if (!found)
						returnList.add(new OperatorSite(site));
				}
			}
		}
	}

	public String getEmpPhoto() {
		return getFileName(employee.getId()) + employee.getPhoto();
	}

	public EmployeeMissingTasks getMissingTasks(int siteID) {
		List<JobSiteTask> jobSiteTasks = siteTaskDAO.findByJob(siteID);

		List<JobTask> all = new ArrayList<JobTask>();
		List<JobTask> qualified = new ArrayList<JobTask>();

		for (JobSiteTask jst : jobSiteTasks) {
			for (EmployeeQualification eq : employee.getEmployeeQualifications()) {
				if (jst.getTask().equals(eq.getTask()) && eq.isQualified() && eq.isCurrent())
					qualified.add(jst.getTask());
			}

			all.add(jst.getTask());
		}

		EmployeeMissingTasks missing = new EmployeeMissingTasks();
		missing.setTotalCount(all.size());
		all.removeAll(qualified);
		missing.setMissingTasks(all);
		missing.setQualifiedTasks(qualified);

		return missing;
	}

	/**
	 * Gets all job tasks across all associated operators (if you're a
	 * contractor). Returns the list of job tasks per project you have under
	 * your umbrella if you're an operator.
	 * 
	 * @return
	 */
	public List<JobTask> getAllJobTasks() {
		if (employee != null) {
			Set<JobTask> tasks = new HashSet<JobTask>();

			if (employee.getAccount().isContractor()) {
				ContractorAccount con = conDAO.find(employee.getAccount().getId());
				for (ContractorOperator co : con.getOperators()) {
					for (JobSite site : co.getOperatorAccount().getJobSites()) {
						for (JobSiteTask task : site.getTasks()) {
							tasks.add(task.getTask());
						}
					}
				}
			} else {
				OperatorAccount op = operatorAccountDAO.find(employee.getAccount().getId());
				for (JobSite site : op.getJobSites()) {
					for (JobSiteTask task : site.getTasks()) {
						tasks.add(task.getTask());
					}
				}
			}

			List<JobTask> allTasks = new ArrayList<JobTask>(tasks);
			Collections.sort(allTasks);
			return allTasks;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getPreviousLocationsJSON() {
		JSONArray a = new JSONArray();
		a.addAll(employeeDAO.findCommonLocations(account.getId()));
		return a;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getPreviousTitlesJSON() {
		JSONArray a = new JSONArray();
		a.addAll(employeeDAO.findCommonTitles());
		return a;
	}

	public class OperatorSite implements Comparable<OperatorSite> {

		private OperatorAccount operator;
		private JobSite site;

		public OperatorSite(OperatorAccount operator) {
			this.operator = operator;
		}

		public OperatorSite(JobSite site) {
			this.operator = site.getOperator();
			this.site = site;
		}

		public OperatorSite(EmployeeSite used) {
			this.operator = used.getOperator();
			if (used.getJobSite() != null)
				this.site = used.getJobSite();
		}

		public int getId() {
			if (site == null)
				return operator.getId();
			else
				return -1 * site.getId();
		}

		public String getName() {
			if (site == null)
				return operator.getName();
			else
				return operator.getName() + ": " + site.getLabel();
		}

		public OperatorAccount getOperator() {
			return operator;
		}

		public JobSite getSite() {
			return site;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			OperatorSite o = (OperatorSite) obj;
			if (!operator.equals(o.getOperator()))
				return false;
			if (site == null && o.getSite() == null)
				return true;
			if (site == null)
				return false;
			return site.equals(o.getSite());
		}

		@Override
		public int compareTo(OperatorSite o) {
			int opCompare = operator.compareTo(o.getOperator());
			if (opCompare != 0)
				return opCompare;
			if (site == null)
				return -1;

			return site.getName().compareTo(o.getSite().getName());
		}

	}

	public class EmployeeMissingTasks {
		private int totalCount;
		private List<JobTask> missingTasks = new ArrayList<JobTask>();
		private List<JobTask> qualifiedTasks = new ArrayList<JobTask>();

		public int getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}

		public List<JobTask> getMissingTasks() {
			return missingTasks;
		}

		public void setMissingTasks(List<JobTask> missingTasks) {
			this.missingTasks = missingTasks;
		}

		public List<JobTask> getQualifiedTasks() {
			return qualifiedTasks;
		}

		public void setQualifiedTasks(List<JobTask> qualifiedTasks) {
			this.qualifiedTasks = qualifiedTasks;
		}
	}
}
