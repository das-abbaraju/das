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
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;

public class ManageJobSitesTest extends PicsTest {
	ManageJobSites manageJobSites;
	User user;
	@Mock private JobSite jobSite;
	@Mock private Country country;
	@Mock private State state;
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

		when(i18nCache.hasKey(eq("ManageProjects.title"), Matchers.argThat(equalTo(new Locale("en"))))).thenReturn(
				Boolean.TRUE);
		when(i18nCache.getText(eq("ManageProjects.title"), Matchers.argThat(equalTo(new Locale("en"))), anyVararg()))
				.thenReturn("Projects");

		assertEquals(ActionSupport.SUCCESS, manageJobSites.execute());
		// assertTrue(permissions.isOperatorCorporate());
		// assertNotNull(manageJobSites.getOperator());
	}
	@Test
	public void testUpdateStateAndCountrySubdivision_countryHasState() throws Exception{
		state = new State("CA");
		state.setCountry(new Country("US"));
		countrySubdivision = new CountrySubdivision("US-CA");
		Whitebox.setInternalState(manageJobSites, "countrySubdivision", countrySubdivision);
		when(jobSite.getState()).thenReturn(state);
		when(jobSite.getCountry()).thenReturn(new Country("US"));
		when(countrySubdivisionDAO.find(anyString())).thenReturn(countrySubdivision);

		Whitebox.invokeMethod(manageJobSites, "updateStateAndCountrySubdivision");
		verify(jobSite).setCountrySubdivision(countrySubdivision);
	}

	@Test
	public void testUpdateStateAndCountrySubdivision_countryHasWrongState() throws Exception{
		state = new State("CA");
		state.setCountry(new Country("CA"));
		countrySubdivision = new CountrySubdivision("CA-CA");
		when(jobSite.getState()).thenReturn(state);
		when(jobSite.getCountry()).thenReturn(new Country("US"));
		when(countrySubdivisionDAO.find(anyString())).thenReturn(countrySubdivision);
		Whitebox.invokeMethod(manageJobSites, "updateStateAndCountrySubdivision");
		verify(jobSite).setState(null);
		verify(jobSite).setCountrySubdivision(null);
	}
	/*
	 * @Test public void testNullOperator() throws Exception {
	 * manageJobSites.execute(); assertFalse(permissions.isOperatorCorporate());
	 * assertNull(manageJobSites.getOperator()); }
	 */
}
