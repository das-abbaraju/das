package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SkillStatusCalculatorTest {
	@Mock
	private AccountSkillEmployee accountSkillEmployee;

	Calendar calendar;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		calendar = Calendar.getInstance();
	}

	@Test
	public void testCalculateStatusFromSkill_Expired() throws Exception {
		calendar.set(2000, 0, 1);

		when(accountSkillEmployee.getEndDate()).thenReturn(calendar.getTime());

		assertEquals(SkillStatus.Expired, SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee));
	}

	@Test
	public void testCalculateStatusFromSkill_Expiring() throws Exception {
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		when(accountSkillEmployee.getEndDate()).thenReturn(calendar.getTime());

		assertEquals(SkillStatus.Expiring, SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee));
	}

	@Test
	public void testCalculateStatusFromSkill_Valid() throws Exception {
		calendar.add(Calendar.YEAR, 1);

		when(accountSkillEmployee.getEndDate()).thenReturn(calendar.getTime());

		assertEquals(SkillStatus.Complete, SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee));
	}
}
