package com.picsauditing.actions.employees;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.PICS.Grepper;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.dao.LegacyEmployeeDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;
import com.picsauditing.validator.InputValidator;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("serial")
public class ManageEmployees extends AccountActionSupport implements Preparable {

	@Autowired
	protected LegacyEmployeeDAO legacyEmployeeDAO;
	@Autowired
	protected JobSiteDAO jobSiteDAO;
	@Autowired
	protected JobSiteTaskDAO siteTaskDAO;

	public static final String ADD = "add";
	public static final String EDIT = "edit";
	public static final String LOAD = "load";

	protected Employee employee;

	protected ContractorAudit audit;
	protected int[] initialClients;
	protected int[] initialJobSites;

	protected Set<JobRole> unusedJobRoles;
	protected List<AssessmentResult> nccerResults;

	private List<Employee> activeEmployees;
	private List<OperatorSite> allEmployeeGUARDOperators;

	private URLUtils urlUtil = new URLUtils();

	@Autowired
	private InputValidator inputValidator;

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

		if (audit != null) {
			account = audit.getContractorAccount();
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
		// TODO fix this wonkiness with an employee having to exist for the add page to work properly
		employee = new Employee();

		return ADD;
	}

	public String edit() {
		return EDIT;
	}

	public String save() throws IOException {
		boolean employeeIsNew = employee == null || employee.getId() == 0;

		validateInput();
		if (hasFieldErrors()) {
			String result = "editInputError";

			if (employeeIsNew) {
				result = "addInputError";
				// TODO fix this wonkiness with an employee having to exist for the add page to work properly
				employee = new Employee();
			}

			return result;
		}

		if (employee.getAccount() == null) {
			employee.setAccount(account);
		}

		if (!Strings.isEmpty(employee.getEmail()) && employee.getEmail().length() > 0) {
			employee.setEmail(EmailAddressUtils.validate(employee.getEmail()));
		}

		employee.setAuditColumns(permissions);
		legacyEmployeeDAO.save(employee);

		if (employeeIsNew) {
			addNote("Added employee " + employee.getDisplayName(), LowMedHigh.Med);
		}

		addInitialSites();

		String url = urlUtil.getActionUrl("ManageEmployees", new HashMap<String, Object>() {
			{
				if (audit != null) {
					put("audit", audit.getId());
					put("questionId", questionId);
				}

				put("account", account.getId());
				put("employee", employee.getId());
			}
		});

		addRequiredCompetenciesIfMissing();

		return setUrlForRedirect(url);
	}

	private void addRequiredCompetenciesIfMissing() {
		for (EmployeeSite employeeSite : employee.getEmployeeSites()) {
			OperatorAccount operator = employeeSite.getOperator();
			if (Strings.isEmpty(operator.getName())) {
				operator = dao.find(OperatorAccount.class, operator.getId());
			}

			for (OperatorCompetency operatorCompetency : operator.getCompetencies()) {
				if (operatorCompetency.isRequiresDocumentation() && employeeMissingCompetency(operatorCompetency)) {
					addRequiredEmployeeCompetency(operatorCompetency);
				}
			}
		}
	}

	private void addRequiredEmployeeCompetency(OperatorCompetency operatorCompetency) {
		EmployeeCompetency employeeCompetency = new EmployeeCompetency();
		employeeCompetency.setEmployee(employee);
		employeeCompetency.setCompetency(operatorCompetency);
		employeeCompetency.setAuditColumns(permissions);
		dao.save(employeeCompetency);

		employee.getEmployeeCompetencies().add(employeeCompetency);
	}

	private boolean employeeMissingCompetency(OperatorCompetency operatorCompetency) {
		for (EmployeeCompetency employeeCompetency : employee.getEmployeeCompetencies()) {
			if (employeeCompetency.getCompetency().equals(operatorCompetency)) {
				return false;
			}
		}

		return true;
	}

	public String inactivate() {
		if (employee != null) {
			employee.setStatus(UserStatus.Inactive);
			legacyEmployeeDAO.save(employee);
			addActionMessage("Employee " + employee.getDisplayName() + " Successfully deactivated.");
		}

		loadActiveEmployees();

		return SUCCESS;
	}

