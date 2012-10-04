package com.picsauditing.actions.employees;

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
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
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
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserStatus;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageEmployees extends AccountActionSupport implements Preparable {
	@Autowired
	protected EmployeeDAO employeeDAO;
	@Autowired
	protected JobSiteTaskDAO siteTaskDAO;

	protected Employee employee;
	protected String ssn;

	protected ContractorAudit audit;
	protected int[] initialSites;

	protected Set<JobRole> unusedJobRoles;
	protected List<OperatorSite> oqOperators;
	protected List<OperatorSite> hseOperators;
	protected List<OperatorAccount> nonEmployeeGUARDOperators;
	protected List<OperatorAccount> unusedNonEmployeeGUARDOperators;
	protected List<AssessmentResult> nccerResults;

	private List<Employee> activeEmployees;

	public ManageEmployees() {
		noteCategory = NoteCategory.Employee;
	}

	@Override
	public void prepare() throws Exception {
		checkPermissions();
	}

	@Before
	public void findAccount() {
		if (account == null && employee != null) {
			account = employee.getAccount();
		}

		if (account == null && getParameter("id") > 0) {
			id = getParameter("id");
			account = accountDAO.find(id);
		}

		if (account == null) {
			account = accountDAO.find(permissions.getAccountId());
		}

		if (account != null) {
			loadActiveEmployees();
		}
	}

	@Override
	public String execute() {
		subHeading = getText("ManageEmployees.title");

		if (audit != null) {
			account = audit.getContractorAccount();
		}

		loadActiveEmployees();

		if (employee != null) {
			account = employee.getAccount();
			// TODO Put this into the employee cron
			for (EmployeeSite es : employee.getEmployeeSites()) {
				Date exp1 = es.getExpirationDate();
				Date exp2 = null;

				if (es.getJobSite() != null)
					exp2 = es.getJobSite().getProjectStop();

				Date exp = null;
				if (exp1 != null && exp2 != null)
					exp = exp1.before(exp2) ? exp1 : exp2;
				else if (exp1 != null || exp2 != null)
					exp = exp1 == null ? exp2 : exp1;

				if (exp != null && exp.before(new Date()) && es.isCurrent()) {
					es.expire();
					es.setAuditColumns(permissions);
					dao.save(es);
				}
			}
		}

		return SUCCESS;
	}

	public String add() {
		employee = new Employee();
		return SUCCESS;
	}

	public String save() throws Exception {
		if (employee.getAccount() == null) {
			employee.setAccount(account);
		}

		checkSsn();

		if (employee.getEmail().length() > 0) {
			employee.setEmail(EmailAddressUtils.validate(employee.getEmail()));
		}

		employee.setAuditColumns(permissions);
		employee = (Employee) employeeDAO.save(employee);

		if (employee.getId() == 0) {
			addNote("Added employee " + employee.getDisplayName(), LowMedHigh.Med);
		}

		addInitialSites();

		return setUrlForRedirect("ManageEmployees.action?"
				+ (audit != null ? "audit=" + audit.getId() + "&questionId=" + questionId : "account="
						+ account.getId()) + "#employee=" + employee.getId());
	}

	public String inactivate() {
		if (employee != null) {
			employee.setStatus(UserStatus.Inactive);
			employeeDAO.save(employee);
			addActionMessage("Employee " + employee.getDisplayName() + " Successfully deactivated.");
		}

		loadActiveEmployees();

		return SUCCESS;
	}

	public String activate() throws Exception {
		if (employee != null) {
			employee.setStatus(UserStatus.Active);
			employeeDAO.save(employee);
			return this.setUrlForRedirect("ManageEmployees.action?id=" + employee.getAccount().getId() + "#employee="
					+ employee.getId());
		}

		return setUrlForRedirect("ManageEmployees.action?id=" + account.getId());
	}

	public String delete() {
		if (employee != null) {
			employee.setStatus(UserStatus.Deleted);
			employeeDAO.save(employee);
			addActionMessage("Employee " + employee.getDisplayName() + " Successfully deleted.");
			employee = null;
		}

		loadActiveEmployees();

		return SUCCESS;
	}

	public List<Employee> getActiveEmployees() {
		return activeEmployees;

	}

	public void setActiveEmployees(List<Employee> activeEmployees) {
		this.activeEmployees = activeEmployees;
	}

	public String load() {
		return "edit";
	}

	public String getFileName(int eID) {
		return PICSFileType.emp + "_" + eID;
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

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	public int[] getInitialSites() {
		return initialSites;
	}

	public void setInitialSites(int[] initialSites) {
		this.initialSites = initialSites;
	}

	public Date getToday() {
		return new Date();
	}

	public Date getExpirationDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 3);
		return cal.getTime();
	}

	public List<OperatorSite> getOqOperators() {
		if (oqOperators == null)
			loadOperators();

		return oqOperators;
	}

	public List<OperatorAccount> getAllOqOperators() {
		List<OperatorAccount> allOqOperators = new ArrayList<OperatorAccount>();

		for (ContractorOperator co : ((ContractorAccount) account).getOperators()) {
			if (co.getOperatorAccount().isRequiresOQ()) {
				allOqOperators.add(co.getOperatorAccount());
			}
		}

		return allOqOperators;
	}

	public List<OperatorSite> getHseOperators() {
		if (hseOperators == null) {
			loadOperators();
		}

		return hseOperators;
	}

	public List<OperatorAccount> getNonEmployeeGUARDOperators() {
		if (nonEmployeeGUARDOperators == null) {
			loadOperators();
		}

		return nonEmployeeGUARDOperators;
	}

	public List<OperatorAccount> getUnusedNonEmployeeGUARDOperators() {
		if (unusedNonEmployeeGUARDOperators == null) {
			loadOperators();
		}

		return unusedNonEmployeeGUARDOperators;
	}

	public boolean isShowJobRolesSection() {
		boolean hasUnusedJobRoles = getUnusedJobRoles().size() > 0;
		boolean hasEmployeeRoles = false;

		if (employee != null) {
			hasEmployeeRoles = employee.getEmployeeRoles().size() > 0;
		}

		return hasUnusedJobRoles || hasEmployeeRoles;
	}

	public Set<JobRole> getUnusedJobRoles() {
		findAccount();

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
			List<JobSiteTask> jsts = siteTaskDAO.findByEmployeeAccount(employee.getAccount().getId());

			for (JobSiteTask jst : jsts) {
				tasks.add(jst.getTask());
			}

			List<JobTask> allTasks = new ArrayList<JobTask>(tasks);
			Collections.sort(allTasks);

			return allTasks;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getPreviousTitlesJSON() {
		JSONArray a = new JSONArray();
		a.addAll(employeeDAO.findCommonTitles());
		return a;
	}

	public List<AssessmentResult> getNccerResults() {
		if (nccerResults == null && employee != null) {
			nccerResults = new ArrayList<AssessmentResult>(employee.getAssessmentResults());

			Iterator<AssessmentResult> iterator = nccerResults.iterator();
			while (iterator.hasNext()) {
				AssessmentResult result = iterator.next();

				if (!result.isCurrent()
						|| result.getAssessmentTest().getAssessmentCenter().getId() != Account.ASSESSMENT_NCCER
						|| result.getAssessmentTest().getQualificationMethod().contains("-old"))
					iterator.remove();
			}
		}

		return nccerResults;
	}

	// Notes
	protected void addNote(String newNote) {
		addNote(newNote, LowMedHigh.Low);
	}

	protected void addNote(String newNote, LowMedHigh priority) {
		User user = new User(permissions.getUserId());
		super.addNote(employee.getAccount(), newNote, noteCategory, priority, true, Account.EVERYONE, user, employee);
	}

	protected void addNote(String newNote, int viewableBy) {
		User user = new User(permissions.getUserId());
		super.addNote(employee.getAccount(), newNote, noteCategory, LowMedHigh.Low, true, viewableBy, user, employee);
	}

	private void checkPermissions() throws NoRightsException {
		if (permissions.isContractor()) {
			if (!permissions.hasPermission(OpPerms.ContractorAdmin)
					&& !permissions.hasPermission(OpPerms.ContractorSafety))
				throw new NoRightsException("Contractor Admin or Safety");
		} else if (permissions.isOperatorCorporate()) {
			id = permissions.getAccountId();

			if (employee != null && employee.getAccount() != null
					&& permissions.getVisibleAccounts().contains(employee.getAccount().getId()))
				id = employee.getAccount().getId();
		}
	}

	private void loadOperators() {
		Set<OperatorSite> returnList = new HashSet<OperatorSite>();
		oqOperators = new ArrayList<OperatorSite>();
		hseOperators = new ArrayList<OperatorSite>();

		if (employee.getAccount().isContractor()) {
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
		} else if (employee.getAccount().isOperatorCorporate()) {
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

		removeEmployeeSites(returnList);
		addOperatorSiteToAppropriateList(returnList);

		Collections.sort(oqOperators);
		Collections.sort(hseOperators);

		loadNonEmployeeGUARDOperators(returnList);
	}

	private void fillSites(Set<OperatorSite> returnList, OperatorAccount operator) {
		if (operator.getJobSites().size() == 0) {
			boolean found = false;
			for (OperatorSite os : returnList) {
				if (os.getOperator().equals(operator) && os.getSite() == null) {
					found = true;
				}
			}

			if (!found) {
				returnList.add(new OperatorSite(operator));
			}
		} else {
			for (JobSite site : operator.getJobSites()) {
				if (site.getProjectStop() == null || site.getProjectStop().after(new Date())) {
					boolean found = false;
					for (OperatorSite os : returnList) {
						if (os.getSite() != null && os.getSite().equals(site)) {
							found = true;
						}
					}

					if (!found) {
						returnList.add(new OperatorSite(site));
					}
				}
			}
		}
	}

	private void loadNonEmployeeGUARDOperators(Set<OperatorSite> returnList) {
		Set<OperatorAccount> employeeGUARDSites = new HashSet<OperatorAccount>();
		for (OperatorSite operatorSite : returnList) {
			employeeGUARDSites.add(operatorSite.getOperator());
		}

		nonEmployeeGUARDOperators = new ArrayList<OperatorAccount>();

		for (EmployeeSite employeeSite : employee.getEmployeeSites()) {
			if (employeeSite.isCurrent() && !nonEmployeeGUARDOperators.contains(employeeSite.getOperator())) {
				nonEmployeeGUARDOperators.add(employeeSite.getOperator());
			}
		}

		nonEmployeeGUARDOperators.removeAll(employeeGUARDSites);
		Collections.sort(nonEmployeeGUARDOperators);

		loadUnusedNonEmployeeGUARDOperators(employeeGUARDSites);
	}

	private void loadUnusedNonEmployeeGUARDOperators(Set<OperatorAccount> employeeGUARDSites) {
		unusedNonEmployeeGUARDOperators = new ArrayList<OperatorAccount>();

		if (employee.getAccount().isContractor()) {
			ContractorAccount contractor = (ContractorAccount) employee.getAccount();
			unusedNonEmployeeGUARDOperators.addAll(contractor.getOperatorAccounts());
		} else if (employee.getAccount().isOperatorCorporate()) {
			OperatorAccount operator = (OperatorAccount) employee.getAccount();

			loadAllChildFacilities(operator.getTopAccount().getOperatorFacilities());
		}

		unusedNonEmployeeGUARDOperators.removeAll(employeeGUARDSites);
		unusedNonEmployeeGUARDOperators.removeAll(nonEmployeeGUARDOperators);

		Collections.sort(unusedNonEmployeeGUARDOperators);
	}

	private void loadAllChildFacilities(List<Facility> operatorFacilities) {
		for (Facility facility : operatorFacilities) {
			OperatorAccount operator = facility.getOperator();

			if (operator.isOperator()) {
				if (!unusedNonEmployeeGUARDOperators.contains(operator)) {
					unusedNonEmployeeGUARDOperators.add(operator);
				}
			} else {
				loadAllChildFacilities(operator.getOperatorFacilities());
			}
		}
	}

	private void loadActiveEmployees() {
		activeEmployees = employeeDAO.findWhere("accountID = " + account.getId() + " and STATUS <> 'Deleted'");
	}

	private void checkSsn() {
		if (!Strings.isEmpty(ssn)) {
			if (ssn.length() == 9) {
				employee.setSsn(ssn);
			} else if (!ssn.matches("X{5}\\d{4}")) {
				addActionError("Invalid social security number entered.");
			}
		}
	}

	private void addInitialSites() {
		if (initialSites != null) {
			for (Integer operatorID : initialSites) {
				EmployeeSite employeeSite = new EmployeeSite();
				employeeSite.setEmployee(employee);
				employeeSite.setOperator(new OperatorAccount());
				employeeSite.getOperator().setId(operatorID);
				employeeSite.setAuditColumns(permissions);
				employeeSite.defaultDates();

				dao.save(employeeSite);
			}
		}
	}

	private void removeEmployeeSites(Set<OperatorSite> returnList) {
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
	}

	private void addOperatorSiteToAppropriateList(Set<OperatorSite> returnList) {
		for (OperatorSite operatorSite : returnList) {
			if (operatorSite.getSite() != null) {
				oqOperators.add(operatorSite);
			} else if (operatorSite.getOperator().isRequiresCompetencyReview()) {
				hseOperators.add(operatorSite);
			}
		}
	}

	// Classes
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
				return site.getId();
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
			if (obj == null) {
				return false;
			}

			OperatorSite o = (OperatorSite) obj;

			if (!operator.equals(o.getOperator())) {
				return false;
			}

			if (site == null && o.getSite() == null) {
				return true;
			}

			if (site == null) {
				return false;
			}

			return site.equals(o.getSite());
		}

		@Override
		public int compareTo(OperatorSite o) {
			int opCompare = operator.compareTo(o.getOperator());

			if (opCompare != 0) {
				return opCompare;
			}

			if (site == null) {
				return -1;
			}

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
