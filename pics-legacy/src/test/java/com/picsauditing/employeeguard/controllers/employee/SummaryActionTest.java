package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.PicsActionTest;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SummaryActionTest extends PicsActionTest {
	private SummaryAction dashboardAction;

	@Mock
	private ProfileEntityService profileEntityService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		dashboardAction = new SummaryAction();

		super.setUp(dashboardAction);

		Whitebox.setInternalState(dashboardAction, "profileEntityService", profileEntityService);
		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);
	}

	@Ignore
	@Test
	public void testIndex() throws Exception {
		assertEquals("dashboard", dashboardAction.index());
//		assertNotNull(dashboardAction.getProfile());
		verify(profileEntityService).findByAppUserId(Identifiable.SYSTEM);
	}
}
