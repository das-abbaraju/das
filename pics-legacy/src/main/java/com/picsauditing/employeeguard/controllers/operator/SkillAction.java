package com.picsauditing.employeeguard.controllers.operator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.IntervalType;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.converter.RequiredSiteSkillFormConverter;
import com.picsauditing.employeeguard.forms.operator.OperatorSkillForm;
import com.picsauditing.employeeguard.forms.operator.RequiredSiteSkillForm;
import com.picsauditing.employeeguard.services.external.AccountService;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.validators.skill.OperatorSkillFormValidator;
import com.picsauditing.employeeguard.viewmodel.factory.RoleEmployeeCountFactory;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.web.UrlBuilder;
import com.picsauditing.validator.Validator;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SkillAction extends PicsRestActionSupport implements AjaxValidator {


	/* Service + Validator */
	@Autowired
	private AccountService accountService;
	@Autowired
	private GroupService roleService;
	@Autowired
	private SkillService skillService;
	@Autowired
	private OperatorSkillFormValidator operatorSkillFormValidator;
	@Autowired
	private RequiredSiteSkillFormConverter requiredSiteSkillFormConverter;

  @Autowired
  private RoleEntityService roleEntityService;

	/* Forms */
	@FormBinding({"operator_skill_create", "operator_skill_edit"})
	private OperatorSkillForm operatorSkillForm;
	@FormBinding("operator_required_skill_edit")
	private RequiredSiteSkillForm requiredSiteSkillForm;
	@FormBinding("operator_skill_search")
	private SearchForm searchForm;

	/* Models */
	private AccountSkill skill;
	private List<AccountSkill> skills;
	private List<Role> roles;

	private List<AccountSkill> requiredSkills;
	private Map<AccountModel, List<AccountSkill>> siteSkills;
	private List<AccountSkill> corporateSkills;

	/* Pages */
	public String index() {
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());

		if (isSearch(searchForm)) {
			String searchTerm = searchForm.getSearchTerm();
			skills = skillService.search(searchTerm, accountIds);
		} else {
			skills = skillService.getSkillsForAccounts(accountIds);
		}

		requiredSkills = skillService.getRequiredSkillsForSite(permissions.getAccountId());

		if (permissions.isCorporate()) {
			siteSkills = skillService.getSiteRequiredSkills(permissions.getAccountId());
		}

		Collections.sort(requiredSkills);
		Collections.sort(skills);

		return LIST;
	}

	public String show() {
		loadSkill();
		return SHOW;
	}

	public String create() {
		loadRoles();

		if (AjaxUtils.isAjax(this.getRequest())) {
			return "create-form";
		}

		return CREATE;
	}

	private void loadSkill() {
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());

		AccountSkill skillById = skillService.getSkill(id);
		if (accountIds.contains(skillById.getAccountId())) {
			skill = skillById;
		}
	}

	private void loadRoles() {
    List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());
		roles = roleEntityService.findRolesForCorporateAccounts(accountIds);
	}

	@SkipValidation
	public String editSkillSection() {
		if (operatorSkillForm == null) {
			loadSkill();
			operatorSkillForm = new OperatorSkillForm.Builder().accountSkill(skill).build();
		} else {
			skill = operatorSkillForm.buildAccountSkill(permissions.getAccountId());
		}

		loadRoles();

		return "edit-form";
	}

	public String insert() throws Exception {
		skill = operatorSkillForm.buildAccountSkill(permissions.getAccountId());
		skillService.save(skill, permissions.getAccountId(), permissions.getUserId());

		if (addAnother(operatorSkillForm)) {
			return setUrlForRedirect("/employee-guard/operators/skill/create");
		}

		return redirectToList();
	}

	public String editRequiredSkillsSection() {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());
		corporateSkills = skillService.getSkillsForAccounts(corporateIds);
		requiredSkills = skillService.getRequiredSkillsForSite(permissions.getAccountId());

		Collections.sort(corporateSkills);
		Collections.sort(requiredSkills);

		return "required-skills";
	}

	public String update() throws Exception {
		int accountId = permissions.getAccountId();
		skill = operatorSkillForm.buildAccountSkill(NumberUtils.toInt(id), accountId);
		skill = skillService.update(skill, id, accountId, permissions.getUserId());

		return setUrlForRedirect("/employee-guard/operators/skill/" + skill.getId());
	}

	public String updateRequiredSkills() throws Exception {
		List<AccountSkill> requiredSkills = requiredSiteSkillFormConverter.convert(requiredSiteSkillForm);
		skillService.setRequiredSkillsForSite(requiredSkills, id, permissions.getAppUserID());

		return setUrlForRedirect("/employee-guard/operators/skill");
	}

	public String delete() throws Exception {
		skillService.delete(id, permissions.getAccountId(), permissions.getUserId());

		return redirectToList();
	}

	private String redirectToList() throws Exception {
		String url = new UrlBuilder().action("skill").build();
		return setUrlForRedirect(url);
	}

	@Override
	public Validator getCustomValidator() {
		return operatorSkillFormValidator;
	}

	@Override
	public void validate() {
		ValueStack valueStack = ActionContext.getContext().getValueStack();
		DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(this);

		operatorSkillFormValidator.validate(valueStack, validatorContext);
	}

	/* Form - Getters + Setters */

	public OperatorSkillForm getOperatorSkillForm() {
		return operatorSkillForm;
	}

	public void setOperatorSkillForm(OperatorSkillForm operatorSkillForm) {
		this.operatorSkillForm = operatorSkillForm;
	}

	public RequiredSiteSkillForm getRequiredSiteSkillForm() {
		return requiredSiteSkillForm;
	}

	public void setRequiredSiteSkillForm(RequiredSiteSkillForm requiredSiteSkillForm) {
		this.requiredSiteSkillForm = requiredSiteSkillForm;
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public void setSearchForm(SearchForm searchForm) {
		this.searchForm = searchForm;
	}

	/* Model - Getters */

	public AccountSkill getSkill() throws PageNotFoundException {
		return skill;
	}

	public List<AccountSkill> getSkills() {
		return skills;
	}

	public List<Role> getRoles() {
		return roles;
	}

	/* Site / Corporate required skills */

	public List<AccountSkill> getRequiredSkills() {
		return requiredSkills;
	}

	public Map<AccountModel, List<AccountSkill>> getSiteSkills() {
		return siteSkills;
	}

	public List<AccountSkill> getCorporateSkills() {
		return corporateSkills;
	}

	public IntervalType[] getIntervalTypes() {
		return IntervalType.getDisplayableOptions();
	}

}
