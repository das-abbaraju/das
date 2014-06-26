package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.MAssignments;
import com.picsauditing.employeeguard.services.CorpRoleService;
import com.picsauditing.employeeguard.services.SiteProjectService;
import com.picsauditing.employeeguard.services.SiteRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectRoleEmployeeAction extends PicsRestActionSupport {
	private static Logger log = LoggerFactory.getLogger(ProjectRoleEmployeeAction.class);

	private int projectId;
	private int roleId;

	@Autowired
	private SiteProjectService siteProjectService;

	public String index() {

		try {
			MAssignments mAssignments = siteProjectService.calculateProjectRoleEmployeeAssignments(projectId, roleId);
			jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(mAssignments);

		} catch (ReqdInfoMissingException e) {
			log.error("Required information missing - Failed to evaluate assignments for projectId={} roleId={}", projectId, roleId);
		}

		return JSON_STRING;
	}

	public int getProjectId() {
		return projectId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
}
