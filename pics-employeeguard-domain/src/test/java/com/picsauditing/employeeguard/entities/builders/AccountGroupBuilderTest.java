package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AccountGroupBuilderTest {

    private static final int ID = 123;
    private static final int ACCOUNT_ID = 1100;
    private static final String NAME = "Group Name";
    private static final String DESCRIPTION = "Group Description";

    private static final List<Employee> EMPLOYEE_LIST = Collections.unmodifiableList(Arrays.asList(new Employee(),
            new Employee(), new Employee()));
    private static final List<AccountSkill> SKILL_LIST = Collections.unmodifiableList(Arrays.asList(new AccountSkill(),
            new AccountSkill(), new AccountSkill()));

    @Test
    public void testBuild() {
        AccountGroup group = new AccountGroupBuilder(ID, ACCOUNT_ID).description(DESCRIPTION).name(NAME).employees(EMPLOYEE_LIST).skills(SKILL_LIST).build();

        verifyAccountGroup(group);
    }

    private void verifyAccountGroup(AccountGroup group) {
        assertEquals(ID, group.getId());
        assertEquals(ACCOUNT_ID, group.getAccountId());
        assertEquals(NAME, group.getName());
        assertEquals(DESCRIPTION, group.getDescription());

        verifyEmployees(group);
        verifySkills(group);
    }

    private void verifyEmployees(AccountGroup group) {
        int index = 0;
        for (AccountGroupEmployee accountGroupEmployee : group.getEmployees()) {
            assertEquals(EMPLOYEE_LIST.get(index), accountGroupEmployee.getEmployee());
            index++;
        }
    }

    private void verifySkills(AccountGroup group) {
        int index = 0;
        for (AccountSkillGroup accountSkillGroup : group.getSkills()) {
            assertEquals(SKILL_LIST.get(index), accountSkillGroup.getSkill());
            index++;
        }
    }
}