	public String activate() throws Exception {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("account", account);

		if (employee != null) {
			employee.setStatus(UserStatus.Active);
			legacyEmployeeDAO.save(employee);

			parameters.put("employee", employee.getId());
		}

		String actionUrl = urlUtil.getActionUrl("ManageEmployees", parameters);
		return setUrlForRedirect(actionUrl);
	}

	public String delete() {
		if (employee != null) {
			employee.setStatus(UserStatus.Deleted);
			legacyEmployeeDAO.save(employee);
			addActionMessage("Employee " + employee.getDisplayName() + " Successfully deleted.");
			employee = null;
		}

		loadActiveEmployees();

		return SUCCESS;
	}

	public String cancel() {
		return SUCCESS;
	}

	public String load() {
		return LOAD;
	}

	public void validateInput() {
		String errorMessageKey = inputValidator.validateName(employee.getFirstName());
		addFieldErrorIfMessage("employee.firstName", errorMessageKey);

		errorMessageKey = inputValidator.validateName(employee.getLastName());
		addFieldErrorIfMessage("employee.lastName", errorMessageKey);

		errorMessageKey = inputValidator.validateName(employee.getTitle(), false);
		addFieldErrorIfMessage("employee.title", errorMessageKey);

		errorMessageKey = inputValidator.validateDate(employee.getHireDate(), false);
		addFieldErrorIfMessage("employee.hireDate", errorMessageKey);

		errorMessageKey = inputValidator.validateDate(employee.getFireDate(), false);
		addFieldErrorIfMessage("employee.fireDate", errorMessageKey);

		errorMessageKey = inputValidator.validateEmail(employee.getEmail(), false);
		addFieldErrorIfMessage("employee.email", errorMessageKey);

		if (permissions.hasGroup(User.GROUP_CSR)) {
			errorMessageKey = inputValidator.validatePicsCSRPhoneNumber(employee.getPhone(), false);
			addFieldErrorIfMessage("user.phone", errorMessageKey);
		} else {
			errorMessageKey = inputValidator.validatePhoneNumber(employee.getPhone(), false);
			addFieldErrorIfMessage("user.phone", errorMessageKey);
		}

		errorMessageKey = inputValidator.validatePhoneNumber(employee.getPhone(), false);
		addFieldErrorIfMessage("employee.phone", errorMessageKey);

		errorMessageKey = inputValidator.validateDate(employee.getTwicExpiration(), false);
		addFieldErrorIfMessage("employee.twicExpiration", errorMessageKey);
	}

	public List<Employee> getActiveEmployees() {
		return activeEmployees;

	}

