package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.PicsActionTest;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.employeeguard.services.ProfileServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DashboardActionTest extends PicsActionTest {
	private DashboardAction dashboardAction;

	private ProfileService profileService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		dashboardAction = new DashboardAction();
		profileService = ProfileServiceFactory.getProfileService();

		super.setUp(dashboardAction);

		Whitebox.setInternalState(dashboardAction, "profileService", profileService);
		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);
	}

	@Test
	public void testIndex() throws Exception {
		assertEquals("dashboard", dashboardAction.index());
		assertNotNull(dashboardAction.getProfile());
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
	}
}