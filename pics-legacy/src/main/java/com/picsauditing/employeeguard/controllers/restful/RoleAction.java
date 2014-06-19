package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.models.MRolesManager;
import com.picsauditing.employeeguard.services.CorpRoleService;
import com.picsauditing.employeeguard.services.SiteRoleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class RoleAction extends PicsRestActionSupport {

	@Autowired
	private CorpRoleService corpRoleService;
	@Autowired
	private SiteRoleService siteRoleService;

	public String index() {
		int accountId = permissions.getAccountId();

		Set<MRolesManager.MRole> roles = null;
		if (permissions.isCorporate()) {
			roles = corpRoleService.findRolesForCorp(accountId);
		} else if (permissions.isOperator()) {
			roles = siteRoleService.findRolesForSite(accountId);
		}

		jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(roles);

		return JSON_STRING;
	}
}
