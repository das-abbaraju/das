package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.models.MCorporate;
import com.picsauditing.employeeguard.models.MSkillsManager;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class CorpOpSkillService {

	@Autowired
	private SkillEntityService skillEntityService;

	public MCorporate findSkillsForCorpOp(final List<Integer> accountIds){
		List<AccountSkill> skills= skillEntityService.findSkillsForCorpOp(accountIds);

		MSkillsManager skillsManager = new MSkillsManager();

		Set<MSkillsManager.MSkill> mSkills = MSkillsManager.newSkillCollection();
		for(AccountSkill skill: skills){
			MSkillsManager.MSkill mSkill = skillsManager.attachWithModel(skill);
			mSkill.copyId().copyName();
			mSkills.add(mSkill);
		}

		MCorporate mcorporate = new MCorporate();
		mcorporate.setSkills(mSkills);
		return mcorporate;

	}

	public void findReqdSkillsForCorpOp(){

	}

	public void filterSkillsForCorpOp(){

	}

}
