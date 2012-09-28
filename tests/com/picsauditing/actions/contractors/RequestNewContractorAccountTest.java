package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.contractors.RequestNewContractorAccount.RequestContactType;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.Database;

public class RequestNewContractorAccountTest {
	private RequestNewContractorAccount requestNewContractorAccount;
	private PicsTestUtil picsTestUtil;

	private ContractorAccount contractor;

	@Mock
	private ContractorOperator relationship;
	@Mock
	private Database database;
	@Mock
	private EmailBuilder emailBuilder;
	@Mock
	private EmailQueue email;
	@Mock
	private EmailSender emailSender;
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

		requestNewContractorAccount = new RequestNewContractorAccount();
		picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(requestNewContractorAccount, entityManager);

		contractor = EntityFactory.makeContractor();

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(Collections.emptyList());

		Whitebox.setInternalState(requestNewContractorAccount, "emailBuilder", emailBuilder);
		Whitebox.setInternalState(requestNewContractorAccount, "emailSender", emailSender);
		Whitebox.setInternalState(requestNewContractorAccount, "permissions", permissions);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	// load tags
	// TODO How do we show tags for corporate and PICS users?

	@Test(expected = NoRightsException.class)
	public void testExecute_Contractor() throws Exception {
		when(permissions.isContractor()).thenReturn(true);

		requestNewContractorAccount.execute();
	}

