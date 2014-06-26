package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SiteProjectService {
	private static Logger log = LoggerFactory.getLogger(SiteProjectService.class);

	@Autowired
	private ProjectEntityService projectEntityService;

	@Autowired
	private SkillEntityService skillEntityService;

	@Autowired
	private AccountService accountService;
	

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

	public MAssignments calculateProjectRoleEmployeeAssignments(int id, int roleId) throws ReqdInfoMissingException {
		ProjectRole projectRole = projectEntityService.findProjectRole(id, roleId);

		List<MOperations> mStatusOperations = new ArrayList<>();mStatusOperations.add(MOperations.EVAL_ALL_SKILLS_STATUS);mStatusOperations.add(MOperations.COPY_NAME);
		MModels.fetchStatusManager().setmOperations(mStatusOperations);

		return findProjectRoleEmployees(id, Arrays.asList(projectRole));
	}

	public MAssignments calculateProjectEmployeeAssignments(int id) throws ReqdInfoMissingException {
		List<ProjectRole> projectRoles = projectEntityService.findProjectRoles(id);

		List<MOperations> mStatusOperations = new ArrayList<>();mStatusOperations.add(MOperations.EVAL_OVERALL_STATUS_ONLY);mStatusOperations.add(MOperations.COPY_NAME);
		MModels.fetchStatusManager().setmOperations(mStatusOperations);

		return findProjectRoleEmployees(id, projectRoles);
	}

	private MAssignments findProjectRoleEmployees(int id, List<ProjectRole> projectRoles)  throws ReqdInfoMissingException {
		Project project = projectEntityService.find(id);
		int accountId = project.getAccountId();
		AccountModel accountModel = accountService.getAccountById(accountId);

		List<AccountSkill> corpReqdSkills = skillEntityService.findAllParentCorpSiteRequiredSkills(accountId);
		List<AccountSkill> siteReqdSkills = skillEntityService.findReqdSkillsForAccount(accountId);

		List<MOperations> mSkillsOperations = new ArrayList<>();mSkillsOperations.add(MOperations.COPY_ID);mSkillsOperations.add(MOperations.COPY_NAME);
		MModels.fetchSkillsManager().setmOperations(mSkillsOperations);

		List<MOperations> mRolesOperations = new ArrayList<>();mRolesOperations.add(MOperations.COPY_ID);mRolesOperations.add(MOperations.COPY_NAME);mRolesOperations.add(MOperations.ATTACH_SKILLS);
		MModels.fetchRolesManager().setmOperations(mRolesOperations);

		List<MOperations> mContractorOperations = new ArrayList<>();mContractorOperations.add(MOperations.COPY_ID);mContractorOperations.add(MOperations.COPY_NAME);
		MModels.fetchContractorManager().setmOperations(mContractorOperations);


		List<MOperations> mCorporateOperations = new ArrayList<>();mCorporateOperations.add(MOperations.COPY_ID);mCorporateOperations.add(MOperations.COPY_NAME);mCorporateOperations.add(MOperations.ATTACH_REQD_SKILLS);
		MModels.fetchCorporateManager().setmOperations(mCorporateOperations);
		MModels.fetchCorporateManager().attachReqdSkills(accountId, corpReqdSkills);
		MModels.fetchCorporateManager().copySite(accountId, accountModel);


		List<MOperations> mSiteOperations = new ArrayList<>();mSiteOperations.add(MOperations.COPY_ID);mSiteOperations.add(MOperations.COPY_NAME);mSiteOperations.add(MOperations.ATTACH_REQD_SKILLS);
		MModels.fetchSitesManager().setmOperations(mSiteOperations);
		MModels.fetchSitesManager().attachReqdSkills(accountId, siteReqdSkills);		
		MModels.fetchSitesManager().copySite(accountId, accountModel);


		List<MOperations> mEmployeeOperations = new ArrayList<>();mEmployeeOperations.add(MOperations.COPY_ID);mEmployeeOperations.add(MOperations.COPY_NAME);mEmployeeOperations.add(MOperations.ATTACH_CONTRACTOR);mEmployeeOperations.add(MOperations.ATTACH_DOCUMENTATION);
		MModels.fetchContractorEmployeeManager().setmOperations(mEmployeeOperations);


		MProjectsManager mProjectsManager = MModels.fetchProjectManager();
		List<MOperations> mProjectsOperations = new ArrayList<>();mProjectsOperations.add(MOperations.COPY_ID);mProjectsOperations.add(MOperations.COPY_NAME);mProjectsOperations.add(MOperations.COPY_ACCOUNT_ID);mProjectsOperations.add(MOperations.ATTACH_ROLES);mProjectsOperations.add(MOperations.ATTACH_REQD_SKILLS);
		MModels.fetchProjectManager().setmOperations(mProjectsOperations);


		mProjectsManager.evalProjectAssignments(projectRoles);

		MAssignments mAssignments = mProjectsManager.fetchModel(project.getId()).getAssignments();

		return mAssignments;

	}

}
