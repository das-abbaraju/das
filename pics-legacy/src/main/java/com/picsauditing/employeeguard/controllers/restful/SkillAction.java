package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.ContractorSkillService;
import com.picsauditing.employeeguard.services.CorporateSkillService;
import com.picsauditing.employeeguard.services.SiteSkillService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class SkillAction extends PicsRestActionSupport{

	@Autowired
	CorporateSkillService corporateSkillService;

	@Autowired
	SiteSkillService siteSkillService;

	@Autowired
	ContractorSkillService contractorSkillService;

	public String findSkills(){

		int accountId = permissions.getAccountId();
		Set<MSkillsManager.MSkill> mSkills;
		if(permissions.isCorporate()) {
			mSkills = corporateSkillService.findSkills(accountId);
		}else if(permissions.isOperator()){
			mSkills = siteSkillService.findSkills(accountId);
		}else{
			mSkills = contractorSkillService.findSkills(accountId);
		}

		jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(mSkills);

		return JSON_STRING;
	}

}
