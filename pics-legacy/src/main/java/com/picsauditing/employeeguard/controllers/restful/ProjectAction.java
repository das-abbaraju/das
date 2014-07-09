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

public class ProjectAction extends PicsRestActionSupport {
	private static Logger log = LoggerFactory.getLogger(ProjectAction.class);

	@Autowired
	private CorpRoleService corpRoleService;
	@Autowired
	private SiteRoleService siteRoleService;


	public String index() {

		int projectId = getIdAsInt();

		return JSON_STRING;
	}
}
