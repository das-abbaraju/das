package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.models.MRolesManager;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Set;

public class CorpRoleService {

	@Autowired
	private RoleEntityService roleEntityService;

	public Set<MRolesManager.MRole> findRolesForCorp(final int corpId) {
		return new MRolesManager().copyBasicInfoAndAttachSkills(
				roleEntityService.findRolesForCorporateAccounts(Arrays.asList(corpId)));
	}
}
