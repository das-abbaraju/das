package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.operator.OperatorSkillForm;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.validators.skill.SkillFormValidator;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.web.UrlBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class SkillAction extends PicsRestActionSupport {


	/* Service + Validator */
	@Autowired
	private GroupService roleService;
	@Autowired
	private SkillService skillService;
	@Autowired
	private SkillFormValidator skillFormValidator;

	/* Forms */
	@FormBinding({"operator_skill_create", "operator_skill_edit"})
	private OperatorSkillForm skillForm;
	@FormBinding("")
	private SearchForm searchForm;

	/* Models */
	private AccountSkill skill;
	private List<AccountSkill> skills;
	private List<AccountGroup> roles;

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
		loadRoles();

		if (AjaxUtils.isAjax(this.getRequest())) {
			return "create-form";
		}

		return CREATE;
	}

	public String edit() throws Exception {
		loadSkill();
		skillForm = new OperatorSkillForm.Builder().accountSkill(skill).build();
		loadRoles();

		if (AjaxUtils.isAjax(this.getRequest())) {
			return "edit-form";
		}

		return EDIT;
	}

	private void loadSkill() {
		skill = skillService.getSkill(id, permissions.getAccountId());
	}

	private void loadRoles() {
		roles = roleService.getGroupsForAccount(permissions.getAccountId());
	}

	@SkipValidation
	public String deleteConfirmation() {
		return "delete-confirmation";
	}

	@SkipValidation
	public String editSkillSection() {
		if (skillForm == null) {
			loadSkill();
			skillForm = new OperatorSkillForm.Builder().accountSkill(skill).build();
		} else {
			skill = skillForm.buildAccountSkill();
		}

		loadRoles();

		return "edit-form";
	}

	public String insert() throws Exception {
		skill = skillForm.buildAccountSkill();
		skillService.save(skill, permissions.getAccountId(), permissions.getUserId());

		if (addAnother(skillForm)) {
			return setUrlForRedirect("/employee-guard/operator/skill/create");
		}

		return redirectToList();
	}

	public String update() throws Exception {
		int accountId = permissions.getAccountId();
		skill = skillForm.buildAccountSkill(NumberUtils.toInt(id), accountId);
		skill = skillService.update(skill, id, accountId, permissions.getUserId());

		return setUrlForRedirect("/employee-guard/operator/skill/" + skill.getId());
	}

	public String delete() throws Exception {
		skillService.delete(id, permissions.getAccountId(), permissions.getUserId());

		return redirectToList();
	}

	private String redirectToList() throws Exception {
		String url = new UrlBuilder().action("skill").build();
		return setUrlForRedirect(url);
	}


	/* Form - Getters + Setters */

	public OperatorSkillForm getSkillForm() {
		return skillForm;
	}

	public void setSkillForm(OperatorSkillForm skillForm) {
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

	public List<AccountGroup> getRoles() {
		return roles;
	}

	public String getDisplayName() {
		if (skill != null) {
			return skill.getName();
		}

		return null;
	}
}
