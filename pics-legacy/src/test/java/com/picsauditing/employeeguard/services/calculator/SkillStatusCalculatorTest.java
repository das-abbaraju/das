package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SkillStatusCalculatorTest {

  @Test(expected = IllegalArgumentException.class)
  public void testCalculateStatusRollUp_ExpiredWhenEmployeeHasntProvidedAnyDocumentation() {
    List<AccountSkillEmployee> accountSkillEmployees = Collections.emptyList();
    SkillStatus result = SkillStatusCalculator.calculateStatusRollUp(accountSkillEmployees);
    assertEquals(SkillStatus.Expired, result);
  }

	@Test
	public void testCalculateStatusFromSkill_Expired() throws Exception {
        AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployeeBuilder()
                .endDate(DateBean.addDays(DateBean.today(), -1))
                .build();

        SkillStatus result = SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee);

        assertEquals(SkillStatus.Expired, result);
	}

	@Test
	public void testCalculateStatusFromSkill_Expiring() throws Exception {
        AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployeeBuilder()
                .endDate(DateBean.addDays(DateBean.today(), 1))
                .build();

        SkillStatus result = SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee);

        assertEquals(SkillStatus.Expiring, result);
	}

	@Test
	public void testCalculateStatusFromSkill_Valid() throws Exception {
        AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployeeBuilder()
                .endDate(DateBean.addYears(DateBean.today(), 1))
                .build();

        SkillStatus result = SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee);

		assertEquals(SkillStatus.Completed, result);
	}

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateStatusRollUp_EmptyList() {
        List<AccountSkillEmployee> accountSkillEmployees = null;

        SkillStatusCalculator.calculateStatusRollUp(accountSkillEmployees);
    }

    @Test
    public void testCalculateStatusRollUp_LowestIsExpired() {
        List<AccountSkillEmployee> accountSkillEmployees = getFakeAccountSkillEmployeeList_Expired();

        SkillStatus result = SkillStatusCalculator.calculateStatusRollUp(accountSkillEmployees);

        assertEquals(SkillStatus.Expired, result);
    }

    @Test
    public void testCalculateStatusRollUp_LowestIsExpiring() {
        List<AccountSkillEmployee> accountSkillEmployees = getFakeAccountSkillEmployeeList_Expiring();

        SkillStatus result = SkillStatusCalculator.calculateStatusRollUp(accountSkillEmployees);

        assertEquals(SkillStatus.Expiring, result);
    }

    @Test
    public void testCalculateStatusRollUp_LowestIsCompleted() {
        List<AccountSkillEmployee> accountSkillEmployees = getFakeAccountSkillEmployeeList_Completed();

        SkillStatus result = SkillStatusCalculator.calculateStatusRollUp(accountSkillEmployees);

        assertEquals(SkillStatus.Completed, result);
    }

    private List<AccountSkillEmployee> getFakeAccountSkillEmployeeList_Expired() {
        return Arrays.asList(
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 50)).build(),
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), -40)).build(),
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 100)).build(),
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 40)).build(),
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 2)).build(),
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 75)).build(),
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 6)).build(),
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), -45)).build(),
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 2)).build(),
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 5)).build(),
                new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 5)).build(),
                null);
    }

  private List<AccountSkillEmployee> getFakeAccountSkillEmployeeList_Expiring() {
    return Arrays.asList(
            new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 6)).build(),
            new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 50)).build(),
            new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 100)).build(),
            new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 2)).build(),
            new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 40)).build(),
            new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 75)).build()
            );
  }

  private List<AccountSkillEmployee> getFakeAccountSkillEmployeeList_Completed() {
    return Arrays.asList(
            new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 50)).build(),
            new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 100)).build(),
            new AccountSkillEmployeeBuilder().endDate(DateBean.addDays(DateBean.today(), 40)).build());
  }


}
