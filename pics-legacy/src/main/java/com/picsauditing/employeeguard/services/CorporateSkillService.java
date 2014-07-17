package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.models.operations.MOperations;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CorporateSkillService {
	private static Logger log = LoggerFactory.getLogger(CorporateSkillService.class);

	@Autowired
	private SkillEntityService skillEntityService;

	@Autowired
	private AccountService accountService;

	public Set<MSkillsManager.MSkill> findSkills(int accountId) throws ReqdInfoMissingException {
		MSkillsManager mSkillsManager = MModels.fetchSkillsManager();
		mSkillsManager.operations().copyName().copyId().attachRoles();

		MModels.fetchRolesManager().operations().copyId().copyName();

		List<AccountSkill> skills = skillEntityService.findSkillsForCorporate(accountId);
		return mSkillsManager.copySkills(skills);
	}

	//TODO:Remove if not used by end of 2014-July
/*	public Set<MSkillsManager.MSkill> findReqdSkillsForCorpSite(int siteId){
		List<AccountSkill> skills = skillEntityService.findReqdSkillsForCorpSite(siteId);

		MSkillsManager skillsManager = new MSkillsManager();
		Set<MSkillsManager.MSkill> mSkills = skillsManager.copyBasicInfo(skills);
		return mSkills;
	}*/


}
