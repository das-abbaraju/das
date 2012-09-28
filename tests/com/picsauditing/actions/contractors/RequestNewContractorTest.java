package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.search.Database;

public class RequestNewContractorTest {
	private RequestNewContractor requestNewContractor;
	private PicsTestUtil picsTestUtil = new PicsTestUtil();

	@Mock
	private ContractorRegistrationRequest registrationRequest;
	@Mock
	private Database database;
	@Mock
	private EmailBuilder builder;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Permissions permissions;
	@Mock
	private Query query;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", database);

		requestNewContractor = new RequestNewContractor();
		picsTestUtil.autowireEMInjectedDAOs(requestNewContractor, entityManager);

		when(entityManager.createQuery(anyString())).thenReturn(query);

		Whitebox.setInternalState(requestNewContractor, "permissions", permissions);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test(expected = NoRightsException.class)
	public void testExecute_Contractor() throws Exception {
		when(permissions.isContractor()).thenReturn(true);
		requestNewContractor.execute();
	}

	@Test
	public void testExecute_PicsEmployee() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);

		assertNull(requestNewContractor.getNewContractor());
		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.execute());
		assertNotNull(requestNewContractor.getNewContractor());
		assertEquals(ContractorRegistrationRequestStatus.Active, requestNewContractor.getStatus());
		assertEquals(0, requestNewContractor.getOpID());
	}

	@Test(expected = NoRightsException.class)
	public void testExecute_OperatorCorporateWithoutPermission() throws Exception {
		when(permissions.isOperatorCorporate()).thenReturn(true);
		requestNewContractor.execute();
	}

	@Test
	public void testExecute_OperatorCorporateWithPermission() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();
		User user = EntityFactory.makeUser();

		when(entityManager.find(OperatorAccount.class, operator.getId())).thenReturn(operator);
		when(entityManager.find(User.class, user.getId())).thenReturn(user);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(operator.getId());
		when(permissions.getUserId()).thenReturn(user.getId());
		when(permissions.hasPermission(OpPerms.RequestNewContractor)).thenReturn(true);

		assertNull(requestNewContractor.getNewContractor());
		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.execute());
		assertNotNull(requestNewContractor.getNewContractor());
		assertEquals(ContractorRegistrationRequestStatus.Active, requestNewContractor.getStatus());
		assertEquals(operator.getId(), requestNewContractor.getOpID());
		assertNotNull(requestNewContractor.getNewContractor().getRequestedBy());
		assertNotNull(requestNewContractor.getNewContractor().getRequestedByUser());
		assertEquals(operator, requestNewContractor.getNewContractor().getRequestedBy());
		assertEquals(user.getId(), requestNewContractor.getNewContractor().getRequestedByUser().getId());
	}

	@Test
	public void testExecute_NewContractorSet() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();

		when(permissions.isPicsEmployee()).thenReturn(true);
		when(registrationRequest.getId()).thenReturn(1);
		when(registrationRequest.getStatus()).thenReturn(ContractorRegistrationRequestStatus.ClosedSuccessful);
		when(registrationRequest.getRequestedBy()).thenReturn(operator);

		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.execute());
		assertEquals(ContractorRegistrationRequestStatus.ClosedSuccessful, requestNewContractor.getStatus());
		assertEquals(operator.getId(), requestNewContractor.getOpID());

		verify(entityManager, never()).find(eq(OperatorAccount.class), anyInt());
		verify(entityManager, never()).find(eq(User.class), anyInt());
	}

	@Test
	public void testExecute_NewContractorIdZero() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();
		User user = EntityFactory.makeUser();

		when(entityManager.find(OperatorAccount.class, operator.getId())).thenReturn(operator);
		when(entityManager.find(User.class, user.getId())).thenReturn(user);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(operator.getId());
		when(permissions.getUserId()).thenReturn(user.getId());
		when(permissions.hasPermission(OpPerms.RequestNewContractor)).thenReturn(true);

		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.execute());
		assertEquals(ContractorRegistrationRequestStatus.Active, requestNewContractor.getStatus());
		assertEquals(operator.getId(), requestNewContractor.getOpID());
		assertNotNull(requestNewContractor.getNewContractor().getRequestedBy());
		assertNotNull(requestNewContractor.getNewContractor().getRequestedByUser());
		assertEquals(operator, requestNewContractor.getNewContractor().getRequestedBy());
		assertEquals(user.getId(), requestNewContractor.getNewContractor().getRequestedByUser().getId());
	}

	@Test
	public void testMatchingList_NoButton() {
		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.matchingList());

		verify(entityManager, never()).find(eq(ContractorRegistrationRequest.class), anyInt());
	}

	@Test
	public void testMatchingList_ButtonNoNewContractor() {
		requestNewContractor.setButton("MatchingList");

		assertEquals(PicsActionSupport.BLANK, requestNewContractor.matchingList());
		assertTrue(requestNewContractor.hasActionErrors());
	}

	@Test
	public void testMatchingList_ButtonNewContractorNoResults() {
		List<ContractorAccount> contractors = new ArrayList<ContractorAccount>();

		requestNewContractor = spy(requestNewContractor);
		when(entityManager.find(eq(ContractorRegistrationRequest.class), anyInt())).thenReturn(registrationRequest);
		doReturn(contractors).when(requestNewContractor).runGapAnalysis(any(ContractorRegistrationRequest.class));

		requestNewContractor.setButton("MatchingList");
		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals("matches", requestNewContractor.matchingList());
		assertFalse(requestNewContractor.hasActionErrors());

		verify(entityManager).find(eq(ContractorRegistrationRequest.class), anyInt());
		verify(entityManager, never()).merge(eq(ContractorRegistrationRequest.class));
		verify(entityManager, never()).persist(eq(ContractorRegistrationRequest.class));
		verify(requestNewContractor).runGapAnalysis(any(ContractorRegistrationRequest.class));
	}

	@Test
	public void testMatchingList_ButtonExistingNewContractorResults() {
		List<ContractorAccount> contractors = new ArrayList<ContractorAccount>();
		contractors.add(EntityFactory.makeContractor());

		requestNewContractor = spy(requestNewContractor);
		when(entityManager.find(eq(ContractorRegistrationRequest.class), anyInt())).thenReturn(registrationRequest);
		when(registrationRequest.getId()).thenReturn(1);
		when(registrationRequest.getName()).thenReturn("Name");
		when(registrationRequest.getRequestedBy()).thenReturn(EntityFactory.makeOperator());
		doReturn(contractors).when(requestNewContractor).runGapAnalysis(any(ContractorRegistrationRequest.class));

		requestNewContractor.setButton("MatchingList");
		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals("matches", requestNewContractor.matchingList());
		assertFalse(requestNewContractor.hasActionErrors());

		verify(entityManager).find(eq(ContractorRegistrationRequest.class), anyInt());
		verify(entityManager).merge(any(ContractorRegistrationRequest.class));
		verify(entityManager, never()).persist(any(ContractorRegistrationRequest.class));
		verify(requestNewContractor).runGapAnalysis(any(ContractorRegistrationRequest.class));
	}

	@Test
	public void testMatchingList_ButtonNewContractorResults() {
		List<ContractorAccount> contractors = new ArrayList<ContractorAccount>();
		contractors.add(EntityFactory.makeContractor());

		requestNewContractor = spy(requestNewContractor);
		when(entityManager.find(eq(ContractorRegistrationRequest.class), anyInt())).thenReturn(registrationRequest);
		when(registrationRequest.getRequestedBy()).thenReturn(EntityFactory.makeOperator());
		when(registrationRequest.getName()).thenReturn("Name");
		doReturn(contractors).when(requestNewContractor).runGapAnalysis(any(ContractorRegistrationRequest.class));

		requestNewContractor.setButton("MatchingList");
		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals("matches", requestNewContractor.matchingList());
		assertFalse(requestNewContractor.hasActionErrors());

		verify(entityManager).find(eq(ContractorRegistrationRequest.class), anyInt());
		verify(entityManager, never()).merge(any(ContractorRegistrationRequest.class));
		verify(entityManager).persist(any(ContractorRegistrationRequest.class));
		verify(requestNewContractor).runGapAnalysis(any(ContractorRegistrationRequest.class));
	}

	@Test
	public void testSave_CheckContactFieldsNameEmpty() throws Exception {
		saveWithErrors();
	}

	@Test
	public void testSave_CheckContactFieldsContactEmpty() throws Exception {
		when(registrationRequest.getName()).thenReturn("Name");

		saveWithErrors();
	}

	@Test
	public void testSave_CheckContactFieldsCountryEmpty() throws Exception {
		when(registrationRequest.getName()).thenReturn("Name");
		when(registrationRequest.getContact()).thenReturn("Contact");

		saveWithErrors();
	}

	@Test
	public void testSave_CheckContactFieldsCountryNotUSOrCA() throws Exception {
		when(registrationRequest.getName()).thenReturn("Name");
		when(registrationRequest.getContact()).thenReturn("Contact");
		when(registrationRequest.getCountry()).thenReturn(new Country("Test"));

		saveWithErrors();
	}

	@Test
	public void testSave_CheckContactFieldsCountryUSSubdivisionNull() throws Exception {
		when(registrationRequest.getName()).thenReturn("Name");
		when(registrationRequest.getContact()).thenReturn("Contact");
		when(registrationRequest.getCountry()).thenReturn(new Country("US"));

		saveWithErrors();
	}

	@Test
	public void testSave_CheckContactFieldsCountryUSSubdivisionPhoneEmpty() throws Exception {
		when(registrationRequest.getName()).thenReturn("Name");
		when(registrationRequest.getContact()).thenReturn("Contact");
		when(registrationRequest.getCountry()).thenReturn(new Country("US"));
		when(registrationRequest.getCountrySubdivision()).thenReturn(new CountrySubdivision("CA"));

		saveWithErrors();
	}

	@Test
	public void testSave_CheckContactFieldsEmailEmpty() throws Exception {
		when(registrationRequest.getName()).thenReturn("Name");
		when(registrationRequest.getContact()).thenReturn("Contact");
		when(registrationRequest.getCountry()).thenReturn(new Country("Test"));
		when(registrationRequest.getPhone()).thenReturn("Phone");

		saveWithErrors();
	}

	@Test
	public void testSave_CheckContactFieldsEmailInvalid() throws Exception {
		when(registrationRequest.getName()).thenReturn("Name");
		when(registrationRequest.getContact()).thenReturn("Contact");
		when(registrationRequest.getCountry()).thenReturn(new Country("Test"));
		when(registrationRequest.getPhone()).thenReturn("Phone");
		when(registrationRequest.getEmail()).thenReturn("Email");

		saveWithErrors();
	}

	@Test
	public void testSave_CheckOperatorSpecifiedFieldsRequestedByNull() throws Exception {
		filledContactFields();
		saveWithErrors();
	}

	@Test
	public void testSave_CheckOperatorSpecifiedFieldsRequestedByUserNullOtherNull() throws Exception {
		filledContactFields();

		when(registrationRequest.getRequestedBy()).thenReturn(EntityFactory.makeOperator());

		saveWithErrors();
	}

	@Test
	public void testSave_CheckOperatorSpecifiedFieldsRequestedByUserOtherNullDeadlineNull() throws Exception {
		filledContactFields();

		when(registrationRequest.getRequestedBy()).thenReturn(EntityFactory.makeOperator());
		when(registrationRequest.getRequestedByUser()).thenReturn(EntityFactory.makeUser());

		saveWithErrors();
	}

	@Test
	public void testSave_CheckOperatorSpecifiedFieldsRequestedByUserNullOtherDeadlineNull() throws Exception {
		filledContactFields();

		when(registrationRequest.getRequestedBy()).thenReturn(EntityFactory.makeOperator());
		when(registrationRequest.getRequestedByUserOther()).thenReturn("Other");

		saveWithErrors();
	}

	@Test
	public void testSave_CheckOperatorSpecifiedFieldsReasonNull() throws Exception {
		filledContactFields();

		when(registrationRequest.getRequestedBy()).thenReturn(EntityFactory.makeOperator());
		when(registrationRequest.getRequestedByUserOther()).thenReturn("Other");
		when(registrationRequest.getDeadline()).thenReturn(new Date());

		saveWithErrors();
	}

	@Test
	public void testSave_CheckStatusRequirementsStatusHoldDateMissing() throws Exception {
		filledContactFields();
		filledOperatorSpecificFields();

		requestNewContractor.setStatus(ContractorRegistrationRequestStatus.Hold);

		saveWithErrors();
	}

	@Test
	public void testSave_CheckStatusRequirementsStatusClosedContactedSuccessfulContractorMissing() throws Exception {
		filledContactFields();
		filledOperatorSpecificFields();

		requestNewContractor.setStatus(ContractorRegistrationRequestStatus.ClosedContactedSuccessful);

		saveWithErrors();
	}

	@Test
	public void testSave_CheckStatusRequirementsStatusMissing() throws Exception {
		when(registrationRequest.getId()).thenReturn(1);

		filledContactFields();
		filledOperatorSpecificFields();

		requestNewContractor.setStatus(null);

		saveWithErrors();
	}

	@Test
	public void testSave_SaveWithMatchesTagsStatusHold() throws Exception {
		requestNewContractor = spy(requestNewContractor);

		List<ContractorAccount> matches = new ArrayList<ContractorAccount>();
		matches.add(EntityFactory.makeContractor());

		doReturn(matches).when(requestNewContractor).runGapAnalysis(registrationRequest);
		when(registrationRequest.getHoldDate()).thenReturn(new Date());
		when(registrationRequest.getId()).thenReturn(1);

		filledContactFields();
		filledOperatorSpecificFields();

		OperatorTag tag = new OperatorTag();
		tag.setTag("Tag");
		tag.setId(1);

		List<OperatorTag> tags = new ArrayList<OperatorTag>();
		tags.add(tag);

		requestNewContractor.setNewContractor(registrationRequest);
		requestNewContractor.setRequestedTags(tags);
		requestNewContractor.setStatus(ContractorRegistrationRequestStatus.Hold);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.save());
		assertFalse(requestNewContractor.hasActionErrors());
	}

	@Test
	public void testSave_SaveCloseContactedSuccessful() throws Exception {
		requestNewContractor = spy(requestNewContractor);

		doReturn(Collections.emptyList()).when(requestNewContractor).runGapAnalysis(registrationRequest);

		when(registrationRequest.getContractor()).thenReturn(EntityFactory.makeContractor());
		when(registrationRequest.getId()).thenReturn(1);
		when(registrationRequest.getStatus()).thenReturn(ContractorRegistrationRequestStatus.ClosedContactedSuccessful);

		filledContactFields();
		filledOperatorSpecificFields();

		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.save());
		assertFalse(requestNewContractor.hasActionErrors());
	}

	@Test
	public void testSave_SaveCloseSuccessful() throws Exception {
		requestNewContractor = spy(requestNewContractor);

		doReturn(Collections.emptyList()).when(requestNewContractor).runGapAnalysis(registrationRequest);

		when(registrationRequest.getContractor()).thenReturn(EntityFactory.makeContractor());
		when(registrationRequest.getId()).thenReturn(1);
		when(registrationRequest.getStatus()).thenReturn(ContractorRegistrationRequestStatus.ClosedSuccessful);

		filledContactFields();
		filledOperatorSpecificFields();

		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.save());
		assertFalse(requestNewContractor.hasActionErrors());
	}

	@Test
	public void testSave_NewContractor() throws Exception {
		filledContactFields();
		filledOperatorSpecificFields();

		requestNewContractor = spy(requestNewContractor);
		doReturn(new ArrayList<ContractorAccount>()).when(requestNewContractor).runGapAnalysis(registrationRequest);
		doNothing().when(requestNewContractor).sendEmail();

		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.save());
		assertFalse(requestNewContractor.hasActionErrors());
		assertTrue(requestNewContractor.hasActionMessages());

		verify(entityManager).persist(any(ContractorRegistrationRequest.class));
	}

	@Test
	public void testSave_Existing() throws Exception {
		when(registrationRequest.getId()).thenReturn(1);

		filledContactFields();
		filledOperatorSpecificFields();

		requestNewContractor = spy(requestNewContractor);
		doReturn(new ArrayList<ContractorAccount>()).when(requestNewContractor).runGapAnalysis(registrationRequest);

		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.save());
		assertFalse(requestNewContractor.hasActionErrors());
		assertTrue(requestNewContractor.hasActionMessages());

		verify(entityManager).merge(any(ContractorRegistrationRequest.class));
	}

	@Test
	public void testSave_TransferNewTagsToContractorAccount() throws Exception {
		ContractorAccount contractor = mock(ContractorAccount.class);
		OperatorAccount operator = mock(OperatorAccount.class);
		OperatorTag tag = mock(OperatorTag.class);

		List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
		operators.add(operator);

		when(contractor.getOperatorAccounts()).thenReturn(operators);
		when(entityManager.find(OperatorTag.class, 1)).thenReturn(tag);
		when(registrationRequest.getContractor()).thenReturn(contractor);
		when(registrationRequest.getId()).thenReturn(1);
		when(registrationRequest.getOperatorTags()).thenReturn("1");
		when(tag.getId()).thenReturn(1);
		when(tag.getOperator()).thenReturn(operator);

		filledContactFields();
		filledOperatorSpecificFields();

		requestNewContractor = spy(requestNewContractor);
		doReturn(new ArrayList<ContractorAccount>()).when(requestNewContractor).runGapAnalysis(registrationRequest);

		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.save());
		assertFalse(requestNewContractor.hasActionErrors());
		assertTrue(requestNewContractor.hasActionMessages());

		verify(entityManager).persist(any(BaseTable.class));
		verify(entityManager).merge(any(ContractorRegistrationRequest.class));
	}

	@Test
	public void testSave_DoNotAddExistingTags() throws Exception {
		ContractorAccount contractor = mock(ContractorAccount.class);
		ContractorTag contractorTag = mock(ContractorTag.class);
		OperatorTag tag = mock(OperatorTag.class);

		List<ContractorTag> tags = new ArrayList<ContractorTag>();
		tags.add(contractorTag);

		when(contractorTag.getTag()).thenReturn(tag);
		when(contractor.getOperatorTags()).thenReturn(tags);
		when(entityManager.find(OperatorTag.class, 1)).thenReturn(tag);
		when(registrationRequest.getContractor()).thenReturn(contractor);
		when(registrationRequest.getId()).thenReturn(1);
		when(registrationRequest.getOperatorTags()).thenReturn("1");
		when(tag.getId()).thenReturn(1);

		filledContactFields();
		filledOperatorSpecificFields();

		requestNewContractor = spy(requestNewContractor);
		doReturn(new ArrayList<ContractorAccount>()).when(requestNewContractor).runGapAnalysis(registrationRequest);

		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.save());
		assertFalse(requestNewContractor.hasActionErrors());
		assertTrue(requestNewContractor.hasActionMessages());

		verify(entityManager, never()).persist(any(BaseTable.class));
		verify(entityManager).merge(any(ContractorRegistrationRequest.class));
	}

	@Test
	public void testContact_PersonalEmail() throws Exception {
		registrationRequest = new ContractorRegistrationRequest();
		registrationRequest.setRequestedBy(EntityFactory.makeOperator());
		registrationRequest.setName("Name");

		requestNewContractor.setContactType(RequestNewContractor.PERSONAL_EMAIL);
		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.REDIRECT, requestNewContractor.contact());
		assertFalse(requestNewContractor.hasActionErrors());
		assertNotNull(requestNewContractor.getNewContractor().getNotes());
		assertEquals(1, requestNewContractor.getNewContractor().getContactCountByEmail());
	}

	@Test
	public void testContact_DraftEmailMissingNotes() throws Exception {
		registrationRequest = new ContractorRegistrationRequest();

		requestNewContractor.setContactType(RequestNewContractor.DRAFT_EMAIL);
		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.contact());
		assertTrue(requestNewContractor.hasActionErrors());
		assertEquals(0, registrationRequest.getContactCountByEmail());
		assertEquals(0, registrationRequest.getContactCount());
	}

	@Test
	public void testContact_DraftEmailWithNotes() throws Exception {
		requestNewContractor = spy(requestNewContractor);
		doNothing().when(requestNewContractor).sendEmail();

		registrationRequest = new ContractorRegistrationRequest();
		registrationRequest.setRequestedBy(EntityFactory.makeOperator());

		requestNewContractor.setAddToNotes("Test");
		requestNewContractor.setContactType(RequestNewContractor.DRAFT_EMAIL);
		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.REDIRECT, requestNewContractor.contact());
		assertFalse(requestNewContractor.hasActionErrors());
		assertEquals(1, registrationRequest.getContactCountByEmail());
		assertEquals(1, registrationRequest.getContactCount());
	}

	@Test
	public void testContact_PhoneMissingNotes() throws Exception {
		registrationRequest = new ContractorRegistrationRequest();

		requestNewContractor.setContactType(RequestNewContractor.PHONE);
		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.contact());
		assertTrue(requestNewContractor.hasActionErrors());
		assertEquals(0, registrationRequest.getContactCountByPhone());
		assertEquals(0, registrationRequest.getContactCount());
	}

	@Test
	public void testContact_Phone() throws Exception {
		registrationRequest = new ContractorRegistrationRequest();
		registrationRequest.setRequestedBy(EntityFactory.makeOperator());
		registrationRequest.setName("Name");

		requestNewContractor.setAddToNotes("Test");
		requestNewContractor.setContactType(RequestNewContractor.PHONE);
		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.REDIRECT, requestNewContractor.contact());
		assertFalse(requestNewContractor.hasActionErrors());
		assertEquals(1, registrationRequest.getContactCountByPhone());
		assertEquals(1, registrationRequest.getContactCount());
	}

	@Test
	public void testPreviewEmail() throws Exception {
		requestNewContractor = spy(requestNewContractor);
		doReturn(builder).when(requestNewContractor).prepareEmailBuilder();

		assertNull(requestNewContractor.previewEmail());

		verify(builder).build();
	}

	@Test
	public void testGetOperatorsList_PermissionsNull() throws Exception {
		Whitebox.setInternalState(requestNewContractor, "permissions", (Permissions) null);

		assertNull(requestNewContractor.getOperatorsList());
	}

	@Test
	public void testGetOperatorsList() throws Exception {
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(query.getResultList()).thenReturn(new ArrayList<OperatorAccount>());

		assertNotNull(requestNewContractor.getOperatorsList());
	}

	@Test
	public void testGetUsersList() throws Exception {
		when(query.getResultList()).thenReturn(new ArrayList<User>());

		assertNotNull(requestNewContractor.getUsersList(1));
	}

	@Test
	public void testGetForm() throws Exception {
		assertNull(requestNewContractor.getForm());
	}

	@Test
	public void testGetForm_NewContractor() throws Exception {
		requestNewContractor.setNewContractor(registrationRequest);
		assertNull(requestNewContractor.getForm());
	}

	@Test
	public void testGetForm_NewContractorWithOperator() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();
		OperatorForm form = new OperatorForm();
		form.setFormName("*Contractor Letter");
		operator.getOperatorForms().add(form);

		when(registrationRequest.getRequestedBy()).thenReturn(operator);

		requestNewContractor.setNewContractor(registrationRequest);
		assertEquals(form, requestNewContractor.getForm());
	}

	@Test
	public void testGetForms_FormsAndRequestNull() {
		assertNull(requestNewContractor.getForms());
	}

	@Test
	public void testGetForms_FormRequestedByNull() {
		requestNewContractor.setNewContractor(registrationRequest);

		assertNull(requestNewContractor.getForms());
	}

	@Test
	public void testGetForms_RequestedByNoForms() {
		when(registrationRequest.getRequestedBy()).thenReturn(EntityFactory.makeOperator());

		requestNewContractor.setNewContractor(registrationRequest);

		assertNotNull(requestNewContractor.getForms());
		assertTrue(requestNewContractor.getForms().isEmpty());
	}

	@Test
	public void testGetForms_RequestedByCorporateForms() {
		OperatorAccount corporate = EntityFactory.makeOperator();
		corporate.setType("Corporate");
		// Make sure they don't end up under the PICS umbrella, just in case.
		corporate.setId(50 + corporate.getId());

		OperatorAccount operator = EntityFactory.makeOperator();
		operator.setId(50 + operator.getId());
		Facility facility = EntityFactory.makeFacility(operator, corporate);
		operator.getCorporateFacilities().add(facility);

		OperatorForm form = new OperatorForm();
		form.setFormName("*Form");
		corporate.getOperatorForms().add(form);

		when(registrationRequest.getRequestedBy()).thenReturn(operator);

		requestNewContractor.setNewContractor(registrationRequest);

		assertNotNull(requestNewContractor.getForms());
		assertFalse(requestNewContractor.getForms().isEmpty());
		assertEquals(form, requestNewContractor.getForms().get(0));
	}

	private void saveWithErrors() throws Exception {
		requestNewContractor = spy(requestNewContractor);
		doReturn(Collections.emptyList()).when(requestNewContractor).runGapAnalysis(registrationRequest);

		requestNewContractor.setNewContractor(registrationRequest);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractor.save());
		assertTrue(requestNewContractor.hasActionErrors());
	}

	private void filledContactFields() {
		when(registrationRequest.getName()).thenReturn("Name");
		when(registrationRequest.getContact()).thenReturn("Contact");
		when(registrationRequest.getCountry()).thenReturn(new Country("Test"));
		when(registrationRequest.getPhone()).thenReturn("Phone");
		when(registrationRequest.getEmail()).thenReturn("test@email.com");
	}

	private void filledOperatorSpecificFields() {
		when(registrationRequest.getRequestedBy()).thenReturn(EntityFactory.makeOperator());
		when(registrationRequest.getRequestedByUserOther()).thenReturn("Other");
		when(registrationRequest.getDeadline()).thenReturn(new Date());
		when(registrationRequest.getReasonForRegistration()).thenReturn("Reason");
	}
}