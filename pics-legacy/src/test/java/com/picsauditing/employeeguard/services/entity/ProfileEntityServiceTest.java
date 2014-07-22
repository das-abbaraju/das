package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.daos.ProfileDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.builders.ProfileBuilder;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.ENTITY_ID;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileEntityServiceTest {

	public static final String PROFILE_PHONE = "555-555-5555";
	public static final String PROFILE_FIRST_NAME = "Bob";
	public static final String PROFILE_LAST_NAME = "Smith";
	private static final String PROFILE_EMAIL = "my_email@test.com";

	private static final int PROFILE_UPDATE_ID = 123;
	private static final int USER_ID = 45;

	// Class under test
	private ProfileEntityService profileEntityService;

	@Mock
	private ProfileDAO profileDAO;

	@Before
	public void setUp() throws Exception {
		profileEntityService = new ProfileEntityService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(profileEntityService, "profileDAO", profileDAO);
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		profileEntityService.find(null);
	}

	@Test
	public void testFind() throws Exception {
		Profile expected = buildFakeProfile();

		when(profileDAO.find(expected.getId())).thenReturn(expected);

		Profile result = profileEntityService.find(expected.getId());

		assertNotNull(result);
		assertEquals(expected.getId(), result.getId());
	}

	@Test
	public void testSave() throws Exception {
		Profile fakeProfile = setupTestSave();

		Profile result = profileEntityService.save(fakeProfile, new EntityAuditInfo.Builder().appUserId(USER_ID)
				.timestamp(DateBean.today()).build());

		verifyTestSave(fakeProfile, result);
	}

	private Profile setupTestSave() {
		Profile fakeProfile = buildFakeProfile();

		when(profileDAO.save(fakeProfile)).thenReturn(fakeProfile);

		return fakeProfile;
	}

	private void verifyTestSave(Profile fakeProfile, Profile result) {
		verify(profileDAO).save(fakeProfile);
		assertEquals(USER_ID, result.getCreatedBy());
		assertNotNull(result.getCreatedDate());
		assertNull(result.getUpdatedDate());
		assertNotNull(fakeProfile.getSlug());
	}

	@Test
	public void testUpdate() throws Exception {
		Profile fakeProfile = buildFakeProfile();
		fakeProfile.setFirstName("First");
		fakeProfile.setLastName("Last");

		Profile updatedProfile = buildFakeProfile();

		when(profileDAO.find(updatedProfile.getId())).thenReturn(updatedProfile);
		when(profileDAO.save(updatedProfile)).thenReturn(updatedProfile);

		Profile result = profileEntityService.update(fakeProfile, new EntityAuditInfo.Builder().appUserId(USER_ID)
				.timestamp(DateBean.today()).build());

		verify(profileDAO).find(updatedProfile.getId());
		verify(profileDAO).save(updatedProfile);
		assertEquals(updatedProfile.getId(), result.getId());
		assertEquals(fakeProfile.getFirstName(), result.getFirstName());
		assertEquals(USER_ID, result.getUpdatedBy());
		assertNotNull(result.getUpdatedDate());
	}

	@Test
	public void testDelete() throws Exception {
		Profile fakeProfile = buildFakeProfile();

		profileEntityService.delete(fakeProfile);

		verify(profileDAO).delete(fakeProfile);
	}

	@Test
	public void testDeleteById() throws Exception {
		Profile fakeProfile = buildFakeProfile();

		when(profileDAO.find(fakeProfile.getId())).thenReturn(fakeProfile);

		profileEntityService.deleteById(fakeProfile.getId());

		verify(profileDAO).delete(fakeProfile);
	}

	private Profile buildFakeProfile() {
		return new ProfileBuilder()
				.id(ENTITY_ID)
				.build();
	}

	@Test
	public void testUpdate_EmployeeProfileEditForm() {
		Profile profile = buildProfile();
		when(profileDAO.find(PROFILE_UPDATE_ID)).thenReturn(profile);
		when(profileDAO.save(profile)).thenReturn(profile);

		Profile result = profileEntityService.update(buildEmployeeProfileEditForm(),
				Integer.toString(PROFILE_UPDATE_ID), USER_ID);

		verifyUpdate(profile, result);
	}

	private void verifyUpdate(Profile profile, Profile result) {
		assertEquals(USER_ID, result.getUpdatedBy());
		assertNotNull(result.getUpdatedDate());
		assertEquals("Bob", result.getFirstName());
		assertEquals("Smith", result.getLastName());
		assertEquals(PROFILE_EMAIL, result.getEmail());
		Assert.assertEquals("555-555-5555", result.getPhone());
		verify(profileDAO).save(profile);
	}

	private Profile buildProfile() {
		Profile profile = new Profile();
		profile.setEmail(PROFILE_EMAIL);
		return profile;
	}

	private EmployeeProfileEditForm buildEmployeeProfileEditForm() {
		EmployeeProfileEditForm employeeProfileEditForm = new EmployeeProfileEditForm();
		employeeProfileEditForm.setFirstName(PROFILE_FIRST_NAME);
		employeeProfileEditForm.setLastName(PROFILE_LAST_NAME);
		employeeProfileEditForm.setEmail(PROFILE_EMAIL);
		employeeProfileEditForm.setPhone(PROFILE_PHONE);
		return employeeProfileEditForm;
	}
}
