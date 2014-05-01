package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.IntervalType;
import com.picsauditing.employeeguard.entities.SkillType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AccountSkillBuilderTest {

    // this is a very unlikely setup, but is being utilized to test to verify every field is set
    private static final int ID = 123;
    private static final int ACCOUNT_ID = 1100;
    private static final String NAME = "Advanced Welding";
    private static final String DESCRIPTION = "Advanced Welding skill description";
    private static final SkillType SKILL_TYPE = SkillType.Certification;
    private static final int INTERVAL_PERIOD = 45;
    private static final IntervalType INTERVAL_TYPE = IntervalType.DAY;
    private static final List<Integer> GROUPS = Collections.unmodifiableList(Arrays.asList(new Integer(25),
            new Integer(56), new Integer(99)));

    @Test
    public void testBuild() {
        AccountSkill accountSkill = new AccountSkillBuilder(ID, ACCOUNT_ID).name(NAME).description(DESCRIPTION)
                .skillType(SKILL_TYPE).intervalPeriod(INTERVAL_PERIOD).intervalType(INTERVAL_TYPE)
                .groups(GROUPS.toArray(new Integer[0])).build();

        verifyAccountSkill(accountSkill);
    }

    private void verifyAccountSkill(AccountSkill accountSkill) {
        assertEquals(ID, accountSkill.getId());
        assertEquals(ACCOUNT_ID, accountSkill.getAccountId());
        assertEquals(NAME, accountSkill.getName());
        assertEquals(DESCRIPTION, accountSkill.getDescription());
        assertEquals(SKILL_TYPE, accountSkill.getSkillType());
        assertEquals(INTERVAL_PERIOD, accountSkill.getIntervalPeriod());
        assertEquals(INTERVAL_TYPE, accountSkill.getIntervalType());

        verifyGroups(accountSkill);
    }

    private void verifyGroups(AccountSkill accountSkill) {
        int index = 0;
        for (AccountSkillGroup accountSkillGroup : accountSkill.getGroups()) {
            assertEquals(GROUPS.get(index), (Integer)accountSkillGroup.getGroup().getId());
            assertEquals(ACCOUNT_ID, accountSkillGroup.getGroup().getAccountId());
            assertEquals(accountSkill, accountSkillGroup.getSkill());
            index++;
        }
    }
}
