package com.picsauditing.employeeguard.process;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static com.picsauditing.employeeguard.EGTestDataUtil.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProcessHelperTest {

	// class under test
	private ProcessHelper processHelper;

	private EGTestDataUtil egTestDataUtil;

	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private GroupEntityService groupEntityService;
	@Mock
	private ProjectEntityService projectEntityService;
	@Mock
	private RoleEntityService roleEntityService;
	@Mock
	private SkillEntityService skillEntityService;

	@Before
	public void setUp() throws Exception {
		processHelper = new ProcessHelper();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(processHelper, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(processHelper, "groupEntityService", groupEntityService);
		Whitebox.setInternalState(processHelper, "projectEntityService", projectEntityService);
		Whitebox.setInternalState(processHelper, "roleEntityService", roleEntityService);
		Whitebox.setInternalState(processHelper, "skillEntityService", skillEntityService);
	}

	@Test
	public void testAllProjectSkills() throws Exception {
		Map<Project, Set<AccountSkill>> result = processHelper.allProjectSkills(FAKE_PROJECT_SET,
				PROJECT_REQUIRED_SKILLS_MAP, PROJECT_ROLES_MAP, ROLE_SKILLS_MAP);

		verifyTestAllProjectSkills(result);
	}

	private void verifyTestAllProjectSkills(final Map<Project, Set<AccountSkill>> result) {
		assertTrue(Utilities.mapsAreEqual(new HashMap<Project, Set<AccountSkill>>() {{
			put(PROJECT_NO_SKILLS, Collections.<AccountSkill>emptySet());

			put(PROJECT_NO_SKILLS_NO_ROLES, Collections.<AccountSkill>emptySet());

			put(PROJECT_WITH_SKILLS, new HashSet<>(Arrays.asList(PROJECT_REQUIRED_SKILL_1, PROJECT_REQUIRED_SKILL_2,
					SKILL_FOR_ROLE_WITH_SKILLS)));
		}},

				result));
	}

	@Test
	public void testGetProjectRequiredSkills() throws Exception {
		when(skillEntityService.getRequiredSkillsForProjects(FAKE_PROJECT_SET)).thenReturn(PROJECT_REQUIRED_SKILLS_MAP);

		Map<Project, Set<AccountSkill>> result = processHelper.getProjectRequiredSkills(FAKE_PROJECT_SET);

		verifyTestGetProjectRequiredSkills(result);
	}

	private void verifyTestGetProjectRequiredSkills(final Map<Project, Set<AccountSkill>> result) {
		assertTrue(Utilities.mapsAreEqual(new HashMap<Project, Set<AccountSkill>>() {{
			put(PROJECT_NO_SKILLS, Collections.<AccountSkill>emptySet());

			put(PROJECT_NO_SKILLS_NO_ROLES, Collections.<AccountSkill>emptySet());

			put(PROJECT_WITH_SKILLS, new HashSet<>(Arrays.asList(PROJECT_REQUIRED_SKILL_1, PROJECT_REQUIRED_SKILL_2)));
		}},

				result));
	}

	@Test
	public void testGetRoleSkills() throws Exception {
		Set<Role> fakeRoles = new HashSet<>(Arrays.asList(ROLE_NO_SKILLS, ROLE_WITH_SKILLS));
		when(skillEntityService.getSkillsForRoles(fakeRoles)).thenReturn(ROLE_SKILLS_MAP);

		Map<Role, Set<AccountSkill>> result = processHelper.getRoleSkills(fakeRoles);

		verifyTestGetRoleSkills(result);
	}

	private void verifyTestGetRoleSkills(final Map<Role, Set<AccountSkill>> result) {
		assertTrue(Utilities.mapsAreEqual(new HashMap<Role, Set<AccountSkill>>() {{

			put(ROLE_NO_SKILLS, new HashSet<AccountSkill>());

			put(ROLE_WITH_SKILLS, new HashSet<>(Arrays.asList(SKILL_FOR_ROLE_WITH_SKILLS)));

		}},
				result));
	}

	@Test
	public void testGetGroupSkills() throws Exception {
		Profile fakeProfile = new Profile();
		when(groupEntityService.getGroupSkillsForProfile(fakeProfile)).thenReturn(GROUP_SKILLS_MAP);

		Map<Group, Set<AccountSkill>> result = processHelper.getGroupSkills(fakeProfile);

		assertTrue(Utilities.mapsAreEqual(GROUP_SKILLS_MAP, result));
	}
}
