package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.models.MRolesManager;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class SiteRoleService {

	@Autowired
	private AccountService accountService;
	@Autowired
	private RoleEntityService roleEntityService;

	public Set<MRolesManager.MRole> findRolesForSite(final int siteId) {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);

		return new MRolesManager().copyBasicInfoAndAttachSkills(
				roleEntityService.findRolesForCorporateAccounts(corporateIds));
	}
}
