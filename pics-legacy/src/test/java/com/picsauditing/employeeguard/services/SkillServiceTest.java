package com.picsauditing.employeeguard.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        skillService = new SkillService();

        Whitebox.setInternalState(skillService, "accountSkillDAO", accountSkillDAO);
        Whitebox.setInternalState(skillService, "accountSkillEmployeeService", accountSkillEmployeeService);
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

        verifySkillServiceDelete(accountSkill);
    }

    private void verifySkillServiceDelete(AccountSkill accountSkill) {
        assertEquals(APP_USER_ID, accountSkill.getDeletedBy());
        assertNotNull(accountSkill.getDeletedDate());

//        assertEquals(APP_USER_ID, accountSkill.getGroups().get(0).getDeletedBy());
//        assertNotNull(accountSkill.getGroups().get(0).getDeletedDate());

        verify(accountSkillDAO).save(accountSkill);
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
}
