package com.picsauditing.employeeguard.controllers.operator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.forms.operator.OperatorJobRoleForm;
import com.picsauditing.employeeguard.forms.operator.RoleProjectsForm;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.validators.group.RoleFormValidator;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.util.web.UrlBuilder;
import com.picsauditing.validator.Validator;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class RoleAction extends PicsRestActionSupport implements AjaxValidator {

	private static final long serialVersionUID = -7045370359496904122L;

	/* Service + Validator */
	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private GroupService roleService;
	@Autowired
	private SkillService skillService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private RoleFormValidator roleFormValidator;

	/* Forms */
	@FormBinding("operator_role_create")
	private OperatorJobRoleForm roleForm;
	@FormBinding("operator_role_edit_name_skills")
	private GroupNameSkillsForm roleNameSkillsForm;
	@FormBinding("operator_role_edit_employees")
	private GroupEmployeesForm roleEmployeesForm;
	@FormBinding("operator_role_project_edit")
	private RoleProjectsForm roleProjectsForm;
	@FormBinding("operator_role_search")
	private SearchForm searchForm;

	/* Models */
	private Group role;
	private Project roleProject;
	private List<Project> operatorProjects;
	private List<Group> roles;
	private List<AccountSkill> roleSkills;
	private List<Employee> roleEmployees;
	private Map<AccountModel, List<ProjectRole>> siteProjects;

	/* Pages */
	public String index() {
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());

		if (isSearch(getSearchForm())) {
			String searchTerm = getSearchForm().getSearchTerm();
			roles = roleService.search(searchTerm, accountIds);
		} else {
			roles = roleService.getGroupsForAccounts(accountIds);
		}

		Collections.sort(roles);

		return LIST;
	}

	public String show() throws PageNotFoundException {
		loadRole();
		loadSiteProjects();

		return SHOW;
	}

	public String create() {
		loadSkills();
		loadEmployees();

		return CREATE;
	}

	@SkipValidation
	public String editNameSkillsSection() {
		loadRole();
		loadSkills();

		return "name-skills-form";
	}

	@SkipValidation
	public String editEmployeesSection() {
		loadRole();
		loadEmployees();

		return "employees-form";
	}

	@SkipValidation
	public String editRoleCurrentProjectsSection() {
		loadRole();
		loadProject();
		loadOperatorProjects();

		return "role-project-form";
	}

	/* Other Methods */

	public String insert() throws Exception {
		role = roleForm.buildAccountGroup();
		roleService.save(role, permissions.getAccountId(), permissions.getUserId());

		if (addAnother(roleForm)) {
			return setUrlForRedirect("/employee-guard/operator/role/create");
		}

		return redirectToList();
	}

	public String update() throws Exception {
		loadRole();

		if (roleNameSkillsForm != null) {
			role = roleService.update(roleNameSkillsForm, id, permissions.getAccountId(), permissions.getUserId());
		} else if (roleEmployeesForm != null) {
			role = roleService.update(roleEmployeesForm, id, permissions.getAccountId(), permissions.getUserId());
		} else {
			role = roleService.update(roleProjectsForm, role, permissions.getAccountId(), permissions.getAppUserID());
		}

		return setUrlForRedirect("/employee-guard/operator/role/" + role.getId());
	}

	public String delete() throws Exception {
		roleService.delete(id, permissions.getAccountId(), permissions.getUserId());

		return redirectToList();
	}

	private String redirectToList() throws Exception {
		String url = new UrlBuilder().action("role").build();
		return setUrlForRedirect(url);
	}

	private void loadRole() {
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());

		Group roleById = roleService.getGroup(id);
		if (accountIds.contains(roleById.getAccountId())) {
			role = roleById;
		}
	}

	private void loadSkills() {
		roleSkills = skillService.getOptionalSkillsForAccount(permissions.getAccountId());
	}

	private void loadEmployees() {
		roleEmployees = employeeService.getEmployeesForAccount(permissions.getAccountId());
	}

	private void loadProject() {
		roleProject = projectService.getProjectByRoleAndAccount(id, permissions.getAccountId());
	}

	private void loadOperatorProjects() {
		operatorProjects = projectService.getProjectsForAccount(permissions.getAccountId());
	}

	private void loadSiteProjects() {
		List<Integer> accountIds = ExtractorUtil.extractList(role.getProjects(), new Extractor<ProjectRole, Integer>() {
			@Override
			public Integer extract(ProjectRole projectRole) {
				return projectRole.getProject().getAccountId();
			}
		});

		Map<Integer, AccountModel> accounts = accountService.getIdToAccountModelMap(accountIds);
		siteProjects = new TreeMap<>();
		for (ProjectRole projectRole : role.getProjects()) {
			int accountId = projectRole.getProject().getAccountId();
			AccountModel account = accounts.get(accountId);

			if (!siteProjects.containsKey(account)) {
				siteProjects.put(account, new ArrayList<ProjectRole>());
			}

			siteProjects.get(account).add(projectRole);
		}
	}

	/* validation */

	@Override
	public Validator getCustomValidator() {
		return roleFormValidator;
	}

	@Override
	public void validate() {
		ValueStack valueStack = ActionContext.getContext().getValueStack();
		DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(this);

		roleFormValidator.validate(valueStack, validatorContext);
	}

	/* getters and setters */

	public Group getRole() {
		return role;
	}

	public List<Group> getRoles() {
		return roles;
	}

	public Project getRoleProject() {
		return roleProject;
	}

	public List<AccountSkill> getRoleSkills() {
		return roleSkills;
	}

	public List<Employee> getRoleEmployees() {
		return roleEmployees;
	}

	public List<Project> getOperatorProjects() {
		return operatorProjects;
	}

	public Map<AccountModel, List<ProjectRole>> getSiteProjects() {
		return siteProjects;
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public void setSearchForm(SearchForm searchForm) {
		this.searchForm = searchForm;
	}

	public OperatorJobRoleForm getRoleForm() {
		return roleForm;
	}

	public void setRoleForm(OperatorJobRoleForm roleForm) {
		this.roleForm = roleForm;
	}

	public GroupNameSkillsForm getRoleNameSkillsForm() {
		return roleNameSkillsForm;
	}

	public void setRoleNameSkillsForm(GroupNameSkillsForm roleNameSkillsForm) {
		this.roleNameSkillsForm = roleNameSkillsForm;
	}

	public GroupEmployeesForm getRoleEmployeesForm() {
		return roleEmployeesForm;
	}

	public void setRoleEmployeesForm(GroupEmployeesForm roleEmployeesForm) {
		this.roleEmployeesForm = roleEmployeesForm;
	}

	public RoleProjectsForm getRoleProjectsForm() {
		return roleProjectsForm;
	}

	public void setRoleProjectsForm(RoleProjectsForm roleProjectsForm) {
		this.roleProjectsForm = roleProjectsForm;
	}
}
