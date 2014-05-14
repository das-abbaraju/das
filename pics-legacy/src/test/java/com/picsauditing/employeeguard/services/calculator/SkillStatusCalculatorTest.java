package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.IntervalType;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SkillStatusCalculatorTest {

	private EGTestDataUtil egTestDataUtil = new EGTestDataUtil();


	@Test
	public void testCalculateStatusFromSkill_Expired() throws Exception {
		DateTime skillDocSaveDate = (new DateTime().minusDays(35));
		AccountSkillEmployee accountSkillEmployee = prepareAccountSkillEmployee(skillDocSaveDate.toDate());

		accountSkillEmployee.getSkill().setIntervalType(IntervalType.MONTH);

		SkillStatus result = SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee);

		assertEquals(SkillStatus.Expired, result);
	}

	@Test
	public void testCalculateStatusFromSkill_Expiring() throws Exception {
		DateTime skillDocSaveDate = (new DateTime().minusDays(3));
		AccountSkillEmployee accountSkillEmployee = prepareAccountSkillEmployee(skillDocSaveDate.toDate());

		accountSkillEmployee.getSkill().setIntervalType(IntervalType.WEEK);

		SkillStatus result = SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee);

		assertEquals(SkillStatus.Expiring, result);

	}

	@Test
	public void testCalculateStatusFromSkill_Valid() throws Exception {
		DateTime skillDocSaveDate = (new DateTime().minusDays(3));
		AccountSkillEmployee accountSkillEmployee = prepareAccountSkillEmployee(skillDocSaveDate.toDate());

		accountSkillEmployee.getSkill().setIntervalType(IntervalType.YEAR);
		// End date is not used.  Its intentionally populated to make sure its not
		accountSkillEmployee.setEndDate((new DateTime().plusDays(5).toDate()));

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

	private AccountSkillEmployee prepareAccountSkillEmployee(Date skillDocSaveDate){
		DateTime oneYrFromNowDate =new DateTime().plusDays(365);
		AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployeeBuilder()
						.startDate(skillDocSaveDate)
						.endDate(oneYrFromNowDate.toDate()) // End date is not used.  Its intentionally populated to make sure its not
						.build();

		accountSkillEmployee.setSkill(egTestDataUtil.buildNewFakeTrainingSkill());

		return accountSkillEmployee;
	}

	private List<AccountSkillEmployee> getFakeAccountSkillEmployeeList_Expired() {

		return Arrays.asList(
						egTestDataUtil.prepareExpiredAccountSkillEmployee(),
						egTestDataUtil.prepareExpiringAccountSkillEmployee(),
						egTestDataUtil.prepareCompletedAccountSkillEmployee(),
						egTestDataUtil.prepareCompletedAccountSkillEmployee(),
						egTestDataUtil.prepareExpiredAccountSkillEmployee(),
						egTestDataUtil.prepareExpiringAccountSkillEmployee(),
						null);
	}

	private List<AccountSkillEmployee> getFakeAccountSkillEmployeeList_Expiring() {
		return Arrays.asList(
						egTestDataUtil.prepareCompletedAccountSkillEmployee(),
						egTestDataUtil.prepareExpiringAccountSkillEmployee(),
						egTestDataUtil.prepareCompletedAccountSkillEmployee(),
						egTestDataUtil.prepareExpiringAccountSkillEmployee()
		);
	}

	private List<AccountSkillEmployee> getFakeAccountSkillEmployeeList_Completed() {
		return Arrays.asList(
						egTestDataUtil.prepareCompletedAccountSkillEmployee(),
						egTestDataUtil.prepareCompletedAccountSkillEmployee(),
						egTestDataUtil.prepareCompletedAccountSkillEmployee(),
						egTestDataUtil.prepareCompletedAccountSkillEmployee()
		);
	}


}
