package com.picsauditing.employeeguard.controllers.restful;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.services.CorpRoleService;
import com.picsauditing.employeeguard.services.SiteRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectRoleAction extends PicsRestActionSupport {
	private static Logger log = LoggerFactory.getLogger(ProjectRoleAction.class);

	int projectId;

	@Autowired
	private CorpRoleService corpRoleService;
	@Autowired
	private SiteRoleService siteRoleService;


	public String index() {
		//int accountId = permissions.getAccountId();



		return JSON_STRING;
	}
}
