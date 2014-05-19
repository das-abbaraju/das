package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.status.ExpirationCalculator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpirationCalculatorTest {
	public static final int INTERVAL_PERIOD = 3;
	@Mock
	private AccountSkill accountSkill;
	@Mock
	private AccountSkillProfile accountSkillProfile;

	private Calendar expectedExpirationDate;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(accountSkillProfile.getSkill()).thenReturn(accountSkill);
		expectedExpirationDate = Calendar.getInstance();
		when(accountSkillProfile.getStartDate()).thenReturn(expectedExpirationDate.getTime());
	}

	@Test
	public void testCalculateExpirationDate_Training_Day() throws Exception {
		when(accountSkill.getSkillType()).thenReturn(SkillType.Training);
		when(accountSkill.getIntervalType()).thenReturn(IntervalType.DAY);
		when(accountSkill.getIntervalPeriod()).thenReturn(INTERVAL_PERIOD);

		Date calculatedExpiration = ExpirationCalculator.calculateExpirationDate(accountSkillProfile);
		Calendar calculatedExpirationCalendar = Calendar.getInstance();
		calculatedExpirationCalendar.setTime(calculatedExpiration);

		expectedExpirationDate.add(Calendar.DAY_OF_YEAR, INTERVAL_PERIOD);

		assertEquals(expectedExpirationDate.get(Calendar.YEAR), calculatedExpirationCalendar.get(Calendar.YEAR));
		assertEquals(expectedExpirationDate.get(Calendar.MONTH), calculatedExpirationCalendar.get(Calendar.MONTH));
		assertEquals(expectedExpirationDate.get(Calendar.DAY_OF_YEAR), calculatedExpirationCalendar.get(Calendar.DAY_OF_YEAR));
	}

	@Test
	public void testCalculateExpirationDate_Training_Week() throws Exception {
		when(accountSkill.getSkillType()).thenReturn(SkillType.Training);
		when(accountSkill.getIntervalType()).thenReturn(IntervalType.WEEK);
		when(accountSkill.getIntervalPeriod()).thenReturn(INTERVAL_PERIOD);

		Date expiration = ExpirationCalculator.calculateExpirationDate(accountSkillProfile);
		Calendar expirationCalendar = Calendar.getInstance();
		expirationCalendar.setTime(expiration);

		expectedExpirationDate.add(Calendar.WEEK_OF_YEAR, INTERVAL_PERIOD);

		assertEquals(expectedExpirationDate.get(Calendar.YEAR), expirationCalendar.get(Calendar.YEAR));
		assertEquals(expectedExpirationDate.get(Calendar.MONTH), expirationCalendar.get(Calendar.MONTH));
		assertEquals(expectedExpirationDate.get(Calendar.DAY_OF_YEAR), expirationCalendar.get(Calendar.DAY_OF_YEAR));
	}

	@Test
	public void testCalculateExpirationDate_Training_Month() throws Exception {
		when(accountSkill.getSkillType()).thenReturn(SkillType.Training);
		when(accountSkill.getIntervalType()).thenReturn(IntervalType.MONTH);
		when(accountSkill.getIntervalPeriod()).thenReturn(INTERVAL_PERIOD);

		Date expiration = ExpirationCalculator.calculateExpirationDate(accountSkillProfile);
		Calendar expirationCalendar = Calendar.getInstance();
		expirationCalendar.setTime(expiration);

		expectedExpirationDate.add(Calendar.MONTH, INTERVAL_PERIOD);

		assertEquals(expectedExpirationDate.get(Calendar.YEAR), expirationCalendar.get(Calendar.YEAR));
		assertEquals(expectedExpirationDate.get(Calendar.MONTH), expirationCalendar.get(Calendar.MONTH));
		assertEquals(expectedExpirationDate.get(Calendar.DAY_OF_MONTH), expirationCalendar.get(Calendar.DAY_OF_MONTH));
	}

	@Test
	public void testCalculateExpirationDate_Training_Year() throws Exception {
		when(accountSkill.getSkillType()).thenReturn(SkillType.Training);
		when(accountSkill.getIntervalType()).thenReturn(IntervalType.YEAR);
		when(accountSkill.getIntervalPeriod()).thenReturn(INTERVAL_PERIOD);

		Date expiration = ExpirationCalculator.calculateExpirationDate(accountSkillProfile);
		Calendar expirationCalendar = Calendar.getInstance();
		expirationCalendar.setTime(expiration);

		expectedExpirationDate.add(Calendar.YEAR, INTERVAL_PERIOD);

		assertEquals(expectedExpirationDate.get(Calendar.YEAR), expirationCalendar.get(Calendar.YEAR));
		assertEquals(expectedExpirationDate.get(Calendar.MONTH), expirationCalendar.get(Calendar.MONTH));
		assertEquals(expectedExpirationDate.get(Calendar.DAY_OF_YEAR), expirationCalendar.get(Calendar.DAY_OF_YEAR));
	}

	@Test
	public void testCalculateExpirationDate_Training_NotApplicable() throws Exception {
		when(accountSkill.getSkillType()).thenReturn(SkillType.Training);
		when(accountSkill.getIntervalType()).thenReturn(IntervalType.NOT_APPLICABLE);
		when(accountSkill.getIntervalPeriod()).thenReturn(INTERVAL_PERIOD);

		Date expiration = ExpirationCalculator.calculateExpirationDate(accountSkillProfile);
		Calendar expirationCalendar = Calendar.getInstance();
		expirationCalendar.setTime(expiration);

		assertEquals(4000, expirationCalendar.get(Calendar.YEAR));
		assertEquals(0, expirationCalendar.get(Calendar.MONTH));
		assertEquals(1, expirationCalendar.get(Calendar.DAY_OF_YEAR));
	}

	@Test
	public void testCalculateExpirationDate_Certificate() throws Exception {
		ProfileDocument profileDocument = mock(ProfileDocument.class);
		when(profileDocument.getEndDate()).thenReturn(expectedExpirationDate.getTime());

		when(accountSkill.getSkillType()).thenReturn(SkillType.Certification);
		when(accountSkillProfile.getProfileDocument()).thenReturn(profileDocument);
		profileDocument.setEndDate(expectedExpirationDate.getTime());

		assertEquals(expectedExpirationDate.getTime(), ExpirationCalculator.calculateExpirationDate(accountSkillProfile));
	}
}
