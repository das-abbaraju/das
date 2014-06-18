package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.CorpSiteSkillService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class SkillAction extends PicsRestActionSupport{

	@Autowired
	private AccountService accountService;

	@Autowired
	CorpSiteSkillService corpSiteSkillService;


	public String findSkillsForCorpSite(){

		int accountId = permissions.getAccountId();
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(accountId);

		Set<MSkillsManager.MSkill> mSkills = corpSiteSkillService.findSkillsForCorpSite(accountIds, accountId);

		Gson jsonObject = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		jsonString = jsonObject.toJson(mSkills);

		return JSON_STRING;
	}

}
