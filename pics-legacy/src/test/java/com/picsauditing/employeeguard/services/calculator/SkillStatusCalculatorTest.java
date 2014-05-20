package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.IntervalType;
import com.picsauditing.employeeguard.entities.builders.AccountSkillProfileBuilder;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SkillStatusCalculatorTest {

	private EGTestDataUtil egTestDataUtil = new EGTestDataUtil();


	@Test
	public void testCalculateStatusFromSkill_Expired() throws Exception {
		DateTime skillDocSaveDate = (new DateTime().minusDays(35));
		AccountSkillProfile accountSkillProfile = prepareAccountSkillProfile(skillDocSaveDate.toDate());

		accountSkillProfile.getSkill().setIntervalType(IntervalType.MONTH);

		SkillStatus result = SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile);

		assertEquals(SkillStatus.Expired, result);
	}

	@Test
	public void testCalculateStatusFromSkill_Expiring() throws Exception {
		DateTime skillDocSaveDate = (new DateTime().minusDays(3));
		AccountSkillProfile accountSkillProfile = prepareAccountSkillProfile(skillDocSaveDate.toDate());

		accountSkillProfile.getSkill().setIntervalType(IntervalType.WEEK);

		SkillStatus result = SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile);

		assertEquals(SkillStatus.Expiring, result);

	}

	@Test
	public void testCalculateStatusFromSkill_Valid() throws Exception {
		DateTime skillDocSaveDate = (new DateTime().minusDays(3));
		AccountSkillProfile accountSkillProfile = prepareAccountSkillProfile(skillDocSaveDate.toDate());

		accountSkillProfile.getSkill().setIntervalType(IntervalType.YEAR);
		// End date is not used.  Its intentionally populated to make sure its not
		accountSkillProfile.setEndDate((new DateTime().plusDays(5).toDate()));

		SkillStatus result = SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile);

		assertEquals(SkillStatus.Completed, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateStatusRollUp_EmptyList() {
		List<AccountSkillProfile> accountSkillProfiles = null;

		SkillStatusCalculator.calculateStatusRollUp(accountSkillProfiles);
	}

	@Test
	public void testCalculateStatusRollUp_LowestIsExpired() {
		List<AccountSkillProfile> accountSkillProfiles = getFakeAccountSkillProfileList_Expired();

		SkillStatus result = SkillStatusCalculator.calculateStatusRollUp(accountSkillProfiles);

		assertEquals(SkillStatus.Expired, result);
	}

	@Test
	public void testCalculateStatusRollUp_LowestIsExpiring() {
		List<AccountSkillProfile> accountSkillProfiles = getFakeAccountSkillProfileList_Expiring();

		SkillStatus result = SkillStatusCalculator.calculateStatusRollUp(accountSkillProfiles);

		assertEquals(SkillStatus.Expiring, result);
	}

	@Test
	public void testCalculateStatusRollUp_LowestIsCompleted() {
		List<AccountSkillProfile> accountSkillProfiles = getFakeAccountSkillProfileList_Completed();

		SkillStatus result = SkillStatusCalculator.calculateStatusRollUp(accountSkillProfiles);

		assertEquals(SkillStatus.Completed, result);
	}

	private AccountSkillProfile prepareAccountSkillProfile(Date skillDocSaveDate){
		DateTime oneYrFromNowDate =new DateTime().plusDays(365);
		AccountSkillProfile accountSkillProfile = new AccountSkillProfileBuilder()
						.startDate(skillDocSaveDate)
						.endDate(oneYrFromNowDate.toDate()) // End date is not used.  Its intentionally populated to make sure its not
						.build();

		accountSkillProfile.setSkill(egTestDataUtil.buildNewFakeTrainingSkill());

		return accountSkillProfile;
	}

	private List<AccountSkillProfile> getFakeAccountSkillProfileList_Expired() {

		return Arrays.asList(
						egTestDataUtil.prepareExpiredAccountSkillProfile(),
						egTestDataUtil.prepareExpiringAccountSkillProfile(),
						egTestDataUtil.prepareCompletedAccountSkillProfile(),
						egTestDataUtil.prepareCompletedAccountSkillProfile(),
						egTestDataUtil.prepareExpiredAccountSkillProfile(),
						egTestDataUtil.prepareExpiringAccountSkillProfile(),
						null);
	}

	private List<AccountSkillProfile> getFakeAccountSkillProfileList_Expiring() {
		return Arrays.asList(
						egTestDataUtil.prepareCompletedAccountSkillProfile(),
						egTestDataUtil.prepareExpiringAccountSkillProfile(),
						egTestDataUtil.prepareCompletedAccountSkillProfile(),
						egTestDataUtil.prepareExpiringAccountSkillProfile()
		);
	}

	private List<AccountSkillProfile> getFakeAccountSkillProfileList_Completed() {
		return Arrays.asList(
						egTestDataUtil.prepareCompletedAccountSkillProfile(),
						egTestDataUtil.prepareCompletedAccountSkillProfile(),
						egTestDataUtil.prepareCompletedAccountSkillProfile(),
						egTestDataUtil.prepareCompletedAccountSkillProfile()
		);
	}


}
