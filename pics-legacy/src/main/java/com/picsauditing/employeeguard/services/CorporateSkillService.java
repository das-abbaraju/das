package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CorporateSkillService {
	private static Logger log = LoggerFactory.getLogger(CorporateSkillService.class);

	@Autowired
	private SkillEntityService skillEntityService;

	@Autowired
	private AccountService accountService;

	public Set<MSkillsManager.MSkill> findSkills(int accountId) throws ReqdInfoMissingException {
		MSkillsManager mSkillsManager = MModels.fetchSkillsManager();
		List<MOperations> mSkillsOperations = new ArrayList<>();mSkillsOperations.add(MOperations.COPY_ID);mSkillsOperations.add(MOperations.COPY_NAME);mSkillsOperations.add(MOperations.ATTACH_ROLES);
		mSkillsManager.setmOperations(mSkillsOperations);

		MRolesManager mRolesManager = MModels.fetchRolesManager();
		List<MOperations> mRolesOperations = new ArrayList<>();mRolesOperations.add(MOperations.COPY_ID);mRolesOperations.add(MOperations.COPY_NAME);
		mRolesManager.setmOperations(mRolesOperations);


		List<AccountSkill> skills = skillEntityService.findSkillsForCorporate(accountId);
		//Map<Integer,AccountSkill> reqdSkillsForCorpSiteMap = skillEntityService.findReqdSkillsForCorpSiteMap(accountId);
		return mSkillsManager.copySkills(skills);
		//mModels.getmSkillsManager().copyBasicInfoAttachRolesAndFlagReqdSkills(skills, reqdSkillsForCorpSiteMap);
	}

	//TODO:Remove if not used by end of 2014-July
/*	public Set<MSkillsManager.MSkill> findReqdSkillsForCorpSite(int siteId){
		List<AccountSkill> skills = skillEntityService.findReqdSkillsForCorpSite(siteId);

		MSkillsManager skillsManager = new MSkillsManager();
		Set<MSkillsManager.MSkill> mSkills = skillsManager.copyBasicInfo(skills);
		return mSkills;
	}*/


}
