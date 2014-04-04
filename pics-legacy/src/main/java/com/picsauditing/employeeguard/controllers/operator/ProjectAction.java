package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.forms.operator.*;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.ProjectModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.ProjectService;
import com.picsauditing.employeeguard.services.RoleService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.validators.project.ProjectFormValidator;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.validator.Validator;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SuppressWarnings("serial")
public class ProjectAction extends PicsRestActionSupport implements AjaxValidator {
	private static final Logger LOG = LoggerFactory.getLogger(ProjectAction.class);
	/* services */

	@Autowired
	private AccountService accountService;
	@Autowired
	private FormBuilderFactory formBuilderFactory;
	@Autowired
	private RoleService roleService;
	@Autowired
	private ProjectFormValidator projectFormValidator;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private SkillService skillService;

	/* forms */

	@FormBinding("operator_project_search")
	private SearchForm searchForm;
	@FormBinding("operator_project_create")
	private ProjectForm projectForm;
	@FormBinding("operator_project_name_skills_edit")
	private ProjectNameSkillsForm projectNameSkillsForm;
	@FormBinding("operator_project_roles_edit")
	private ProjectRolesForm projectRolesForm;
	@FormBinding("operator_project_companies_edit")
	private ProjectCompaniesForm projectCompaniesForm;

	/* entities */

	private ProjectInfo project;
	private List<ProjectInfo> projects;
	private List<AccountSkill> projectSkills;
	private List<Role> projectRoles;
	private List<AccountModel> projectSites;

	/* pages */

	public String index() throws NoRightsException {
		if (!permissions.isOperatorCorporate()) {
			throw new NoRightsException("Operator or Corporate");
		}

		int accountId = permissions.getAccountId();
		List<Project> projectsFromDatabase = projectService.getProjectsForAccount(accountId);

		Map<Integer, AccountModel> accountModels;
		if (permissions.isCorporate()) {
			List<Integer> childOperators = accountService.getChildOperatorIds(accountId);
			accountModels = accountService.getIdToAccountModelMap(childOperators);
		} else {
			accountModels = accountService.getIdToAccountModelMap(Arrays.asList(accountId));
		}

		List<ProjectModel> projectModels = ModelFactory.getProjectModelFactory().createWithSiteNames(
				projectsFromDatabase,
				null,
				null,
				accountModels);

		Collections.sort(projectModels);

		jsonString = new Gson().toJson(projectModels);

		return JSON_STRING;
	}

	public String show() throws PageNotFoundException {
		Project projectFromDatabase = loadProject();

		List<Integer> accountIds = new ArrayList<>();
		for (ProjectCompany projectCompany : projectFromDatabase.getCompanies()) {
			accountIds.add(projectCompany.getAccountId());
		}

		projectSites = accountService.getAccountsByIds(accountIds);
		Collections.sort(projectSites, new Comparator<AccountModel>() {
			@Override
			public int compare(AccountModel o1, AccountModel o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		project = formBuilderFactory.getProjectInfoFactory().build(projectFromDatabase);

		return SHOW;
	}

	public String create() {
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());

		projectRoles = roleService.getRolesForAccounts(accountIds);
		projectSkills = skillService.getOptionalSkillsForAccounts(accountIds);
		projectSites = accountService.getChildOperators(permissions.getAccountId());

		return CREATE;
	}

	@SkipValidation
	public String editProjectNameSkillsSection() throws PageNotFoundException {
		Project projectFromDatabase = loadProject();
		AccountModel account = accountService.getAccountById(projectFromDatabase.getAccountId());
		projectNameSkillsForm = new ProjectNameSkillsForm.Builder().project(projectFromDatabase).site(account.getName()).build();

		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());
		projectSkills = skillService.getOptionalSkillsForAccounts(accountIds);

		return "name-skills-form";
	}

	@SkipValidation
	public String editProjectJobRolesSection() throws PageNotFoundException {
		Project projectFromDatabase = loadProject();
		projectRolesForm = new ProjectRolesForm.Builder().project(projectFromDatabase).build();

		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());
		projectRoles = roleService.getRolesForAccounts(accountIds);

		return "job-roles-form";
	}

