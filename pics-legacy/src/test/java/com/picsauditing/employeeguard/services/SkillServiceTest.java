package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.AccountSkillRoleDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.AccountSkillRoleBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class SkillServiceTest {

    private SkillService skillService;

    private static final int DELETE_SKILL_ID = 2;

    private static final int ACCOUNT_ID = 4567;
    private static final int APP_USER_ID = 123;
    private static final Date CREATED_DATE = new Date();
    private static final int ACCOUNT_SKILL_GROUP_ID = 5678;

    @Mock
    private AccountSkillDAO accountSkillDAO;
    @Mock
    private AccountSkillEmployeeService accountSkillEmployeeService;
	@Mock
	private AccountSkillRoleDAO accountSkillRoleDAO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        skillService = new SkillService();

        Whitebox.setInternalState(skillService, "accountSkillDAO", accountSkillDAO);
        Whitebox.setInternalState(skillService, "accountSkillEmployeeService", accountSkillEmployeeService);
		Whitebox.setInternalState(skillService, "accountSkillRoleDAO", accountSkillRoleDAO);
    }

    @Test
    public void testSave() {

//        verifyTest();
    }

    private void verifyTest() {


        verify(accountSkillDAO).save(any(AccountSkill.class));
        verify(accountSkillEmployeeService).linkEmployeesToSkill(any(AccountSkill.class), anyInt());
    }

    private AccountSkill buildAccountSkill() {
        AccountSkill accountSkill = new AccountSkill();
        accountSkill.setGroups(Arrays.asList(buildAccountSkillGroup()));
        return accountSkill;
    }

    private AccountSkillGroup buildAccountSkillGroup() {
        AccountSkillGroup accountSkillGroup = new AccountSkillGroup();
        accountSkillGroup.setId(ACCOUNT_SKILL_GROUP_ID);
        return accountSkillGroup;
    }

    @Test
    public void testUpdate() {

    }

    @Test
    public void testDelete() {
        AccountSkill accountSkill = new AccountSkill();
        when(accountSkillDAO.findSkillByAccount(DELETE_SKILL_ID, ACCOUNT_ID)).thenReturn(accountSkill);

        skillService.delete(Integer.toString(DELETE_SKILL_ID), ACCOUNT_ID, APP_USER_ID);

        assertEquals(APP_USER_ID, accountSkill.getDeletedBy());
        verify(accountSkillDAO).delete(accountSkill);
    }

    @Test
    public void testSearch() {
        when(accountSkillDAO.search("My Skill", ACCOUNT_ID)).thenReturn(Arrays.asList(new AccountSkill(), new AccountSkill()));

        List<AccountSkill> results = skillService.search("My Skill", ACCOUNT_ID);

        assertEquals(2, results.size());
    }

    @Test
    public void testSearch_EmptySearchTerm() {
        List<AccountSkill> result = skillService.search(Strings.EMPTY_STRING, ACCOUNT_ID);

        assertTrue(result.isEmpty());
    }

	@Test
	public void testGetAllProjectSkillsForEmployeeProjectRoles_EmptyProjectRoleMap() {
		Map<Project, List<AccountSkill>> result = skillService.getAllProjectSkillsForEmployeeProjectRoles(ACCOUNT_ID,
				Collections.<Project, Set<Role>>emptyMap());

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetSkillsForRoles_EmptyRoleSkillsMap() {
		Map<Role, List<AccountSkill>> result = skillService.getSkillsForRoles(ACCOUNT_ID,
				Collections.<Role>emptyList());

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetSkillsForRoles() {
		when(accountSkillRoleDAO.findSkillsByRoles(anyCollectionOf(Role.class)))
				.thenReturn(buildFakeAccountSkillRoles());

		Map<Role, List<AccountSkill>> result = skillService.getSkillsForRoles(ACCOUNT_ID,
				Collections.<Role>emptyList());

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	public List<AccountSkillRole> buildFakeAccountSkillRoles() {
		return Arrays.asList(
				new AccountSkillRoleBuilder()
						.skill(new AccountSkillBuilder(123, ACCOUNT_ID).name("Test Skill 1").skillType(SkillType.Training).build())
						.role(new RoleBuilder().accountId(ACCOUNT_ID).name("Test Role 1").build())
						.build(),

				new AccountSkillRoleBuilder()
						.skill(new AccountSkillBuilder(145, ACCOUNT_ID).name("Test Skill 2").skillType(SkillType.Training).build())
						.role(new RoleBuilder().accountId(ACCOUNT_ID).name("Test Role 1").build())
						.build()
		);
	}
}
