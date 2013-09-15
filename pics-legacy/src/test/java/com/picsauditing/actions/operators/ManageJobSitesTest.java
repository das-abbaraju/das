package com.picsauditing.actions.operators;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.User;

public class ManageJobSitesTest extends PicsTest {
	ManageJobSites manageJobSites;
	User user;
	@Mock private JobSite jobSite;
	@Mock private Country country;
	@Mock private CountrySubdivision countrySubdivision;
	@Mock private CountrySubdivisionDAO countrySubdivisionDAO;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		super.setUp();

		manageJobSites = new ManageJobSites();
		autowireEMInjectedDAOs(manageJobSites);

		user = EntityFactory.makeUser();
		// the copy of user.id to permisions.userId happens only on
		// loadPermissions which
		// happens in login, which we are not doing here. stub it
		PicsTestUtil.forceSetPrivateField(manageJobSites, "permissions", permissions);
		PicsTestUtil.forceSetPrivateField(manageJobSites, "countrySubdivisionDAO", countrySubdivisionDAO);
		manageJobSites.setJobSite(jobSite);
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
		PicsTestUtil.forceSetPrivateField(permissions, "accountType", "Operator");

		when(translationService.hasKey(eq("ManageProjects.title"), Matchers.argThat(equalTo(new Locale("en"))))).thenReturn(
				Boolean.TRUE);
		when(translationService.getText(eq("ManageProjects.title"), Matchers.argThat(equalTo(new Locale("en"))), anyVararg()))
				.thenReturn("Projects");

		assertEquals(ActionSupport.SUCCESS, manageJobSites.execute());
		// assertTrue(permissions.isOperatorCorporate());
		// assertNotNull(manageJobSites.getOperator());
	}
}