package com.picsauditing.employeeguard.services;

import com.google.gson.Gson;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.ProfileBuilder;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.AccountType;
import com.picsauditing.employeeguard.models.factories.EmployeeSkillsModelFactory;
import com.picsauditing.employeeguard.process.ProfileSkillData;
import com.picsauditing.employeeguard.process.ProfileSkillStatusProcess;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class EmployeeSkillsModelServiceTest {

	public static final int CORPORATE_ID = 15;
	public static final AccountModel CORPORATE_ACCOUNT_MODEL = new AccountModel.Builder().id(CORPORATE_ID).name("Corp Account").accountType(AccountType.CORPORATE).build();
	public static final int SITE_ID = 67;
	public static final AccountModel SITE_ACCOUNT_MODEL = new AccountModel.Builder().id(SITE_ID).name("Site Account").accountType(AccountType.OPERATOR).build();
	public static final int CONTRACTOR_ID = 120;
	public static final AccountModel CONTRACTOR_ACCOUNT_MODEL = new AccountModel.Builder().id(CONTRACTOR_ID).name("Contractor Account").accountType(AccountType.CONTRACTOR).build();

	public static final Project TEST_PROJECT = new ProjectBuilder().accountId(SITE_ID).name("Test Project").build();
	public static final AccountSkill PROJECT_REQUIRED_SKILL_1 = new AccountSkillBuilder().accountId(CORPORATE_ID).name("Project Skill 1").build();
	public static final AccountSkill PROJECT_REQUIRED_SKILL_2 = new AccountSkillBuilder().accountId(CORPORATE_ID).name("Project Skill 2").build();
	public static final AccountSkill CORPORATE_REQUIRED_SKILL = new AccountSkillBuilder().accountId(CORPORATE_ID).name("Corp Required Skill").build();
	public static final AccountSkill SITE_REQUIRED_SKILL = new AccountSkillBuilder().accountId(CORPORATE_ID).name("Site Required Skill").build();
	public static final AccountSkill CONTRACTOR_REQUIRED_SKILL = new AccountSkillBuilder().accountId(CONTRACTOR_ID).name("Contractor RequiredSkill").build();

	// Class under test
	private EmployeeSkillsModelService employeeSkillsModelService;

	@Mock
	private ProfileSkillStatusProcess profileSkillStatusProcess;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeSkillsModelService = new EmployeeSkillsModelService();

		Whitebox.setInternalState(employeeSkillsModelService, "profileSkillStatusProcess", profileSkillStatusProcess);
	}

	@Test
	public void testBuildEmployeeSkillsModel() throws Exception {
		Profile fakeProfile = new ProfileBuilder().id(890).build();
		when(profileSkillStatusProcess.buildProfileSkillData(fakeProfile)).thenReturn(buildFakeProfileSkillData());

		EmployeeSkillsModelFactory.EmployeeSkillsModel result = employeeSkillsModelService
				.buildEmployeeSkillsModel(fakeProfile);

		verifyTestBuildEmployeeSkillsModel(result);
	}

	private void verifyTestBuildEmployeeSkillsModel(EmployeeSkillsModelFactory.EmployeeSkillsModel result) throws Exception {
		Approvals.verify(new Gson().toJson(result));
	}

	private ProfileSkillData buildFakeProfileSkillData() {
		ProfileSkillData profileSkillData = new ProfileSkillData();

		profileSkillData = addProjects(profileSkillData);
		profileSkillData = addAllProjectSkills(profileSkillData);
		profileSkillData = addSkillStatusMap(profileSkillData);
		profileSkillData = addSiteAccounts(profileSkillData);
		profileSkillData = addProjectStatuses(profileSkillData);
		profileSkillData = addAllProjectSkills(profileSkillData);
		profileSkillData = addAllRequiredSkills(profileSkillData);
		profileSkillData = addOverallStatus(profileSkillData);
		profileSkillData = addSiteStatuses(profileSkillData);

		return profileSkillData;
	}

	private ProfileSkillData addProjects(final ProfileSkillData profileSkillData) {
		profileSkillData.setProjects(new HashSet<>(Arrays.asList(TEST_PROJECT)));

		return profileSkillData;
	}

	private ProfileSkillData addAllProjectSkills(final ProfileSkillData profileSkillData) {
		profileSkillData.setAllProjectSkills(new HashMap<Project, Set<AccountSkill>>() {{

			put(TEST_PROJECT, new HashSet<>(Arrays.asList(PROJECT_REQUIRED_SKILL_1, PROJECT_REQUIRED_SKILL_2)));

		}});

		return profileSkillData;
	}

	private ProfileSkillData addSkillStatusMap(final ProfileSkillData profileSkillData) {
		profileSkillData.setSkillStatusMap(new HashMap<AccountSkill, SkillStatus>() {{

			put(PROJECT_REQUIRED_SKILL_1, SkillStatus.Completed);
			put(PROJECT_REQUIRED_SKILL_2, SkillStatus.Completed);
			put(CORPORATE_REQUIRED_SKILL, SkillStatus.Expiring);
			put(SITE_REQUIRED_SKILL, SkillStatus.Completed);
			put(CONTRACTOR_REQUIRED_SKILL, SkillStatus.Completed);

		}});

		return profileSkillData;
	}

	private ProfileSkillData addSiteAccounts(final ProfileSkillData profileSkillData) {
		profileSkillData.setSiteAccounts(new HashMap<Integer, AccountModel>() {{

			put(CORPORATE_ID, CORPORATE_ACCOUNT_MODEL);
			put(SITE_ID, SITE_ACCOUNT_MODEL);

		}});

		return profileSkillData;
	}

	private ProfileSkillData addProjectStatuses(final ProfileSkillData profileSkillData) {
		profileSkillData.setProjectStatuses(new HashMap<Project, SkillStatus>() {{

			put(TEST_PROJECT, SkillStatus.Completed);

		}});

		return profileSkillData;
	}

	private ProfileSkillData addAllRequiredSkills(final ProfileSkillData profileSkillData) {
		profileSkillData.setAllRequiredSkills(new HashMap<AccountModel, Set<AccountSkill>>() {{

			put(CORPORATE_ACCOUNT_MODEL, new HashSet<>(Arrays.asList(CORPORATE_REQUIRED_SKILL)));
			put(SITE_ACCOUNT_MODEL, new HashSet<>(Arrays.asList(SITE_REQUIRED_SKILL, CORPORATE_REQUIRED_SKILL)));
			put(CONTRACTOR_ACCOUNT_MODEL, new HashSet<>(Arrays.asList(CONTRACTOR_REQUIRED_SKILL)));

		}});

		return profileSkillData;
	}

	private ProfileSkillData addOverallStatus(final ProfileSkillData profileSkillData) {
		profileSkillData.setOverallStatus(SkillStatus.Expiring);

		return profileSkillData;
	}

	private ProfileSkillData addSiteStatuses(final ProfileSkillData profileSkillData) {
		profileSkillData.setSiteStatuses(new HashMap<AccountModel, SkillStatus>() {{

			put(CORPORATE_ACCOUNT_MODEL, SkillStatus.Expiring);
			put(SITE_ACCOUNT_MODEL, SkillStatus.Completed);
			put(CONTRACTOR_ACCOUNT_MODEL, SkillStatus.Completed);

		}});

		return profileSkillData;
	}
}
