package com.picsauditing.employeeguard.controllers.contractor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.employeeguard.validators.group.GroupFormValidator;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.util.web.UrlBuilder;
import com.picsauditing.validator.Validator;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class GroupAction extends PicsRestActionSupport implements AjaxValidator {

	private static final long serialVersionUID = -4767047559683194585L;

	/* Service + Validator */
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private SkillService skillService;
	@Autowired
	private GroupFormValidator groupFormValidator;

	/* Forms */
	@FormBinding("contractor_group_create")
	private GroupForm groupForm;
	@FormBinding("contractor_group_edit_name_skills")
	private GroupNameSkillsForm groupNameSkillsForm;
	@FormBinding("contractor_group_edit_employees")
	private GroupEmployeesForm groupEmployeesForm;
	@FormBinding("contractor_group_search")
	private SearchForm searchForm;

	/* Models */

	private Group group;
	private List<Group> groups;
	private List<AccountSkill> groupSkills;
	private List<Employee> groupEmployees;

	/* Other */
	private UrlBuilder urlBuilder;

	/* Pages */

	public String index() {
		if (isSearch(getSearchForm())) {
			String searchTerm = getSearchForm().getSearchTerm();
			groups = groupService.search(searchTerm, permissions.getAccountId());
		} else {
			groups = groupService.getGroupsForAccount(permissions.getAccountId());
		}

		Collections.sort(groups);

		return LIST;
	}

	public String show() throws PageNotFoundException {
		loadGroup();

		return SHOW;
	}

	public String create() {
		loadSkills();
		loadEmployees();

		return CREATE;
	}

	@SkipValidation
	public String editNameSkillsSection() {
		loadGroup();
		loadSkills();

		return "name-skills-form";
	}

	@SkipValidation
	public String editEmployeesSection() {
		loadGroup();
		loadEmployees();

		return "employees-form";
	}

	/* Other Methods */

	public String insert() throws Exception {
		group = groupForm.buildAccountGroup();
		groupService.save(group, permissions.getAccountId(), permissions.getAppUserID());

		if (addAnother(groupForm)) {
			return setUrlForRedirect("/employee-guard/contractor/employee-group/create");
		}

		return redirectToList();
	}

	public String update() throws Exception {
		if (groupNameSkillsForm != null) {
			group = groupService.update(groupNameSkillsForm, id, permissions.getAccountId(), permissions.getAppUserID());
		} else {
			group = groupService.update(groupEmployeesForm, id, permissions.getAccountId(), permissions.getAppUserID());
		}

		return setUrlForRedirect("/employee-guard/contractor/employee-group/" + group.getId());
	}

	public String delete() throws Exception {
		groupService.delete(id, permissions.getAccountId(), permissions.getAppUserID());

		return redirectToList();
	}

	private String redirectToList() throws Exception {
		String url = urlBuilder().action("employee-group").build();
		return setUrlForRedirect(url);
	}

	private void loadGroup() {
		group = groupService.getGroup(id, permissions.getAccountId());
	}

	private void loadSkills() {
		groupSkills = skillService.getOptionalSkillsForAccount(permissions.getAccountId());
	}

	private void loadEmployees() {
		groupEmployees = employeeEntityService.getEmployeesForAccount(permissions.getAccountId());
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
		return groupFormValidator;
	}

	@Override
	public void validate() {
		prepareFormDataWhenValidationFails();

		ValueStack valueStack = ActionContext.getContext().getValueStack();
		DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(this);

		groupFormValidator.validate(valueStack, validatorContext);
	}

	private void prepareFormDataWhenValidationFails() {
		loadEmployees();
		loadSkills();
	}

	/* Form - Getters + Setters */

	public GroupForm getGroupForm() {
		return groupForm;
	}

	public void setGroupForm(GroupForm groupForm) {
		this.groupForm = groupForm;
	}

	public GroupEmployeesForm getGroupEmployeesForm() {
		return groupEmployeesForm;
	}

	public void setGroupEmployeesForm(GroupEmployeesForm groupEmployeesForm) {
		this.groupEmployeesForm = groupEmployeesForm;
	}

	public GroupNameSkillsForm getGroupNameSkillsForm() {
		return groupNameSkillsForm;
	}

	public void setGroupNameSkillsForm(GroupNameSkillsForm groupNameSkillsForm) {
		this.groupNameSkillsForm = groupNameSkillsForm;
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public void setSearchForm(SearchForm searchForm) {
		this.searchForm = searchForm;
	}

	/* Model - Getters */

	public Group getGroup() throws PageNotFoundException {
		return group;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public List<AccountSkill> getGroupSkills() {
		return groupSkills;
	}

	public List<Employee> getGroupEmployees() {
		return groupEmployees;
	}
}
