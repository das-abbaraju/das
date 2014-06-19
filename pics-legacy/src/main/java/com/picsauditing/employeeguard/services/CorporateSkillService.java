package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.models.MSkillsManager;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CorporateSkillService {

	@Autowired
	private SkillEntityService skillEntityService;

	@Autowired
	private AccountService accountService;

	public Set<MSkillsManager.MSkill> findSkills(int accountId){
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(accountId);
		List<AccountSkill> skills = skillEntityService.findSkillsForCorporate(accountIds);
		Map<Integer,AccountSkill> reqdSkillsForCorpSiteMap = skillEntityService.findReqdSkillsForCorpSiteMap(accountId);

		Set<MSkillsManager.MSkill> mSkills = new MSkillsManager().copyBasicInfoAttachRolesAndFlagReqdSkills(skills, reqdSkillsForCorpSiteMap);

		return mSkills;
	}

	//TODO:Remove if not used by end of 2014-July
	public Set<MSkillsManager.MSkill> findReqdSkillsForCorpSite(int siteId){
		List<AccountSkill> skills = skillEntityService.findReqdSkillsForCorpSite(siteId);

		MSkillsManager skillsManager = new MSkillsManager();
		Set<MSkillsManager.MSkill> mSkills = skillsManager.copyBasicInfo(skills);
		return mSkills;
	}


}
