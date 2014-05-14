package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class AccountSkillProfileBuilderTest {

	private static final int ID = 17828;
	private static final int ACCOUNT_ID = 1100;
	private static final Date START_DATE = DateBean.buildDate(1, 1, 2013);
	private static final Date END_DATE = DateBean.buildDate(2, 1, 2013);

	private static final Date CREATED_DATE = DateBean.buildDate(3, 1, 2013);
	private static final Date UPDATED_DATE = DateBean.buildDate(4, 1, 2013);
	private static final Date DELETED_DATE = DateBean.buildDate(5, 1, 2013);

	private static final int CREATED_USER = 123;
	private static final int UPDATED_USER = 456;
	private static final int DELETED_USER = 789;

	private static final String PROFILE_DOCUMENT_NAME = "Test Document Name";
	private static final String SKILL_NAME = "Test Skill Name";
	private static final String EMPLOYEE_FIRST_NAME = "Bob";
	private static final String EMPLOYEE_LAST_NAME = "Johnson";

	@Test
	public void testBuild() {
		AccountSkillProfile result = new AccountSkillProfileBuilder().id(ID)
				.accountSkill(new AccountSkillBuilder(ACCOUNT_ID).name(SKILL_NAME).build())
				.profile(new ProfileBuilder().firstName(EMPLOYEE_FIRST_NAME).lastName(EMPLOYEE_LAST_NAME).build())
				.profileDocument(new ProfileDocumentBuilder().name(PROFILE_DOCUMENT_NAME).build())
				.startDate(START_DATE).endDate(END_DATE)
				.createdBy(CREATED_USER).updatedBy(UPDATED_USER).deletedBy(DELETED_USER)
				.createdDate(CREATED_DATE).updatedDate(UPDATED_DATE).deletedDate(DELETED_DATE)
				.build();

		verify(result);
	}

	private void verify(AccountSkillProfile accountSkillProfile) {
		assertEquals(ID, accountSkillProfile.getId());
		assertEquals(PROFILE_DOCUMENT_NAME, accountSkillProfile.getProfileDocument().getName());
		assertEquals(SKILL_NAME, accountSkillProfile.getSkill().getName());
		assertEquals(EMPLOYEE_FIRST_NAME, accountSkillProfile.getProfile().getFirstName());
		assertEquals(EMPLOYEE_LAST_NAME, accountSkillProfile.getProfile().getLastName());

		assertEquals(START_DATE, accountSkillProfile.getStartDate());
		assertEquals(END_DATE, accountSkillProfile.getEndDate());
		assertEquals(CREATED_DATE, accountSkillProfile.getCreatedDate());
		assertEquals(UPDATED_DATE, accountSkillProfile.getUpdatedDate());
		assertEquals(DELETED_DATE, accountSkillProfile.getDeletedDate());
		assertEquals(CREATED_USER, accountSkillProfile.getCreatedBy());
		assertEquals(UPDATED_USER, accountSkillProfile.getUpdatedBy());
		assertEquals(DELETED_USER, accountSkillProfile.getDeletedBy());
	}

}
