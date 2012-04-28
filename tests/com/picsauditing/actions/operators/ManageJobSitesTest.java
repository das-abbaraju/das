package com.picsauditing.actions.operators;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.hamcrest.Matchers.*;


import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(I18nCache.class)
public class ManageJobSitesTest extends PicsTest {
	ManageJobSites manageJobSites;
	User user;

	@Mock private Permissions permissions;
	@Mock private I18nCache i18nCache;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		mockStatic(I18nCache.class);
		Mockito.when(I18nCache.getInstance()).thenReturn(i18nCache);

		manageJobSites = new ManageJobSites();
		autowireEMInjectedDAOs(manageJobSites);

		user = EntityFactory.makeUser();
		// the copy of user.id to permisions.userId happens only on
		// loadPermissions which
		// happens in login, which we are not doing here. stub it
		forceSetPrivateField(manageJobSites, "permissions", permissions);
	}

	@Test
	public void testOperatorIsLoaded() throws Exception {
		// Check operator
		// check permissions
		// set operator based on permissions
		// set subheading
		// return success

		// test operator not null if operator/corporate
		// test operator null
		// test subheading not null
		// test success
		forceSetPrivateField(permissions, "accountType", "Operator");
		
		when(i18nCache.hasKey(eq("ManageProjects.title"), Matchers.argThat(equalTo(new Locale("en")))))
		.thenReturn(Boolean.TRUE);
		when(i18nCache.getText(eq("ManageProjects.title"), 
				Matchers.argThat(equalTo(new Locale("en"))), 
				anyVararg())).thenReturn("Projects");
		
		assertEquals(ActionSupport.SUCCESS, manageJobSites.execute());
		//assertTrue(permissions.isOperatorCorporate());
		//assertNotNull(manageJobSites.getOperator());
	}

/*	@Test
	public void testNullOperator() throws Exception {
		manageJobSites.execute();
		assertFalse(permissions.isOperatorCorporate());
		assertNull(manageJobSites.getOperator());
	}*/
}
