package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.MRolesManager;
import com.picsauditing.employeeguard.services.CorpRoleService;
import com.picsauditing.employeeguard.services.SiteRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class RoleAction extends PicsRestActionSupport {
	private static Logger log = LoggerFactory.getLogger(RoleAction.class);

	@Autowired
	private CorpRoleService corpRoleService;
	@Autowired
	private SiteRoleService siteRoleService;

	public String index() {
		int accountId = permissions.getAccountId();

		Set<MRolesManager.MRole> mRoles = null;
		try {
			if (permissions.isCorporate()) {
				mRoles = corpRoleService.findRolesForCorp(accountId);
			} else if (permissions.isOperator()) {
				mRoles = siteRoleService.findRolesForSite(accountId);
			}

			jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(mRoles);

		} catch (ReqdInfoMissingException e) {
			log.error("Failed to get Roles - Required information missing -  ", e);
		}


		return JSON_STRING;
	}
}
