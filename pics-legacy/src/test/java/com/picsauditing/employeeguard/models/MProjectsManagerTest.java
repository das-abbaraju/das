package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MProjectsManagerTest extends MManagersTest {
	private List<ProjectRoleEmployee> pres;
	private ProjectRole projectRole;
	private Project project;
	private Role role;
	private Employee employee;
	private List<AccountSkill> corpReqdSkills;
	private List<AccountSkill> siteReqdSkills;
	private int siteAccountId = egTestDataUtil.SITE_ID;
	private int contractorAccountId = egTestDataUtil.CONTRACTOR_ID;



	@Before
	public void setUp() throws Exception {
		super.setUp();

		AccountModel contractorAccountModel = egTestDataUtil.buildFakeContractorAccountModel();
		when( accountService.getAccountById(contractorAccountId)).thenReturn(contractorAccountModel);

		employee = egTestDataUtil.buildNewFakeEmployee();

		pres = egTestDataUtil.buildFakeProjectRoleEmployees(employee);
		for(ProjectRoleEmployee pre : pres){
			projectRole = pre.getProjectRole();
			project = projectRole.getProject();
			role = projectRole.getRole();
			project.setRoles(Arrays.asList(projectRole));
			break;
		}

		project.setSkills(egTestDataUtil.buildFakeProjectSkills(project));

	}

	private void masterInit() throws ReqdInfoMissingException {
		requestMap.put(MModels.MMODELS, MModels.newMModels());

		List<MOperations> mSkillsOperations = new ArrayList<>();mSkillsOperations.add(MOperations.COPY_ID);mSkillsOperations.add(MOperations.COPY_NAME);
		MModels.fetchSkillsManager().setmOperations(mSkillsOperations);

		List<MOperations> mRolesOperations = new ArrayList<>();mRolesOperations.add(MOperations.COPY_ID);mRolesOperations.add(MOperations.COPY_NAME);mRolesOperations.add(MOperations.ATTACH_SKILLS);
		MModels.fetchRolesManager().setmOperations(mRolesOperations);

		List<MOperations> mContractorOperations = new ArrayList<>();mContractorOperations.add(MOperations.COPY_ID);mContractorOperations.add(MOperations.COPY_NAME);
		MModels.fetchContractorManager().setmOperations(mContractorOperations);

		initCorpManager();

		initSiteManager();

		initEmployeeManager();

		initProjectManager();

	}

	private void initSiteManager() throws ReqdInfoMissingException {
		AccountModel accountModel = egTestDataUtil.buildFakeSiteAccountModel();
		siteReqdSkills = egTestDataUtil.buildFakeSiteReqdSkillsList();
		List<MOperations> mSiteOperations = new ArrayList<>();mSiteOperations.add(MOperations.COPY_ID);mSiteOperations.add(MOperations.COPY_NAME);mSiteOperations.add(MOperations.ATTACH_REQD_SKILLS);
		MModels.fetchSitesManager().setmOperations(mSiteOperations);
		MModels.fetchSitesManager().attachReqdSkills(siteAccountId, siteReqdSkills);
		MModels.fetchSitesManager().copySite(siteAccountId, accountModel);

	}

	private void initCorpManager() throws ReqdInfoMissingException {
		AccountModel accountModel = egTestDataUtil.buildFakeCorporateAccountModel();
		corpReqdSkills = egTestDataUtil.buildFakeCorporateReqdSkillsList();
		List<MOperations> mCorporateOperations = new ArrayList<>();mCorporateOperations.add(MOperations.COPY_ID);mCorporateOperations.add(MOperations.COPY_NAME);mCorporateOperations.add(MOperations.ATTACH_REQD_SKILLS);
		MModels.fetchCorporateManager().setmOperations(mCorporateOperations);
		MModels.fetchCorporateManager().attachReqdSkills(siteAccountId, corpReqdSkills);
		MModels.fetchCorporateManager().copySite(siteAccountId, accountModel);
	}

	private void initEmployeeManager() throws ReqdInfoMissingException {
		List<MOperations> mEmployeeOperations = new ArrayList<>();mEmployeeOperations.add(MOperations.COPY_ID);mEmployeeOperations.add(MOperations.COPY_NAME);mEmployeeOperations.add(MOperations.ATTACH_CONTRACTOR);mEmployeeOperations.add(MOperations.ATTACH_DOCUMENTATION);
		MModels.fetchContractorEmployeeManager().setmOperations(mEmployeeOperations);
	}

	private void initProjectManager() throws ReqdInfoMissingException {
		List<MOperations> mProjectsOperations = new ArrayList<>();mProjectsOperations.add(MOperations.COPY_ID);mProjectsOperations.add(MOperations.COPY_NAME);mProjectsOperations.add(MOperations.COPY_ACCOUNT_ID);mProjectsOperations.add(MOperations.ATTACH_ROLES);mProjectsOperations.add(MOperations.ATTACH_REQD_SKILLS);
		MModels.fetchProjectManager().setmOperations(mProjectsOperations);
	}

	@Test
	public void testAttachWithModel() throws Exception {
		masterInit();
		MProjectsManager mManager = MModels.fetchProjectManager();
		mManager.attachWithModel(project);
		assertNotNull(mManager.fetchModel(project.getId()));
	}

	@Test
	public void testCopyProject() throws Exception {
		masterInit();
		MProjectsManager mManager = MModels.fetchProjectManager();
		mManager.copyProject(project);

		MProjectsManager.MProject mProject = mManager.fetchModel(project.getId());

		assertNotNull(mProject.getAccountId());
		assertNotNull(mProject.getReqdSkills());
		assertNotNull(mProject.getRoles());
		assertNotNull(mProject.getId());
		assertNotNull(mProject.getName());
	}

	@Test
	public void testEvalProjectAssignments_calculateProjectRoleEmployeeAssignments() throws Exception {
		masterInit();

		List<MOperations> mStatusOperations = new ArrayList<>();mStatusOperations.add(MOperations.EVAL_ALL_SKILLS_STATUS);mStatusOperations.add(MOperations.COPY_NAME);
		MModels.fetchStatusManager().setmOperations(mStatusOperations);

		MProjectsManager mManager = MModels.fetchProjectManager();
		mManager.evalProjectAssignments(Arrays.asList(projectRole));

		MProjectsManager.MProject mProject = mManager.fetchModel(project.getId());
		Set<MContractorEmployeeManager.MContractorEmployee> mContractorEmployees = mProject.getAssignments().getEmployees();

		assertNotNull(mProject);
		assertNotNull(mContractorEmployees);
		assertTrue(mContractorEmployees.size() > 0);

		for(MContractorEmployeeManager.MContractorEmployee mContractorEmployee : mContractorEmployees){
			assertNotNull(mContractorEmployee.getEmployeeRoles());

			MEmployeeStatus mEmployeeStatus = mContractorEmployee.getEmployeeStatus();
			assertNotNull(mEmployeeStatus);

			Set<MEmployeeSkillStatus> mEmployeeSkillStatuses = mEmployeeStatus.getEmployeeSkillStatus();
			assertNotNull(mEmployeeSkillStatuses);

			assertTrue(mEmployeeSkillStatuses.size() > 0);

			for(MEmployeeSkillStatus mEmployeeStatusEval : mEmployeeSkillStatuses){
				assertNotNull(mEmployeeStatusEval.getStatus());
				assertNotNull(mEmployeeStatusEval.getSkill());
			}

		}
	}

	@Test
	public void testEvalProjectAssignments_calculateProjectEmployeeAssignments() throws Exception {
		masterInit();

		List<MOperations> mStatusOperations = new ArrayList<>();mStatusOperations.add(MOperations.EVAL_OVERALL_STATUS_ONLY);mStatusOperations.add(MOperations.COPY_NAME);
		MModels.fetchStatusManager().setmOperations(mStatusOperations);

		MProjectsManager mManager = MModels.fetchProjectManager();
		mManager.evalProjectAssignments(Arrays.asList(projectRole));

		MProjectsManager.MProject mProject = mManager.fetchModel(project.getId());
		Set<MContractorEmployeeManager.MContractorEmployee> mContractorEmployees = mProject.getAssignments().getEmployees();

		assertNotNull(mProject);
		assertNotNull(mContractorEmployees);
		assertTrue(mContractorEmployees.size() > 0);

		for(MContractorEmployeeManager.MContractorEmployee mContractorEmployee : mContractorEmployees){
			assertNotNull(mContractorEmployee.getEmployeeRoles());

			MEmployeeStatus mEmployeeStatus = mContractorEmployee.getEmployeeStatus();
			assertNotNull(mEmployeeStatus);

			assertNotNull(mEmployeeStatus.getOverallStatus());

			Set<MEmployeeSkillStatus> mEmployeeSkillStatuses = mEmployeeStatus.getEmployeeSkillStatus();
			assertNull(mEmployeeSkillStatuses);

		}
	}

}
