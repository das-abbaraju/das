package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;

public class RegistrationGapAnalysisTest {
	private RegistrationGapAnalysis registrationGapAnalysis;
	private static int counter;

	@Mock
	private Database database;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", database);

		counter = 1;

		registrationGapAnalysis = new RegistrationGapAnalysis();
		Whitebox.setInternalState(registrationGapAnalysis, "database", database);
		Whitebox.setInternalState(registrationGapAnalysis, "permissions", permissions);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test
	public void testExecute_PicsEmployee() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, registrationGapAnalysis.execute());
	}

	@Test(expected = NoRightsException.class)
	public void testExecute_Other() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, registrationGapAnalysis.execute());
	}

	@Test
	public void testGetPossibleMatches() {
		assertNotNull(registrationGapAnalysis.getPossibleMatches());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testBuildPossibleMatches() throws Exception {
		BasicDynaBean recentlyRegistered = mockResult();
		BasicDynaBean requestedContractor = mockResult();

		BasicDynaBean requestedContractor2 = mockResult();
		when(requestedContractor2.get("id")).thenReturn(counter);
		when(requestedContractor2.get("name")).thenReturn("Test");
		when(requestedContractor2.get("address")).thenReturn("Test");
		when(requestedContractor2.get("city")).thenReturn("Test");
		when(requestedContractor2.get("zip")).thenReturn("Test");
		when(requestedContractor2.get("taxID")).thenReturn("Test");
		when(requestedContractor2.get("contactID")).thenReturn(counter);
		when(requestedContractor2.get("contact")).thenReturn("Test");
		when(requestedContractor2.get("email")).thenReturn("Test");
		when(requestedContractor2.get("phone")).thenReturn("Test");

		List<BasicDynaBean> recentlyRegisteredList = new ArrayList<BasicDynaBean>();
		recentlyRegisteredList.add(recentlyRegistered);

		List<BasicDynaBean> requestedContractorList = new ArrayList<BasicDynaBean>();
		requestedContractorList.add(requestedContractor);
		requestedContractorList.add(requestedContractor2);

		when(database.select(anyString(), anyBoolean())).thenReturn(recentlyRegisteredList, requestedContractorList);

		Whitebox.invokeMethod(registrationGapAnalysis, "buildPossibleMatches");
		Map<ContractorAccount, List<ContractorAccount>> possibleMatches = registrationGapAnalysis.getPossibleMatches();

		assertEquals(1, possibleMatches.keySet().size());

		for (ContractorAccount contractor : possibleMatches.keySet()) {
			assertEquals(1, possibleMatches.get(contractor).size());
		}
	}

	@Test
	public void testGetRecentlyRegistered() throws Exception {
		Whitebox.invokeMethod(registrationGapAnalysis, "getRecentlyRegistered");

		verify(database).select(anyString(), anyBoolean());
	}

	@Test
	public void testGetRequestedContractors() throws Exception {
		Whitebox.invokeMethod(registrationGapAnalysis, "getRequestedContractors");

		verify(database).select(anyString(), anyBoolean());
	}

	@Test
	public void testCreateContractorAndUser() throws Exception {
		BasicDynaBean row = mockResult();

		ContractorAccount contractor = Whitebox.invokeMethod(registrationGapAnalysis, "createContractorAndUser", row);

		assertEquals(row.get("name").toString(), contractor.getName());
		assertEquals(row.get("contact").toString(), contractor.getPrimaryContact().getName());
	}

	@Test
	public void testIsPartialMatch() throws Exception {
		ContractorAccount registered = new ContractorAccount();
		ContractorAccount requested = new ContractorAccount();

		registered.setName("Existing");
		registered.setAddress("123 Main");
		registered.setCity("Irvine");

		requested.setName("Requested");
		requested.setAddress("123 Main");
		requested.setCity("Irvin");

		assertTrue((Boolean) Whitebox.invokeMethod(registrationGapAnalysis, "isPartialMatch", registered, requested));

		registered.setZip("92614");

		requested.setCity("Tustin");
		requested.setZip("92614-9999");

		assertTrue((Boolean) Whitebox.invokeMethod(registrationGapAnalysis, "isPartialMatch", registered, requested));

		registered.setAddress("123 Main");
		registered.setTaxId("123456789");

		requested.setAddress("456 Wall");
		requested.setTaxId("12345678");

		assertTrue((Boolean) Whitebox.invokeMethod(registrationGapAnalysis, "isPartialMatch", registered, requested));

		registered.setPrimaryContact(new User());
		registered.getPrimaryContact().setName("Test");

		requested.setTaxId("987654321");
		requested.setPrimaryContact(new User());
		requested.getPrimaryContact().setName("Tess");

		assertTrue((Boolean) Whitebox.invokeMethod(registrationGapAnalysis, "isPartialMatch", registered, requested));

		registered.getPrimaryContact().setEmail("test1@test.com");

		requested.getPrimaryContact().setName("Jane");
		requested.getPrimaryContact().setEmail("test@test.com");

		assertTrue((Boolean) Whitebox.invokeMethod(registrationGapAnalysis, "isPartialMatch", registered, requested));

		registered.getPrimaryContact().setPhone("555 555 5555");

		requested.getPrimaryContact().setName("Jane");
		requested.getPrimaryContact().setEmail("jane@test.com");
		requested.getPrimaryContact().setPhone("055 555 5555");

		assertTrue((Boolean) Whitebox.invokeMethod(registrationGapAnalysis, "isPartialMatch", registered, requested));

		requested.getPrimaryContact().setPhone("123 456 7890");

		assertFalse((Boolean) Whitebox.invokeMethod(registrationGapAnalysis, "isPartialMatch", registered, requested));
	}

	private BasicDynaBean mockResult() {
		BasicDynaBean result = mock(BasicDynaBean.class);

		when(result.get("id")).thenReturn(counter);
		when(result.get("name")).thenReturn("Contractor " + counter);
		when(result.get("address")).thenReturn("Address " + counter);
		when(result.get("city")).thenReturn("City " + counter);
		when(result.get("zip")).thenReturn("Zip " + counter);
		when(result.get("taxID")).thenReturn("TaxID " + counter);

		when(result.get("contactID")).thenReturn(counter);
		when(result.get("contact")).thenReturn("Contact " + counter);
		when(result.get("email")).thenReturn("Email " + counter);
		when(result.get("phone")).thenReturn("Phone " + counter);

		counter++;

		return result;
	}
}
