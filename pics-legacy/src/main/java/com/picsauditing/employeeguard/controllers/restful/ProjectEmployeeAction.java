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

public class ProjectEmployeeAction extends PicsRestActionSupport {
	private static Logger log = LoggerFactory.getLogger(ProjectEmployeeAction.class);

	private int projectId;

	@Autowired
	private SiteProjectService siteProjectService;

	public String index() {
		try {
			MAssignments mAssignments = siteProjectService.calculateProjectEmployeeAssignments(projectId);
			jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(mAssignments);

		} catch (ReqdInfoMissingException e) {
			log.error("Required information missing - Failed to evaluate assignments for projectId={} ", projectId);
		}

		return JSON_STRING;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
}