	@Test
	public void testExecute_PicsEmployee() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
	}

	@Test
	public void testExecute_OperatorCorporate() throws Exception {
		when(permissions.isOperatorCorporate()).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
	}

	@Test
	public void testExecute_DefaultStatus() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(AccountStatus.Requested, requestNewContractorAccount.getRequestedContractor().getStatus());
	}

	@Test
	public void testExecute_ExistingStatus() throws Exception {
		contractor.setStatus(AccountStatus.Deactivated);

		when(permissions.isPicsEmployee()).thenReturn(true);

		requestNewContractorAccount.setRequestedContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(AccountStatus.Deactivated, requestNewContractorAccount.getRequestedContractor().getStatus());
	}

	@Test
	public void testExecute_ExistingRequestedByOtherOperator() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();
		OperatorAccount otherOperator = EntityFactory.makeOperator();

		contractor.setRequestedBy(otherOperator);
		contractor.setStatus(AccountStatus.Requested);

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(entityManager.find(OperatorAccount.class, operator.getId())).thenReturn(operator);
		when(query.getResultList()).thenReturn(Collections.emptyList());
		setPermissionsAsOperator(operator);

		requestNewContractorAccount.setRequestedContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(operator, requestNewContractorAccount.getRequestRelationship().getOperatorAccount());
		assertEquals(otherOperator, requestNewContractorAccount.getRequestedContractor().getRequestedBy());
	}

	// If we're logged in as the operator, find the my relationship with
	// this request
	@Test
	public void testExecute_FindOperatorRelationship() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();
		OperatorAccount otherOperator = EntityFactory.makeOperator();
		ContractorOperator contractorOperator = EntityFactory.addContractorOperator(contractor, operator);
		EntityFactory.addContractorOperator(contractor, otherOperator);

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(Collections.emptyList());
		setPermissionsAsOperator(operator);

		requestNewContractorAccount.setRequestedContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(contractorOperator, requestNewContractorAccount.getRequestRelationship());
	}

	// If I'm a corporate, find all the relationships this contractor has
	// with my children and give me a table to add/remove relationships
	@Test
	public void testExecute_FindCorporateRelationships() throws Exception {
		OperatorAccount corporate = EntityFactory.makeOperator();
		OperatorAccount child1 = EntityFactory.makeOperator();
		OperatorAccount child2 = EntityFactory.makeOperator();
		OperatorAccount otherOperator = EntityFactory.makeOperator();
		EntityFactory.makeFacility(child1, corporate);
		EntityFactory.makeFacility(child2, corporate);

		Set<Integer> operatorChildren = new HashSet<Integer>();
		operatorChildren.add(child1.getId());
		operatorChildren.add(child2.getId());

		when(permissions.getOperatorChildren()).thenReturn(operatorChildren);

		ContractorOperator link1 = EntityFactory.addContractorOperator(contractor, child1);
		ContractorOperator link2 = EntityFactory.addContractorOperator(contractor, child2);
		ContractorOperator link3 = EntityFactory.addContractorOperator(contractor, otherOperator);

		when(permissions.getAccountId()).thenReturn(corporate.getId());
		when(permissions.isCorporate()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);

		requestNewContractorAccount.setRequestedContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertTrue(requestNewContractorAccount.getVisibleRelationships().contains(link1));
		assertTrue(requestNewContractorAccount.getVisibleRelationships().contains(link2));
		assertFalse(requestNewContractorAccount.getVisibleRelationships().contains(link3));
	}

	// If I'm a PICS employee, find all the relationships and list them in a
	// table with add/remove abilities
	@Test
	public void testExecute_FindAllRelationships() throws Exception {
		OperatorAccount operator1 = EntityFactory.makeOperator();
		OperatorAccount operator2 = EntityFactory.makeOperator();

		ContractorOperator link1 = EntityFactory.addContractorOperator(contractor, operator1);
		ContractorOperator link2 = EntityFactory.addContractorOperator(contractor, operator2);

		when(permissions.isPicsEmployee()).thenReturn(true);

		requestNewContractorAccount.setRequestedContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertTrue(requestNewContractorAccount.getVisibleRelationships().contains(link1));
		assertTrue(requestNewContractorAccount.getVisibleRelationships().contains(link2));
	}

	// If I save a new request send the request an email. If I'm an operator I
	// should be set as the requesting operator. ALL entities should be new.
	@Test
	public void testSave_NewRequestOperator() throws Exception {
		User info = EntityFactory.makeUser();
		info.setId(User.INFO_AT_PICSAUDITING);

		User primaryContact = requestNewContractorAccount.getPrimaryContact();
		primaryContact.setEmail("test@test.com");

		OperatorAccount operator = EntityFactory.makeOperator();
		ContractorOperator relationship = requestNewContractorAccount.getRequestRelationship();
		relationship.setOperatorAccount(operator);

		User requestedBy = EntityFactory.makeUser();
		relationship.setRequestedBy(requestedBy);

		ContractorRegistrationRequest request = Whitebox.getInternalState(requestNewContractorAccount, "legacyRequest");

		contractor.setId(0);

		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				((BaseTable) args[0]).setId(1);
				return null;
			}
		}).when(entityManager).persist(any(BaseTable.class));

		when(emailBuilder.build()).thenReturn(email);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(entityManager.find(OperatorAccount.class, operator.getId())).thenReturn(operator);
		when(entityManager.find(User.class, info.getId())).thenReturn(info);
		when(entityManager.find(User.class, requestedBy.getId())).thenReturn(requestedBy);
		when(entityManager.merge(any())).thenReturn(contractor, primaryContact, relationship, request, email, request,
				contractor);
		when(permissions.getUserId()).thenReturn(requestedBy.getId());
		when(query.getResultList()).thenReturn(Collections.emptyList());
		setPermissionsAsOperator(operator);

		requestNewContractorAccount.setPrimaryContact(primaryContact);
		requestNewContractorAccount.setRequestedContractor(contractor);
		requestNewContractorAccount.setRequestRelationship(relationship);

		assertEquals(PicsActionSupport.REDIRECT, requestNewContractorAccount.save());
		assertEquals(operator, requestNewContractorAccount.getRequestedContractor().getRequestedBy());

		verify(emailBuilder).build();
		verify(emailSender).send(email);
		verify(entityManager, times(5)).persist(any(BaseTable.class));
		verify(entityManager).merge(any(BaseTable.class));
	}

	// If I save a new request send the request an email. If I'm an operator I
	// should be set as the requesting operator. ALL entities should be new.
	@Test
	public void testSave_ExistingRequestOperator() throws Exception {
		User info = EntityFactory.makeUser();
		info.setId(User.INFO_AT_PICSAUDITING);

		User primaryContact = EntityFactory.makeUser();
		primaryContact.setEmail("test@test.com");
		contractor.setPrimaryContact(primaryContact);

		OperatorAccount operator = EntityFactory.makeOperator();
		ContractorOperator relationship = requestNewContractorAccount.getRequestRelationship();
		relationship.setOperatorAccount(operator);

		User requestedBy = EntityFactory.makeUser();
		relationship.setRequestedBy(requestedBy);

		OperatorAccount otherOperator = EntityFactory.makeOperator();
		contractor.setRequestedBy(otherOperator);

		ContractorRegistrationRequest request = Whitebox.getInternalState(requestNewContractorAccount, "legacyRequest");
		request.setId(1);

		when(emailBuilder.build()).thenReturn(email);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(entityManager.find(OperatorAccount.class, operator.getId())).thenReturn(operator);
		when(entityManager.find(User.class, info.getId())).thenReturn(info);
		when(entityManager.find(User.class, requestedBy.getId())).thenReturn(requestedBy);
		when(entityManager.merge(any(BaseTable.class))).thenReturn(contractor, primaryContact, request, contractor);
		when(permissions.getUserId()).thenReturn(requestedBy.getId());
		when(query.getResultList()).thenReturn(Collections.emptyList());
		setPermissionsAsOperator(operator);

		requestNewContractorAccount.setPrimaryContact(primaryContact);
		requestNewContractorAccount.setRequestedContractor(contractor);
		requestNewContractorAccount.setRequestRelationship(relationship);

		assertEquals(PicsActionSupport.REDIRECT, requestNewContractorAccount.save());
		assertEquals(otherOperator, requestNewContractorAccount.getRequestedContractor().getRequestedBy());

		verify(emailBuilder).build();
		verify(emailSender).send(email);
		// Save the new ContractorOperator object
		verify(entityManager, atLeastOnce()).persist(any(BaseTable.class));
		// Merge the existing contractor, user, and request objects
		verify(entityManager, times(4)).merge(any(BaseTable.class));
	}

	@Test
	public void testIsContactable() {
		when(permissions.isOperatorCorporate()).thenReturn(true);
		assertFalse(requestNewContractorAccount.isContactable());

		when(permissions.isOperatorCorporate()).thenReturn(false);
		// Requested contractor id = 0
		assertFalse(requestNewContractorAccount.isContactable());

		requestNewContractorAccount.setRequestedContractor(contractor);
		// Missing primary user
		assertFalse(requestNewContractorAccount.isContactable());

		User primaryContact = EntityFactory.makeUser();
		contractor.setPrimaryContact(primaryContact);
		// Missing phone number
		assertFalse(requestNewContractorAccount.isContactable());

		primaryContact.setPhone("Phoe");
		assertTrue(requestNewContractorAccount.isContactable());
	}

	@Test
	public void testGetOperatorUsers() {
		List<User> opUserList1 = new ArrayList<User>();
		User opUser1 = EntityFactory.makeUser();
		opUserList1.add(opUser1);

		List<User> opUserList2 = new ArrayList<User>();
		User opUser2 = EntityFactory.makeUser();
		opUserList2.add(opUser2);

		OperatorAccount operator = EntityFactory.makeOperator();
		requestNewContractorAccount.getRequestRelationship().setOperatorAccount(operator);

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(opUserList1, opUserList2);

		assertTrue(requestNewContractorAccount.getOperatorUsers().containsAll(opUserList1));
		assertTrue(requestNewContractorAccount.getOperatorUsers().containsAll(opUserList2));
	}

	@Test
	public void testGetApplicableStatuses() {
		assertTrue(requestNewContractorAccount.getApplicableStatuses().contains(
				ContractorRegistrationRequestStatus.Active));
		assertTrue(requestNewContractorAccount.getApplicableStatuses().contains(
				ContractorRegistrationRequestStatus.Hold));
		assertTrue(requestNewContractorAccount.getApplicableStatuses().contains(
				ContractorRegistrationRequestStatus.ClosedUnsuccessful));
		assertEquals(3, requestNewContractorAccount.getApplicableStatuses().size());
	}

	@Test
	public void testSetRequestStatus() throws Exception {
		requestNewContractorAccount.setRequestedContractor(contractor);

		Whitebox.invokeMethod(requestNewContractorAccount, "setRequestStatus");
		assertEquals(ContractorRegistrationRequestStatus.ClosedSuccessful, requestNewContractorAccount.getStatus());

		contractor.contactByPhone();
		contractor.setStatus(AccountStatus.Pending);
		Whitebox.invokeMethod(requestNewContractorAccount, "setRequestStatus");
		assertEquals(ContractorRegistrationRequestStatus.ClosedContactedSuccessful,
				requestNewContractorAccount.getStatus());

		contractor.setStatus(AccountStatus.Requested);
		Whitebox.invokeMethod(requestNewContractorAccount, "setRequestStatus");
		assertEquals(ContractorRegistrationRequestStatus.Active, requestNewContractorAccount.getStatus());

		contractor.setFollowUpDate(new Date());
		Whitebox.invokeMethod(requestNewContractorAccount, "setRequestStatus");
		assertEquals(ContractorRegistrationRequestStatus.Hold, requestNewContractorAccount.getStatus());

		contractor.setStatus(AccountStatus.Deactivated);
		Whitebox.invokeMethod(requestNewContractorAccount, "setRequestStatus");
		assertEquals(ContractorRegistrationRequestStatus.ClosedUnsuccessful, requestNewContractorAccount.getStatus());

		contractor.setStatus(AccountStatus.Deleted);
		Whitebox.invokeMethod(requestNewContractorAccount, "setRequestStatus");
		assertEquals(ContractorRegistrationRequestStatus.ClosedUnsuccessful, requestNewContractorAccount.getStatus());
	}

	@Test
	public void testLoadTags() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();
		setPermissionsAsOperator(operator);

		OperatorTag tag = new OperatorTag();
		tag.setOperator(operator);
		tag.setTag("Tag");

		List<OperatorTag> tags = new ArrayList<OperatorTag>();
		tags.add(tag);

		ContractorTag contractorTag = new ContractorTag();
		contractorTag.setContractor(contractor);
		contractorTag.setTag(tag);

		contractor.getOperatorTags().add(contractorTag);

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(tags);

		requestNewContractorAccount.setRequestedContractor(contractor);

		Whitebox.invokeMethod(requestNewContractorAccount, "loadTags");

		assertTrue(requestNewContractorAccount.getOperatorTags().isEmpty());
		assertTrue(requestNewContractorAccount.getRequestedTags().contains(tag));
	}

	@Test
	public void testLoadLegacyRequest() throws Exception {
		List<ContractorRegistrationRequest> requests = new ArrayList<ContractorRegistrationRequest>();
		ContractorRegistrationRequest request = new ContractorRegistrationRequest();
		request.setId(1);
		requests.add(request);

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(requests);

		requestNewContractorAccount.setRequestedContractor(contractor);

		Whitebox.invokeMethod(requestNewContractorAccount, "loadLegacyRequest");
		assertEquals(request, Whitebox.getInternalState(requestNewContractorAccount, "legacyRequest"));
	}

	@Test
	public void testLoadLegacyRequest_Empty() throws Exception {
		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(Collections.emptyList());

		Object legacyRequest = Whitebox.getInternalState(requestNewContractorAccount, "legacyRequest");

		requestNewContractorAccount.setRequestedContractor(contractor);

		Whitebox.invokeMethod(requestNewContractorAccount, "loadLegacyRequest");
		assertEquals(legacyRequest, Whitebox.getInternalState(requestNewContractorAccount, "legacyRequest"));
	}

	@Test
	public void testGetContractorLetter_FormOnCorporate() throws Exception {
		OperatorAccount corporate = EntityFactory.makeOperator();
		OperatorForm form = createOperatorForm(corporate);

		OperatorAccount operator = EntityFactory.makeOperator();
		OperatorForm otherForm = createOperatorForm(operator);
		otherForm.setFormName("Form");
		operator.setParent(corporate);

		requestNewContractorAccount.getRequestRelationship().setOperatorAccount(operator);

		assertEquals(form, Whitebox.invokeMethod(requestNewContractorAccount, "getContractorLetter"));
	}

	@Test
	public void testSaveNoteIfContacted_NoContactType() throws Exception {
		ContractorRegistrationRequest legacyRequest = Whitebox.getInternalState(requestNewContractorAccount,
				"legacyRequest");

		Whitebox.invokeMethod(requestNewContractorAccount, "saveNoteIfContacted");

		assertEquals(0, requestNewContractorAccount.getRequestedContractor().getTotalContactCount());
		assertEquals(0, legacyRequest.getContactCount());

		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testSaveNoteIfContacted_ContactTypes() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();

		ContractorOperator relationship = EntityFactory.addContractorOperator(contractor, operator);

		User user = EntityFactory.makeUser();

		when(entityManager.find(User.class, user.getId())).thenReturn(user);
		when(permissions.getUserId()).thenReturn(user.getId());

		ContractorRegistrationRequest legacyRequest = Whitebox.getInternalState(requestNewContractorAccount,
				"legacyRequest");

		requestNewContractorAccount.setContactType(RequestContactType.EMAIL);
		requestNewContractorAccount.setRequestRelationship(relationship);

		Whitebox.invokeMethod(requestNewContractorAccount, "saveNoteIfContacted");

		assertEquals(1, requestNewContractorAccount.getRequestedContractor().getTotalContactCount());
		assertEquals(1, legacyRequest.getContactCount());

		requestNewContractorAccount.setContactType(RequestContactType.PHONE);

		Whitebox.invokeMethod(requestNewContractorAccount, "saveNoteIfContacted");

		assertEquals(2, requestNewContractorAccount.getRequestedContractor().getTotalContactCount());
		assertEquals(2, legacyRequest.getContactCount());
	}

	@Test
	public void testRemoveUnneededTags() throws Exception {
		List<ContractorTag> viewable = new ArrayList<ContractorTag>();
		OperatorTag tag = new OperatorTag();
		tag.setTag("Tag");

		ContractorTag contractorTag = new ContractorTag();
		contractorTag.setTag(tag);

		viewable.add(contractorTag);

		List<OperatorTag> otherTags = new ArrayList<OperatorTag>();
		OperatorTag other = new OperatorTag();
		other.setTag("Other");
		otherTags.add(other);

		requestNewContractorAccount.setRequestedTags(otherTags);

		Whitebox.invokeMethod(requestNewContractorAccount, "removeUnneededTags", viewable);

		assertTrue(viewable.isEmpty());
		verify(entityManager).remove(any(BaseTable.class));
	}

	@Test
	public void testRemoveExistingTagsFromSelected() throws Exception {
		List<ContractorTag> viewable = new ArrayList<ContractorTag>();
		OperatorTag tag = new OperatorTag();
		tag.setTag("Tag");

		ContractorTag contractorTag = new ContractorTag();
		contractorTag.setTag(tag);

		viewable.add(contractorTag);

		List<OperatorTag> requested = new ArrayList<OperatorTag>();
		OperatorTag other = new OperatorTag();
		other.setTag("Other");
		requested.add(other);
		requested.add(tag);

		requestNewContractorAccount.setRequestedTags(requested);

		Whitebox.invokeMethod(requestNewContractorAccount, "removeExistingTagsFromSelected", viewable);

		assertEquals(1, requestNewContractorAccount.getRequestedTags().size());
		assertEquals(other, requestNewContractorAccount.getRequestedTags().get(0));
	}

	@Test
	public void testAddRemainingTags() throws Exception {
		OperatorTag tag = new OperatorTag();
		tag.setTag("Tag");

		requestNewContractorAccount.getRequestedTags().add(tag);

		Whitebox.invokeMethod(requestNewContractorAccount, "addRemainingTags");

		assertFalse(requestNewContractorAccount.getRequestedContractor().getOperatorTags().isEmpty());
		assertEquals(tag, requestNewContractorAccount.getRequestedContractor().getOperatorTags().get(0).getTag());

		verify(entityManager).persist(any(ContractorTag.class));
	}

	@Test
	public void testUpdateAccounWithLegacyChanges() throws Exception {
		contractor = mock(ContractorAccount.class);
		OperatorAccount operator = mock(OperatorAccount.class);
		User user = mock(User.class);
		ContractorRegistrationRequest request = mock(ContractorRegistrationRequest.class);

		List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
		contractorOperators.add(relationship);

		when(contractor.getId()).thenReturn(1);
		when(contractor.getOperators()).thenReturn(contractorOperators);
		when(relationship.getId()).thenReturn(1);
		when(relationship.getOperatorAccount()).thenReturn(operator);
		when(request.getId()).thenReturn(1);
		when(request.getRequestedBy()).thenReturn(operator);
		when(request.isCreatedUpdatedAfter(any(ContractorAccount.class))).thenReturn(true);
		when(user.getId()).thenReturn(1);

		requestNewContractorAccount.setRequestedContractor(contractor);
		requestNewContractorAccount.setRequestRelationship(relationship);
		Whitebox.setInternalState(requestNewContractorAccount, "legacyRequest", request);
		Whitebox.setInternalState(requestNewContractorAccount, "primaryContact", user);

		Whitebox.invokeMethod(requestNewContractorAccount, "updateAccountWithLegacyChanges");

		verify(entityManager, times(3)).merge(any(BaseTable.class));
	}

	@Test
	public void testUpdateAccounWithLegacyChanges_NotUpdated() throws Exception {
		ContractorRegistrationRequest request = mock(ContractorRegistrationRequest.class);

		when(request.getId()).thenReturn(1);
		when(request.isCreatedUpdatedAfter(any(ContractorAccount.class))).thenReturn(false);

		Whitebox.setInternalState(requestNewContractorAccount, "legacyRequest", request);
		Whitebox.invokeMethod(requestNewContractorAccount, "updateAccountWithLegacyChanges");

		verify(entityManager, never()).merge(any(BaseTable.class));
	}

	@Test
	public void testUpdateAccounWithLegacyChanges_NewLegacyRequest() throws Exception {
		ContractorRegistrationRequest request = mock(ContractorRegistrationRequest.class);

		when(request.getId()).thenReturn(0);
		when(request.isCreatedUpdatedAfter(any(ContractorAccount.class))).thenReturn(true);

		Whitebox.setInternalState(requestNewContractorAccount, "legacyRequest", request);
		Whitebox.invokeMethod(requestNewContractorAccount, "updateAccountWithLegacyChanges");

		verify(entityManager, never()).merge(any(BaseTable.class));
	}

	private OperatorForm createOperatorForm(OperatorAccount operator) {
		OperatorForm form = new OperatorForm();
		form.setFormName("*");
		operator.getOperatorForms().add(form);

		return form;
	}

	private void setPermissionsAsOperator(OperatorAccount operator) {
		when(permissions.getAccountId()).thenReturn(operator.getId());
		when(permissions.isOperator()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);
	}
}