	@SkipValidation
	public String editProjectCompaniesSection() throws PageNotFoundException {
		Project projectFromDatabase = loadProject();
		projectCompaniesForm = formBuilderFactory.getProjectCompaniesFormBuilder().build(projectFromDatabase);
		projectSites = accountService.getContractors(projectFromDatabase.getAccountId());

		return "companies-form";
	}

	private Project loadProject() throws PageNotFoundException {
		Project project = projectService.getProject(id, permissions.getAccountId());
		if (project == null) {
			LOG.error("Error finding project {} for account {}", id, permissions.getAccountId());
			throw new PageNotFoundException();
		}

		return project;
	}

	/* other methods */

	public String insert() throws Exception {
		Project project = projectForm.buildProject();

		if (permissions.isOperator()) {
			project.setAccountId(permissions.getAccountId());
		}

		projectService.save(project, permissions.getAccountId(), permissions.getAppUserID());

		if (projectForm.isAddAnother()) {
			return setUrlForRedirect("/employee-guard/operators/project/create");
		}

		return redirectToList();
	}

	public String update() throws Exception {
		Project projectFromDatabase = loadProject();

		if (projectNameSkillsForm != null) {
			projectFromDatabase = projectService.update(projectFromDatabase, projectNameSkillsForm, permissions.getAppUserID());
		} else if (projectRolesForm != null) {
			projectFromDatabase = projectService.update(projectFromDatabase, projectRolesForm, permissions.getAppUserID());
		} else if (projectCompaniesForm != null) {
			projectFromDatabase = projectService.update(projectFromDatabase, projectCompaniesForm, permissions.getAppUserID());
		}

		return setUrlForRedirect("/employee-guard/operators/project/" + projectFromDatabase.getId());
	}

	public String delete() throws Exception {
		projectService.delete(id, permissions.getAccountId(), permissions.getAppUserID());

		return redirectToList();
	}

	private String redirectToList() throws Exception {
		return setUrlForRedirect("/employee-guard/operators/project");
	}

	/* validation */

	@Override
	public Validator getCustomValidator() {
		return projectFormValidator;
	}

	@Override
	public void validate() {
		prepareFormDataWhenValidationFails();

		ValueStack valueStack = ActionContext.getContext().getValueStack();
		DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(this);

		projectFormValidator.validate(valueStack, validatorContext);
	}

	private void prepareFormDataWhenValidationFails() {
		projectRoles = roleService.getRolesForAccount(permissions.getAccountId());
		projectSkills = skillService.getOptionalSkillsForAccount(permissions.getAccountId());
	}

	/* getters + setters */

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public void setSearchForm(SearchForm searchForm) {
		this.searchForm = searchForm;
	}

	public ProjectForm getProjectForm() {
		return projectForm;
	}

	public void setProjectForm(ProjectForm projectForm) {
		this.projectForm = projectForm;
	}

	public ProjectNameSkillsForm getProjectNameSkillsForm() {
		return projectNameSkillsForm;
	}

	public void setProjectNameSkillsForm(ProjectNameSkillsForm projectNameSkillsForm) {
		this.projectNameSkillsForm = projectNameSkillsForm;
	}

	public ProjectRolesForm getProjectRolesForm() {
		return projectRolesForm;
	}

	public void setProjectRolesForm(ProjectRolesForm projectRolesForm) {
		this.projectRolesForm = projectRolesForm;
	}

	public ProjectCompaniesForm getProjectCompaniesForm() {
		return projectCompaniesForm;
	}

	public void setProjectCompaniesForm(ProjectCompaniesForm projectCompaniesForm) {
		this.projectCompaniesForm = projectCompaniesForm;
	}

	public ProjectInfo getProject() {
		return project;
	}

	public List<ProjectInfo> getProjects() {
		return projects;
	}

	public List<AccountSkill> getProjectSkills() {
		return projectSkills;
	}

	public List<Role> getProjectRoles() {
		return projectRoles;
	}

	public List<AccountModel> getProjectSites() {
		return projectSites;
	}
}
