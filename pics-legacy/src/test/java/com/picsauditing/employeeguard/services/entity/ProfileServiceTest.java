package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProfileDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.builders.ProfileBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileServiceTest {

	private ProfileService profileService;

	@Mock
	private ProfileDAO profileDAO;

	@Before
	public void setUp() throws Exception {
		profileService = new ProfileService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(profileService, "profileDAO", profileDAO);
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		profileService.find(null);
	}

	@Test
	public void testFind() throws Exception {
		Profile expected = buildFakeProfile();

		when(profileDAO.find(expected.getId())).thenReturn(expected);

		Profile result = profileService.find(expected.getId());

		assertNotNull(result);
		assertEquals(expected.getId(), result.getId());
	}

	@Test
	public void testSave() throws Exception {
		Profile fakeProfile = buildFakeProfile();

		when(profileDAO.save(fakeProfile)).thenReturn(fakeProfile);

		Profile result = profileService.save(fakeProfile, CREATED);

		verify(profileDAO).save(fakeProfile);
		assertEquals(USER_ID, result.getCreatedBy());
		assertEquals(CREATED_DATE, result.getCreatedDate());
		assertNull(result.getUpdatedDate());
	}

	@Test
	public void testUpdate() throws Exception {
		Profile fakeProfile = buildFakeProfile();
		fakeProfile.setFirstName("First");
		fakeProfile.setLastName("Last");

		Profile updatedProfile = buildFakeProfile();

		when(profileDAO.find(updatedProfile.getId())).thenReturn(updatedProfile);
		when(profileDAO.save(updatedProfile)).thenReturn(updatedProfile);

		Profile result = profileService.update(fakeProfile, UPDATED);

		verify(profileDAO).find(updatedProfile.getId());
		verify(profileDAO).save(updatedProfile);
		assertEquals(updatedProfile.getId(), result.getId());
		assertEquals(fakeProfile.getFirstName(), result.getFirstName());
		assertEquals(USER_ID, result.getUpdatedBy());
		assertEquals(UPDATED_DATE, result.getUpdatedDate());
	}

	@Test
	public void testDelete() throws Exception {
		Profile fakeProfile = buildFakeProfile();

		profileService.delete(fakeProfile);

		verify(profileDAO).delete(fakeProfile);
	}

	@Test
	public void testDeleteById() throws Exception {
		Profile fakeProfile = buildFakeProfile();

		when(profileDAO.find(fakeProfile.getId())).thenReturn(fakeProfile);

		profileService.deleteById(fakeProfile.getId());

		verify(profileDAO).delete(fakeProfile);
	}

	private Profile buildFakeProfile() {
		return new ProfileBuilder()
				.id(ENTITY_ID)
				.build();
	}

}
