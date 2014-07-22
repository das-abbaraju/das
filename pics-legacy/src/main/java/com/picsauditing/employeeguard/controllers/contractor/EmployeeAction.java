package com.picsauditing.employeeguard.controllers.contractor;

import com.google.common.collect.Table;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.AuthenticationAware;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeeEmploymentForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeeForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePersonalForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePhotoForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.process.EmployeeSkillData;
import com.picsauditing.employeeguard.process.EmployeeSkillDataProcess;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.email.EmailHashService;
import com.picsauditing.employeeguard.services.email.EmailService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.employeeguard.validators.employee.EmployeeEmploymentFormValidator;
import com.picsauditing.employeeguard.validators.employee.EmployeeFormValidator;
import com.picsauditing.employeeguard.validators.employee.EmployeePhotoFormValidator;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.util.web.UrlBuilder;
import com.picsauditing.validator.Validator;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@AuthenticationAware
public class EmployeeAction extends PicsRestActionSupport implements AjaxValidator {

	private static final long serialVersionUID = 1334317982294098486L;

	/* Service + Validator */
	@Autowired
	private AccountService accountService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private EmailHashService emailHashService;
	@Autowired
	private EmployeeFormValidator employeeFormValidator;
	@Autowired
	private EmployeePhotoFormValidator employeePhotoFormValidator;

	@Autowired
	private EmployeeEmploymentFormValidator employeeEmploymentFormValidator;
	@Autowired
	private FormBuilderFactory formBuilderFactory;
	@Autowired
	private GroupService groupService;
	@Autowired
	private PhotoUtil photoUtil;
	@Autowired
	private ProfileDocumentService profileDocumentService;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private EmployeeSkillDataProcess employeeSkillDataProcess;

	/* Forms */
	@FormBinding("contractor_employee_create")
	private EmployeeForm employeeForm;
	@FormBinding("contractor_employee_edit_personal")
	private EmployeePersonalForm employeePersonalForm;
	@FormBinding("contractor_employee_edit_employment")
	private EmployeeEmploymentForm employeeEmploymentForm;
	@FormBinding("contractor_employee_edit_photo")
	private EmployeePhotoForm employeePhotoForm;
	@FormBinding("contractor_employee_search")
	private SearchForm searchForm;

	/* Models */
	private Employee employee;
	private List<Employee> employees;
	private List<Group> employeeGroups;
	private List<SkillInfo> skillInfoList;
	private List<EmployeeAssignmentModel> employeeAssignments;

	/* Misc data */
	private InputStream inputStream;
	private Table<Employee, String, Integer> employeeSkillStatuses;
	private UrlBuilder urlBuilder;

	/* Pages */
	public String index() {
		if (isSearch(searchForm)) {
			String searchTerm = searchForm.getSearchTerm();
			employees = employeeEntityService.search(searchTerm, permissions.getAccountId());
		} else {
			employees = employeeEntityService.getEmployeesForAccount(permissions.getAccountId());
		}

		Collections.sort(employees);

		loadEmployeeSkillStatuses(employees);

		return LIST;
	}

	public String show() throws PageNotFoundException {
		loadEmployee();

		Set<Employee> employees = new HashSet<>(Arrays.asList(employee));
		Map<AccountModel, Set<AccountModel>> siteHierarchy = getSiteHierarchy(employees);
		EmployeeSkillData employeeSkillData = employeeSkillDataProcess.buildEmployeeSkillData(permissions.getAccountId(),
				employee, siteHierarchy);
		skillInfoList = formBuilderFactory.getSkillInfoBuilder().build(employeeSkillData.getSkillStatuses());

		Collections.sort(skillInfoList);

		loadEmployeeAssignments(employee);

		return SHOW;
	}

	public String create() throws IOException {
		loadEmployeeGroups();

		return CREATE;
	}

	@SkipValidation
	public String editPersonalSection() {
		loadEmployee();

		return "personal-form";
	}

	@SkipValidation
	public String editEmploymentSection() {
		loadEmployee();
		loadEmployeeGroups();

		return "employment-form";
	}

	@SkipValidation
	public String editAssignmentSection() {
		loadEmployee();

		return "assignment-form";
	}

	@SkipValidation
	public String photo() throws FileNotFoundException {
		if (NumberUtils.toInt(id) > 0) {
			employee = employeeEntityService.find(getIdAsInt(), permissions.getAccountId());
			inputStream = photoUtil.getPhotoStreamForEmployee(employee, permissions.getAccountId(), getFtpDir());

			if (inputStream == null) {
				// Try profile
				inputStream = photoUtil.getPhotoStreamForProfile(profileDocumentService.getPhotoDocumentFromProfile(employee.getProfile()), getFtpDir());
			}
		}

		if (inputStream == null) {
			// If neither employee photo or profile photo exists
			inputStream = photoUtil.getDefaultPhotoStream(getFtpDir());
		}

		return "photo";
	}

	/* Other Methods */

	public String insert() throws Exception {
		int accountId = permissions.getAccountId();
		employee = employeeService.save(employeeForm, getFtpDir(), accountId, permissions.getAppUserID());

		EmailHash hash = emailHashService.createNewHash(employee);
		emailService.sendEGWelcomeEmail(hash, permissions.getAccountName());

		if (addAnother(employeeForm)) {
			return setUrlForRedirect("/employee-guard/contractor/employee/create");
		}

		return redirectToList();
	}

