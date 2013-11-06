package com.picsauditing.employeeguard.controllers.contractor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.IntervalType;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.SkillForm;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.validators.skill.SkillFormValidator;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.web.UrlBuilder;
import com.picsauditing.validator.Validator;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class SkillAction extends PicsRestActionSupport implements AjaxValidator {

	private static final long serialVersionUID = -3879403139978601779L;

	/* Service + Validator */
	@Autowired
	private GroupService groupService;
	@Autowired
	private SkillService skillService;
	@Autowired
	private SkillFormValidator skillFormValidator;

	/* Forms */
	@FormBinding({"contractor_skill_create", "contractor_skill_edit"})
	private SkillForm skillForm;
	@FormBinding("contractor_skill_search")
	private SearchForm searchForm;

	/* Models */
	private AccountSkill skill;
	private List<AccountSkill> skills;
	private List<AccountGroup> skillGroups;

	/* Other */
	private UrlBuilder urlBuilder;

	/* Pages */

	public String index() {
		if (isSearch(searchForm)) {
			String searchTerm = searchForm.getSearchTerm();
			skills = skillService.search(searchTerm, permissions.getAccountId());
		} else {
			skills = skillService.getSkillsForAccount(permissions.getAccountId());
		}

		Collections.sort(skills);

		return LIST;
	}

	public String show() {
		loadSkill();

		return SHOW;
	}

	public String create() {
		loadGroups();

		if (AjaxUtils.isAjax(this.getRequest())) {
			return "create-form";
		}

		return CREATE;
	}

	@SkipValidation
	public String editSkillSection() {
		if (skillForm == null) {
			loadSkill();
			skillForm = new SkillForm.Builder().accountSkill(skill).build();
		} else {
			skill = skillForm.buildAccountSkill();
		}

		loadGroups();

		return "edit-form";
	}

	/* Other Methods */

	public String insert() throws Exception {
		skill = skillForm.buildAccountSkill();
		skillService.save(skill, permissions.getAccountId(), permissions.getAppUserID());

		if (addAnother(skillForm)) {
			return setUrlForRedirect("/employee-guard/contractor/skill/create");
		}

		return redirectToList();
	}

	public String update() throws Exception {
		int accountId = permissions.getAccountId();
		skill = skillForm.buildAccountSkill(NumberUtils.toInt(id), accountId);
		skill = skillService.update(skill, id, accountId, permissions.getAppUserID());

		return setUrlForRedirect("/employee-guard/contractor/skill/" + skill.getId());
	}

	public String delete() throws Exception {
		skillService.delete(id, permissions.getAccountId(), permissions.getAppUserID());

		return redirectToList();
	}

	private String redirectToList() throws Exception {
		String url = urlBuilder().action("skill").build();
		return setUrlForRedirect(url);
	}

	private void loadSkill() {
		skill = skillService.getSkill(id, permissions.getAccountId());
	}

	private void loadGroups() {
		skillGroups = groupService.getGroupsForAccount(permissions.getAccountId());
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
		return skillFormValidator;
	}

	@Override
	public void validate() {
		prepareFormDataWhenValidationFails();

		ValueStack valueStack = ActionContext.getContext().getValueStack();
		DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(this);

		skillFormValidator.validate(valueStack, validatorContext);
	}

	private void prepareFormDataWhenValidationFails() {
		loadGroups();
	}

	/* Form - Getters + Setters */

	public SkillForm getSkillForm() {
		return skillForm;
	}

	public void setSkillForm(SkillForm skillForm) {
		this.skillForm = skillForm;
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

	public List<AccountGroup> getSkillGroups() {
		return skillGroups;
	}

	public IntervalType[] getIntervalTypes() {
		return IntervalType.getDisplayableOptions();
	}
}
