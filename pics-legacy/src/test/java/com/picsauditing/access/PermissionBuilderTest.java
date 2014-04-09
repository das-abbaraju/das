package com.picsauditing.access;

import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.provisioning.ProductSubscriptionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class PermissionBuilderTest {

	private PermissionBuilder permissionBuilder;

	@Mock
	private ProductSubscriptionService productSubscriptionService;

	@Before
	public void setUp() throws Exception {
		permissionBuilder = new PermissionBuilder();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(permissionBuilder, "productSubscriptionService", productSubscriptionService);
	}

	@Test
	public void testLogin_SetsEmployeeUser() throws Exception {
		when(productSubscriptionService.isEmployeeGUARDEmployeeUser(any(Integer.class))).thenReturn(true);

		Permissions permissions = permissionBuilder.login(new AppUser(), new Profile());

		assertTrue(permissions.isEmployeeGuardEmployeeUser());
	}
}
