package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.models.MSkillsManager;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CorpOpSkillService {

	@Autowired
	private SkillEntityService skillEntityService;

	public Set<MSkillsManager.MSkill> findSkillsForCorpOp(final List<Integer> accountIds, int accountId){
		List<AccountSkill> skills = skillEntityService.findSkillsForCorpOp(accountIds);
		Map<Integer,AccountSkill> reqdSkillsForCorpOpMap = skillEntityService.findReqdSkillsForCorpOpMap(accountId);

		MSkillsManager skillsManager = new MSkillsManager();
		Set<MSkillsManager.MSkill> mSkills = skillsManager.copyBasicInfoAttachRolesAndFlagReqdSkills(skills, reqdSkillsForCorpOpMap);

		return mSkills;
	}

	public Set<MSkillsManager.MSkill> findReqdSkillsForCorpOp(int siteId){
		List<AccountSkill> skills = skillEntityService.findReqdSkillsForCorpOp(siteId);

		MSkillsManager skillsManager = new MSkillsManager();
		Set<MSkillsManager.MSkill> mSkills = skillsManager.copyBasicInfo(skills);
		return mSkills;
	}

	public Set<MSkillsManager.MSkill> filterSkillsForCorpOp(String searchTerm, final List<Integer> accountIds, int accountId){
		List<AccountSkill> skills = skillEntityService.filterSkillsForCorpOp(searchTerm, accountIds);
		Map<Integer,AccountSkill> reqdSkillsForCorpOpMap = skillEntityService.findReqdSkillsForCorpOpMap(accountId);
		MSkillsManager skillsManager = new MSkillsManager();
		Set<MSkillsManager.MSkill> mSkills = skillsManager.copyBasicInfoAttachRolesAndFlagReqdSkills(skills, reqdSkillsForCorpOpMap);
		skillsManager.copyRoles(mSkills);
		return mSkills;
	}

}
