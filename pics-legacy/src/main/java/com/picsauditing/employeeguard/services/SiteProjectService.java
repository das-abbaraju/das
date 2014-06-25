package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class SiteProjectService {
	private static Logger log = LoggerFactory.getLogger(SiteProjectService.class);

	@Autowired
	private ProjectEntityService projectEntityService;

	public MProjectsManager.MProject findProject(int id) throws ReqdInfoMissingException {
		MProjectsManager mProjectsManager = MModels.fetchProjectManager();

		List<MOperations> mProjectsOperations = new ArrayList<>();mProjectsOperations.add(MOperations.COPY_ID);mProjectsOperations.add(MOperations.COPY_NAME);
		mProjectsManager.setmOperations(mProjectsOperations);

		Project project = projectEntityService.find(id);

		return mProjectsManager.copyProject(project);

	}

	public MProjectsManager.MProject findProjectRoles(int id) throws ReqdInfoMissingException {
		MProjectsManager mProjectsManager = MModels.fetchProjectManager();

		List<MOperations> mProjectsOperations = new ArrayList<>();mProjectsOperations.add(MOperations.COPY_ID);mProjectsOperations.add(MOperations.COPY_NAME);mProjectsOperations.add(MOperations.ATTACH_ROLES);
		mProjectsManager.setmOperations(mProjectsOperations);

		MRolesManager mRolesManager = MModels.fetchRolesManager();
		List<MOperations> mRolesOperations = new ArrayList<>();mRolesOperations.add(MOperations.COPY_ID);mRolesOperations.add(MOperations.COPY_NAME);
		mRolesManager.setmOperations(mRolesOperations);

		Project project = projectEntityService.find(id);

		return mProjectsManager.copyProject(project);

	}

	public MProjectsManager.MProject findProjectRoleEmployees(int id, int roleId) throws ReqdInfoMissingException {
		MProjectsManager mProjectsManager = MModels.fetchProjectManager();

		List<MOperations> mProjectsOperations = new ArrayList<>();mProjectsOperations.add(MOperations.COPY_ID);mProjectsOperations.add(MOperations.COPY_NAME);mProjectsOperations.add(MOperations.EVAL_EMPLOYEE_STATUS_RETAIN_SKILLS);
		mProjectsManager.setmOperations(mProjectsOperations);

		Project project = projectEntityService.find(id);

		return mProjectsManager.copyProject(project);

	}

}
