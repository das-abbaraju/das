package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.builders.ProfileBuilder;
import com.picsauditing.employeeguard.models.factories.EmployeeSkillsModelFactory;
import com.picsauditing.employeeguard.process.ProfileSkillData;
import com.picsauditing.employeeguard.process.ProfileSkillStatusProcess;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.when;

public class EmployeeSkillsModelServiceTest {

	// Class under test
	private EmployeeSkillsModelService employeeSkillsModelService;

	@Mock
	private ProfileSkillStatusProcess profileSkillStatusProcess;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		profileSkillStatusProcess = new ProfileSkillStatusProcess();

		Whitebox.setInternalState(employeeSkillsModelService, "profileSkillStatusProcess", profileSkillStatusProcess);
	}

	@Test
	public void testBuildEmployeeSkillsModel() {
		Profile fakeProfile = new ProfileBuilder().id(890).build();
		when(profileSkillStatusProcess.buildProfileSkillData(fakeProfile)).thenReturn(buildFakeProfileSkillData());

		EmployeeSkillsModelFactory.EmployeeSkillsModel result = employeeSkillsModelService
				.buildEmployeeSkillsModel(fakeProfile);

		verifyTestBuildEmployeeSkillsModel(result);
	}

	private void verifyTestBuildEmployeeSkillsModel(EmployeeSkillsModelFactory.EmployeeSkillsModel result) {
		//To change body of created methods use File | Settings | File Templates.
	}

	private ProfileSkillData buildFakeProfileSkillData() {
		ProfileSkillData profileSkillData = new ProfileSkillData();

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

	private ProfileSkillData addAllProjectSkills(final ProfileSkillData profileSkillData) {
		return profileSkillData;
	}

	private ProfileSkillData addSkillStatusMap(final ProfileSkillData profileSkillData) {
		return profileSkillData;
	}

	private ProfileSkillData addSiteAccounts(final ProfileSkillData profileSkillData) {
		return profileSkillData;
	}

	private ProfileSkillData addProjectStatuses(final ProfileSkillData profileSkillData) {
		return profileSkillData;
	}

	private ProfileSkillData addAllRequiredSkills(final ProfileSkillData profileSkillData) {
		return profileSkillData;
	}

	private ProfileSkillData addOverallStatus(final ProfileSkillData profileSkillData) {
		profileSkillData.setOverallStatus(SkillStatus.Expiring);

		return profileSkillData;
	}

	private ProfileSkillData addSiteStatuses(final ProfileSkillData profileSkillData) {
		return profileSkillData;
	}
}
