package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.MModels;
import com.picsauditing.employeeguard.models.MOperations;
import com.picsauditing.employeeguard.models.MRolesManager;
import com.picsauditing.employeeguard.models.MSkillsManager;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SiteRoleService {

	@Autowired
	private AccountService accountService;
	@Autowired
	private RoleEntityService roleEntityService;

	public Set<MRolesManager.MRole> findRolesForSite(final int siteId) throws ReqdInfoMissingException {
		MRolesManager mRolesManager = MModels.fetchRolesManager();
		List<MOperations> mRolesOperations = new ArrayList<>();mRolesOperations.add(MOperations.COPY_ID);mRolesOperations.add(MOperations.COPY_NAME);mRolesOperations.add(MOperations.ATTACH_SKILLS);
		mRolesManager.setmOperations(mRolesOperations);

		MSkillsManager mSkillsManager = MModels.fetchSkillsManager();
		List<MOperations> mSkillsOperations = new ArrayList<>();mSkillsOperations.add(MOperations.COPY_ID);mSkillsOperations.add(MOperations.COPY_NAME);
		mSkillsManager.setmOperations(mSkillsOperations);

		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		return mRolesManager.copyRoles(roleEntityService.findRolesForCorporateAccounts(corporateIds));
/*
		return new MRolesManager().copyBasicInfoAndAttachSkills(
				roleEntityService.findRolesForCorporateAccounts(corporateIds));
*/
	}
}