	public void setActiveEmployees(List<Employee> activeEmployees) {
		this.activeEmployees = activeEmployees;
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

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	public int[] getInitialClients() {
		return initialClients;
	}

	public void setInitialClients(int[] initialClients) {
		this.initialClients = initialClients;
	}

	public int[] getInitialJobSites() {
		return initialJobSites;
	}

	public void setInitialJobSites(int[] initialJobSites) {
		this.initialJobSites = initialJobSites;
	}

	public Date getToday() {
		return new Date();
	}

	public Date getExpirationDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 3);
		return cal.getTime();
	}

	public List<OperatorSite> getAllEmployeeGUARDOperators() {
		if (allEmployeeGUARDOperators == null) {
			loadOperators();
		}

		return allEmployeeGUARDOperators;
	}

	public List<OperatorSite> getHseOperators() {
		List<OperatorSite> hseOperators = new Grepper<OperatorSite>() {
			@Override
			public boolean check(OperatorSite operatorSite) {
				if (operatorSite.getSite() == null) {
					return true;
				}

				return false;
			}
		}.grep(getAllEmployeeGUARDOperators());

		return hseOperators;
	}

	public List<OperatorSite> getOqOperators() {
		List<OperatorSite> oqOperators = new Grepper<OperatorSite>() {
			@Override
			public boolean check(OperatorSite operatorSite) {
				if (operatorSite.getSite() != null) {
					return true;
				}

				return false;
			}
		}.grep(getAllEmployeeGUARDOperators());

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
				if (unusedJobRoles.contains(employeeRole.getJobRole())) {
					unusedJobRoles.remove(employeeRole.getJobRole());
				}
			}

			Iterator<JobRole> roleIter = unusedJobRoles.iterator();
			while (roleIter.hasNext()) {
				if (!roleIter.next().isActive()) {
					roleIter.remove();
				}
			}
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
		a.addAll(legacyEmployeeDAO.findCommonTitles());
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

	public boolean isHseOperator(EmployeeSite employeeSite) {
		if (employeeSite != null && employeeSite.isCurrent() && employeeSite.getJobSite() == null) {
			return isHseOperator(employeeSite.getOperator());
		}

		return false;
	}

	public boolean isHseOperator(OperatorAccount operator) {
		boolean hseOperator = false;
		if (operator != null && operator.isRequiresCompetencyReview()) {
			if (operatorHasHSECompetencyTag(operator)) {
				hseOperator = true;
			}

			if (operatorHasRequiredCompetencies(operator)) {
				hseOperator = true;
			}
		}

		return hseOperator;
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
		allEmployeeGUARDOperators = new ArrayList<OperatorSite>();

		if (employee != null && employee.getAccount() != null) {
			account = employee.getAccount();
		}

		if (account.isContractor()) {
			// if contractor employee, return site list of non-corporate sites
			ContractorAccount contractor = (ContractorAccount) account;

			for (ContractorOperator co : contractor.getNonCorporateOperators()) {
				fillSites(allEmployeeGUARDOperators, co.getOperatorAccount());
			}

			for (JobContractor jc : contractor.getJobSites()) {
				allEmployeeGUARDOperators.add(new OperatorSite(jc.getJob()));
			}
		} else if (account.isOperatorCorporate()) {
			// if operator employee return list of self, and if corporate, child
			// facilities
			OperatorAccount operator = (OperatorAccount) account;

			if (operator.isCorporate()) {
				fillSites(allEmployeeGUARDOperators, operator);

				for (Facility facility : operator.getCorporateFacilities()) {
					fillSites(allEmployeeGUARDOperators, facility.getOperator());
				}
			} else {
				// just add self
				fillSites(allEmployeeGUARDOperators, operator);
			}
		}

		removeEmployeeSites(allEmployeeGUARDOperators);

		Collections.sort(allEmployeeGUARDOperators);
	}

	private void fillSites(List<OperatorSite> returnList, OperatorAccount operator) {
		if (isHseOperator(operator)) {
			boolean found = false;
			for (OperatorSite os : returnList) {
				if (os.getOperator().equals(operator) && os.getSite() == null) {
					found = true;
				}
			}

			if (!found) {
				returnList.add(new OperatorSite(operator));
			}
		}

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

	private boolean operatorHasRequiredCompetencies(OperatorAccount operator) {
		for (OperatorCompetency operatorCompetency : operator.getCompetencies()) {
			if (operatorCompetency.isRequiresDocumentation()) {
				return true;
			}
		}

		return false;
	}

	private void loadActiveEmployees() {
		activeEmployees = legacyEmployeeDAO.findWhere("accountID = " + account.getId() + " and STATUS <> 'Deleted'");
	}

	private void addInitialSites() {
		if (initialClients != null) {
			for (Integer operatorID : initialClients) {
				EmployeeSite employeeSite = new EmployeeSite();
				employeeSite.setEmployee(employee);
				employeeSite.setOperator(new OperatorAccount());
				employeeSite.getOperator().setId(operatorID);
				employeeSite.setAuditColumns(permissions);
				employeeSite.defaultDates();

				dao.save(employeeSite);
				employee.getEmployeeSites().add(employeeSite);
			}
		}

		if (initialJobSites != null) {
			List<JobSite> sites = jobSiteDAO.findWhere(JobSite.class, "t.id IN (" + Strings.implode(initialJobSites)
					+ ")");

			for (JobSite site : sites) {
				EmployeeSite employeeSite = new EmployeeSite();
				employeeSite.setEmployee(employee);
				employeeSite.setOperator(site.getOperator());
				employeeSite.setJobSite(site);
				employeeSite.setAuditColumns(permissions);
				employeeSite.defaultDates();

				dao.save(employeeSite);
				employee.getEmployeeSites().add(employeeSite);
			}
		}
	}

	private void removeEmployeeSites(List<OperatorSite> returnList) {
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

	private boolean operatorHasHSECompetencyTag(OperatorAccount operator) {
		Set<Integer> processed = new HashSet<Integer>();
		while (operator != null && !processed.contains(operator.getId())) {
			if (operator.hasTagCategory(OperatorTagCategory.CompetencyReview)) {
				return true;
			}

			processed.add(operator.getId());
			operator = operator.getParent();
		}

		return false;
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