	public String update() throws Exception {
		if (employeePersonalForm != null) {
			employee = employeeService.updatePersonal(employeePersonalForm, getIdAsInt(), permissions.getAccountId(), permissions.getAppUserID());
		} else if (employeeEmploymentForm != null) {
			employee = employeeService.updateEmployment(employeeEmploymentForm, getIdAsInt(), permissions.getAccountId(), permissions.getAppUserID());
		} else if (employeePhotoForm != null) {
			employee = employeeEntityService.updatePhoto(employeePhotoForm, getFtpDir(), getIdAsInt(), permissions.getAccountId());
		} else {
			// Since there is another form that needs to be implemented for assignments, we will just redirect back for now.
			return setUrlForRedirect("/employee-guard/contractor/employee/" + id);
		}

		return setUrlForRedirect("/employee-guard/contractor/employee/" + employee.getId());
	}

	public String delete() throws Exception {
		employeeEntityService.delete(getIdAsInt(), permissions.getAccountId());

		return redirectToList();
	}

	private String redirectToList() throws Exception {
		String url = urlBuilder().action("employee").build();
		return setUrlForRedirect(url);
	}

	private Employee loadEmployee() {
		employee = employeeEntityService.find(getIdAsInt(), permissions.getAccountId());

		return employee;
	}

	private void loadEmployeeAssignments(Employee employee) {
		Set<Integer> siteAssignments = assignmentService.findAllEmployeeSiteAssignments(employee);
		Set<Project> projects = projectEntityService.getProjectsForEmployee(employee);

		Map<Integer, AccountModel> accountModelMap = accountService.getIdToAccountModelMap(siteAssignments);

		employeeAssignments = ViewModelFactory.getEmployeeAssignmentModelFactory().create(projects, accountModelMap);
	}

	private void loadEmployeeGroups() {
		employeeGroups = groupService.getGroupsForAccount(permissions.getAccountId());
	}

	private void loadEmployeeSkillStatuses(List<Employee> employees) {
		Map<AccountModel, Set<AccountModel>> siteHierarchy = getSiteHierarchy(employees);

		employeeSkillStatuses = employeeSkillDataProcess.buildEmployeeSkillStatuses(permissions.getAccountId(),
				employees, siteHierarchy);
	}

	private Map<AccountModel, Set<AccountModel>> getSiteHierarchy(Collection<Employee> employees) {
		Map<Employee, Set<Integer>> employeeSiteAssignments = employeeEntityService
				.getEmployeeSiteAssignments(employees);

		return accountService.getSiteParentAccounts(PicsCollectionUtil
				.flattenCollectionOfCollection(employeeSiteAssignments.values()));
	}

	private UrlBuilder urlBuilder() {
		if (urlBuilder == null) {
			urlBuilder = new UrlBuilder();
		}

		return urlBuilder;
	}

	/* Validation */

	// For the Ajax Validation
	public Validator getCustomValidator() {
		if (employeeForm != null || employeePersonalForm != null) {
			return employeeFormValidator;
		}

		if (employeeEmploymentForm != null) {
			return employeeEmploymentFormValidator;
		}

		return employeePhotoFormValidator;
	}

	@Override
	public void validate() {
		prepareFormDataWhenValidationFails();

		ValueStack valueStack = ActionContext.getContext().getValueStack();
		DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(this);

		if (employeeForm != null || employeePersonalForm != null) {
			employeeFormValidator.validate(valueStack, validatorContext);
		}
		if (employeeEmploymentForm != null) {
			employeeEmploymentFormValidator.validate(valueStack, validatorContext);
		}
		if (employeePhotoForm != null) {
			employeePhotoFormValidator.validate(valueStack, validatorContext);
		}

	}

	private void prepareFormDataWhenValidationFails() {
		loadEmployeeGroups();
	}

	/* Form - Getters + Setters */

	public EmployeeForm getEmployeeForm() {
		return employeeForm;
	}

	public void setEmployeeForm(EmployeeForm employeeForm) {
		this.employeeForm = employeeForm;
	}

	public EmployeePersonalForm getEmployeePersonalForm() {
		return employeePersonalForm;
	}

	public void setEmployeePersonalForm(EmployeePersonalForm employeePersonalForm) {
		this.employeePersonalForm = employeePersonalForm;
	}

	public EmployeeEmploymentForm getEmployeeEmploymentForm() {
		return employeeEmploymentForm;
	}

	public void setEmployeeEmploymentForm(EmployeeEmploymentForm employeeEmploymentForm) {
		this.employeeEmploymentForm = employeeEmploymentForm;
	}

	public EmployeePhotoForm getEmployeePhotoForm() {
		return employeePhotoForm;
	}

	public void setEmployeePhotoForm(EmployeePhotoForm employeePhotoForm) {
		this.employeePhotoForm = employeePhotoForm;
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public void setSearchForm(SearchForm searchForm) {
		this.searchForm = searchForm;
	}

    /* Model - Getters */

	public Employee getEmployee() {
		return employee;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public List<Group> getEmployeeGroups() {
		return employeeGroups;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public Table<Employee, String, Integer> getEmployeeSkillStatuses() {
		return employeeSkillStatuses;
	}

	public List<SkillInfo> getSkillInfoList() {
		return skillInfoList;
	}

	public List<EmployeeAssignmentModel> getEmployeeAssignments() {
		return employeeAssignments;
	}
}