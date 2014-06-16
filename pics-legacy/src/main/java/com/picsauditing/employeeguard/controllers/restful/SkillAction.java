package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.converter.RequiredSiteSkillFormConverter;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.MCorporate;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.CorpOpSkillService;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.validators.skill.OperatorSkillFormValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class SkillAction extends PicsRestActionSupport{

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


	@Autowired
	CorpOpSkillService corpOpSkillService;


	public String findSkillsForCorpOp(){
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());

		MCorporate mCorporate = corpOpSkillService.findSkillsForCorpOp(accountIds);

		Gson jsonObject = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		jsonString = jsonObject.toJson(mCorporate);
		return jsonString;
	}

}
