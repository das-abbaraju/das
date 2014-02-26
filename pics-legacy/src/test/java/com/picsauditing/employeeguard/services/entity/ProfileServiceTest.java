package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProfileDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.fail;

public class ProfileServiceTest {

	ProfileService profileService;

	@Mock
	private ProfileDAO profileDAO;


	@Before
	public void setUp() throws Exception {
		profileService = new ProfileService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(profileService, "profileDAO", profileDAO);
	}

	@Test
	public void testFind() throws Exception {
		fail("Not implemented");
	}

	@Test
	public void testSave() throws Exception {
		fail("Not implemented");
	}

	@Test
	public void testUpdate() throws Exception {
		fail("Not implemented");
	}

	@Test
	public void testDelete() throws Exception {
		fail("Not implemented");
	}

	@Test
	public void testDeleteById() throws Exception {
		fail("Not implemented");
	}
}
