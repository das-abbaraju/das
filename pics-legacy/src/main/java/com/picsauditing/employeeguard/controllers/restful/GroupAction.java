package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.GsonBuilder;
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
import com.picsauditing.employeeguard.models.MGroupsManager;
import com.picsauditing.employeeguard.services.ContractorGroupService;
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
import java.util.Set;

public class GroupAction extends PicsRestActionSupport {

	private static final long serialVersionUID = -4767047559683194585L;

	/* Service + Validator */
	@Autowired
	private ContractorGroupService contractorGroupService;

	/* Forms */

	/* Models */

	private Group group;
	private List<Group> groups;
	private List<AccountSkill> groupSkills;
	private List<Employee> groupEmployees;

	/* Other */
	private UrlBuilder urlBuilder;

	/* Pages */

	public String index() {

		Set<MGroupsManager.MGroup> mGroups = contractorGroupService.findGroups(permissions.getAccountId());
		jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(mGroups);

		return JSON_STRING;
	}
}
