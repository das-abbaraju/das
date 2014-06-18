package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.converter.RequiredSiteSkillFormConverter;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.CorpOpSkillService;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.validators.skill.OperatorSkillFormValidator;
import com.picsauditing.forms.binding.FormBinding;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkillAction extends PicsRestActionSupport{

	@Autowired
	private AccountService accountService;

	@Autowired
	CorpOpSkillService corpOpSkillService;

	private String filter;

	public String findSkillsForCorpOp(){
		if (StringUtils.isNotEmpty(filter))
			return filterSkillsForCorpOp();

		int accountId = permissions.getAccountId();
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(accountId);

		Set<MSkillsManager.MSkill> mSkills = corpOpSkillService.findSkillsForCorpOp(accountIds, accountId);



		Gson jsonObject = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		jsonString = jsonObject.toJson(mSkills);

		return JSON_STRING;
	}

	public String filterSkillsForCorpOp(){
		Gson jsonObject = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		if (StringUtils.isEmpty(filter))
			return JSON_STRING;
		MFilter mFilter = jsonObject.fromJson(filter, MFilter.class);
		String searchTerm = mFilter.removeWildCards(mFilter.getName());
		if (StringUtils.isEmpty(searchTerm))
			return JSON_STRING;

		int accountId = permissions.getAccountId();
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(accountId);

		Set<MSkillsManager.MSkill> mSkills = corpOpSkillService.filterSkillsForCorpOp(searchTerm, accountIds, accountId);

		jsonString = jsonObject.toJson(mSkills);
		return JSON_STRING;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
}
