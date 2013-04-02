package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.contractors.RegistrationGapAnalysis.Match;
import com.picsauditing.actions.contractors.RegistrationGapAnalysis.MatchType;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;

public class RegistrationGapAnalysisTest {
	private RegistrationGapAnalysis registrationGapAnalysis;
	private static int counter;

	@Mock
	private BasicDAO dao;
	@Mock
	private Database database;
	@Mock
	private Permissions permissions;

	// Added this because when running tests testGetRecentlyRegistered() and testGetRequestedContractors()
	// caused failures when run independently because the database in the I18nCache is the same as the
	// mock in the Action class, which is being called two times instead of just once.
	@BeforeClass
	public static void classSetup() {
		Database databaseForTesting = Mockito.mock(Database.class);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		counter = 1;

		registrationGapAnalysis = new RegistrationGapAnalysis();
		Whitebox.setInternalState(registrationGapAnalysis, "dao", dao);
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
	public void testDeactivateDuplicate_NoDuplicate() throws Exception {
		assertFalse(registrationGapAnalysis.hasActionMessages());
		assertEquals(PicsActionSupport.REDIRECT, registrationGapAnalysis.deactivateDuplicate());

		verify(dao, never()).save(any(BaseTable.class));
	}

	@Test
	public void testDeactivateDuplicate_Duplicate() throws Exception {
		ContractorAccount duplicate = EntityFactory.makeContractor();
		ContractorAccount original = EntityFactory.makeContractor();

		registrationGapAnalysis.setDuplicate(duplicate);
		registrationGapAnalysis.setOriginal(original);

		assertEquals(PicsActionSupport.REDIRECT, registrationGapAnalysis.deactivateDuplicate());

		duplicate = Whitebox.getInternalState(registrationGapAnalysis, "duplicate");

		assertTrue(duplicate.getStatus().isDeleted());
		assertTrue(duplicate.getName().contains("DUPLICATE"));

		verify(dao).save(any(BaseTable.class));
	}

	@Test
	public void testGetMatches() {
		assertNotNull(registrationGapAnalysis.getMatches());
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
		Map<ContractorAccount, Set<Match>> matches = registrationGapAnalysis.getMatches();

		assertEquals(1, matches.keySet().size());

		for (ContractorAccount contractor : matches.keySet()) {
			assertEquals(1, matches.get(contractor).size());
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
	public void testCompareRegisteredWithRequested() throws Exception {
		ContractorAccount registered = new ContractorAccount();
		registered.setName("Existing");
		registered.setAddress("123 Main");
		registered.setCity("Irvine");
		registered.setZip("99999");
		registered.setTaxId("123456789");
		registered.setPrimaryContact(new User());
		registered.getPrimaryContact().setName("Test");
		registered.getPrimaryContact().setEmail("test1@test.com");
		registered.getPrimaryContact().setPhoneIndex("5555555555");

		ContractorAccount requested = new ContractorAccount();
		requested.setName("Requested");
		requested.setAddress("123 Main");
		requested.setCity("Other");
		requested.setZip("12345");
		requested.setTaxId("987654321");
		requested.setPrimaryContact(new User());
		requested.getPrimaryContact().setName("User");
		requested.getPrimaryContact().setEmail("user@test.com");
		requested.getPrimaryContact().setPhoneIndex("3214567890");

		Map<MatchType, String> matches = Whitebox.invokeMethod(registrationGapAnalysis,
				"compareRegisteredWithRequested", registered, requested);
		assertTrue(matches.isEmpty());

		requested.setName("Existing1");

		matches = Whitebox.invokeMethod(registrationGapAnalysis, "compareRegisteredWithRequested", registered,
				requested);
		assertTrue(matches.containsKey(MatchType.Name));
		assertEquals(1, matches.size());

		requested.setCity("Irvin");
		matches = Whitebox.invokeMethod(registrationGapAnalysis, "compareRegisteredWithRequested", registered,
				requested);
		assertTrue(matches.containsKey(MatchType.Address));
		assertEquals(2, matches.size());

		requested.setCity("Other");
		requested.setZip("99999-0001");
		matches = Whitebox.invokeMethod(registrationGapAnalysis, "compareRegisteredWithRequested", registered,
				requested);
		assertTrue(matches.containsKey(MatchType.Address));
		assertEquals(2, matches.size());

		requested.setTaxId("123456789-0");
		matches = Whitebox.invokeMethod(registrationGapAnalysis, "compareRegisteredWithRequested", registered,
				requested);
		assertTrue(matches.containsKey(MatchType.TaxID));
		assertEquals(3, matches.size());

		requested.getPrimaryContact().setName("Test1");
		matches = Whitebox.invokeMethod(registrationGapAnalysis, "compareRegisteredWithRequested", registered,
				requested);
		assertTrue(matches.containsKey(MatchType.Contact));
		assertEquals(4, matches.size());

		requested.getPrimaryContact().setEmail("test@test.com");
		matches = Whitebox.invokeMethod(registrationGapAnalysis, "compareRegisteredWithRequested", registered,
				requested);
		assertTrue(matches.containsKey(MatchType.Email));
		assertEquals(5, matches.size());

		requested.getPrimaryContact().setPhoneIndex("55555555553456");
		matches = Whitebox.invokeMethod(registrationGapAnalysis, "compareRegisteredWithRequested", registered,
				requested);
		assertTrue(matches.containsKey(MatchType.Phone));
		assertEquals(6, matches.size());
	}

	@Test
	public void testLevenshteinDistance_Examples() throws Exception {
		assertEquals(1, StringUtils.getLevenshteinDistance("Glyn", "Glen"));
		assertEquals(2, StringUtils.getLevenshteinDistance("Glyn", "Glenn"));
		assertEquals(2, StringUtils.getLevenshteinDistance("Joe Glyn", "Jo Glen"));
		// This is beyond our threshold
		assertEquals(3, StringUtils.getLevenshteinDistance("Joe Glyn", "Jo Glenn"));
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
