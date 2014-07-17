package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.MModels;
import com.picsauditing.employeeguard.models.operations.MOperations;
import com.picsauditing.employeeguard.models.MRolesManager;
import com.picsauditing.employeeguard.models.MSkillsManager;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CorpRoleService {

	@Autowired
	private RoleEntityService roleEntityService;

	public Set<MRolesManager.MRole> findRolesForCorp(final int corpId) throws ReqdInfoMissingException {
		MRolesManager mRolesManager = MModels.fetchRolesManager();
		mRolesManager.operations().copyId().copyName().attachSkills();

		MSkillsManager mSkillsManager = MModels.fetchSkillsManager();
		mSkillsManager.operations().copyName().copyId();


		return mRolesManager.copyRoles(roleEntityService.findRolesForCorporateAccounts(Arrays.asList(corpId)));


	}
}
