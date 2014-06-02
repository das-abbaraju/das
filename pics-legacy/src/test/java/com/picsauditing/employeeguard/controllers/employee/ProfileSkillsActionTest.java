package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.builders.ProfileBuilder;
import com.picsauditing.employeeguard.models.factories.EmployeeSkillsModelFactory;
import com.picsauditing.employeeguard.services.EmployeeSkillsModelService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class ProfileSkillsActionTest extends PicsActionTest {

	// Class under test
	private ProfileSkillsAction profileSkillsAction;

	@Mock
	private EmployeeSkillsModelService employeeSkillsModelService;
	@Mock
	private ProfileEntityService profileEntityService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		profileSkillsAction = new ProfileSkillsAction();

		super.setUp(profileSkillsAction);

		Whitebox.setInternalState(profileSkillsAction, "employeeSkillsModelService", employeeSkillsModelService);
		Whitebox.setInternalState(profileSkillsAction, "profileEntityService", profileEntityService);
	}

	@Test
	public void testSkills() {
		setupTestSkills();

		String result = profileSkillsAction.skills();

		verifyTestSkills(result);
	}

	private void setupTestSkills() {
		Profile fakeProfile = new ProfileBuilder().id(34).build();
		when(profileEntityService.findByAppUserId(anyInt())).thenReturn(fakeProfile);

		EmployeeSkillsModelFactory.EmployeeSkillsModel fakeEmployeeSkillsModel =
				new EmployeeSkillsModelFactory().create(SkillStatus.Completed, null);
		when(employeeSkillsModelService.buildEmployeeSkillsModel(fakeProfile)).thenReturn(fakeEmployeeSkillsModel);
	}

	private void verifyTestSkills(String result) {
		assertEquals(PicsActionSupport.JSON_STRING, result);
		assertEquals("{\"status\":\"Completed\"}", profileSkillsAction.getJsonString());
	}
}
