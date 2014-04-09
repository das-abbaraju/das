package com.picsauditing.provisioning;

import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProductSubscriptionServiceImplTest {

	public static final int APP_USER_ID_FOR_PROFILE = 45;
	public static final int APP_USER_ID_NO_PROFILE = 789;

	private ProductSubscriptionService productSubscriptionService;

	@Mock
	private ProfileEntityService profileEntityService;

	@Before
	public void setUp() throws Exception {
		productSubscriptionService = new ProductSubscriptionServiceImpl();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(productSubscriptionService, "profileEntityService", profileEntityService);
	}

	@Test
	public void testIsEmployeeGUARDEmployeeUser() throws Exception {
		setupProfileEntityService();

		boolean result = productSubscriptionService.isEmployeeGUARDEmployeeUser(APP_USER_ID_FOR_PROFILE);

		assertTrue(result);
	}

	@Test
	public void testIsEmployeeGUARDEmployeeUser_Not_EmployeeGUARD_Employee() throws Exception {
		setupProfileEntityService();

		boolean result = productSubscriptionService.isEmployeeGUARDEmployeeUser(APP_USER_ID_NO_PROFILE);

		assertFalse(result);
	}

	private void setupProfileEntityService() {
		when(profileEntityService.findByAppUserId(APP_USER_ID_FOR_PROFILE)).thenReturn(new Profile());
	}
}
