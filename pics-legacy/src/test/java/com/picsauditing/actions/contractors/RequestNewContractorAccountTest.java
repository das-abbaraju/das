package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.model.user.UserManagementService;
import com.picsauditing.service.RequestNewContractorService;
import com.picsauditing.service.account.ContractorOperatorService;
import com.picsauditing.service.account.events.ContractorOperatorEventType;
import com.picsauditing.service.notes.NoteService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.contractors.RequestNewContractorAccount.RequestContactType;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.URLUtils;

@SuppressWarnings("deprecation")
public class RequestNewContractorAccountTest extends PicsTranslationTest {
	private RequestNewContractorAccount requestNewContractorAccount;
	private PicsTestUtil picsTestUtil;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private ContractorOperator relationship;
	@Mock
	private ContractorRegistrationRequest request;
	@Mock
	private EntityManager entityManager;
	@Mock
	private FeatureToggle featureToggle;
	@Mock
	private OperatorAccount operator;
	@Mock
	private Permissions permissions;
	@Mock
	private Query query;
	@Mock
	private RegistrationRequestEmailHelper emailHelper;
	@Mock
	private User user;
	@Mock
	private URLUtils urlUtil;
    @Mock
    private RequestNewContractorService requestNewContractorService;
    @Mock
    private ContractorAccountDAO contractorAccountDAO;
    @Mock
    private NoteDAO noteDao;
    @Mock
    private ContractorOperatorService contractorOperatorService;
    @Mock
    private NoteService noteService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);


		requestNewContractorAccount = new RequestNewContractorAccount();
		picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(requestNewContractorAccount, entityManager);

		when(contractor.getId()).thenReturn(1);
		when(contractor.getStatus()).thenReturn(AccountStatus.Requested);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(Collections.emptyList());
		when(relationship.getOperatorAccount()).thenReturn(operator);
		when(urlUtil.getActionUrl(anyString(), any(HashMap.class))).thenReturn("URL");

        when(requestNewContractorService.populateRequestedContractor(eq(contractor), eq(operator), any(RequestContactType.class), anyString())).thenReturn(contractor);
        when(requestNewContractorService.saveRequestedContractor(contractor)).thenReturn(contractor);

        when(requestNewContractorService.populatePrimaryContact(contractor, user)).thenReturn(user);
        when(requestNewContractorService.savePrimaryContact(user)).thenReturn(user);

        when(requestNewContractorService.populateRelationship(contractor, relationship)).thenReturn(relationship);
        when(requestNewContractorService.saveRelationship(relationship)).thenReturn(relationship);

		Whitebox.setInternalState(requestNewContractorAccount, "emailHelper", emailHelper);
		Whitebox.setInternalState(requestNewContractorAccount, "featureToggle", featureToggle);
		Whitebox.setInternalState(requestNewContractorAccount, "permissions", permissions);
		Whitebox.setInternalState(requestNewContractorAccount, "urlUtil", urlUtil);
        Whitebox.setInternalState(requestNewContractorAccount, "requestNewContractorService", requestNewContractorService);
        Whitebox.setInternalState(requestNewContractorAccount, "contractorOperatorService", contractorOperatorService);
        Whitebox.setInternalState(requestNewContractorAccount, "noteService", noteService);
	}

	@Test
	public void testExecute_DefaultStatus() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(AccountStatus.Requested, requestNewContractorAccount.getContractor().getStatus());
	}

	@Test
	public void testExecute_ExistingStatus() throws Exception {
		when(contractor.getStatus()).thenReturn(AccountStatus.Deactivated);
		when(permissions.isPicsEmployee()).thenReturn(true);

		requestNewContractorAccount.setContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(AccountStatus.Deactivated, requestNewContractorAccount.getContractor().getStatus());
	}

	@Test
	public void testExecute_ExistingRequestedByOtherOperator() throws Exception {
		OperatorAccount otherOperator = mock(OperatorAccount.class);

		setPermissionsAsOperator();
		when(contractor.getRequestedBy()).thenReturn(otherOperator);

		requestNewContractorAccount.setContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(operator, requestNewContractorAccount.getRequestRelationship().getOperatorAccount());
		assertEquals(otherOperator, requestNewContractorAccount.getContractor().getRequestedBy());
	}

	// If we're logged in as the operator, find the my relationship with
	// this request
	@Test
	public void testExecute_FindOperatorRelationship() throws Exception {
		List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
		operators.add(relationship);

		setPermissionsAsOperator();
		when(contractor.getOperators()).thenReturn(operators);

		requestNewContractorAccount.setContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(relationship, requestNewContractorAccount.getRequestRelationship());
	}

	// If I'm a corporate, find all the relationships this contractor has
	// with my children and give me a table to add/remove relationships
	@Test
	public void testExecute_FindCorporateRelationships() throws Exception {
		OperatorAccount corporate = mock(OperatorAccount.class);
		OperatorAccount child1 = mock(OperatorAccount.class);
		OperatorAccount child2 = mock(OperatorAccount.class);
		OperatorAccount otherOperator = mock(OperatorAccount.class);

		Set<Integer> operatorChildren = new HashSet<Integer>();
		operatorChildren.add(2);
		operatorChildren.add(3);

		ContractorOperator link1 = mock(ContractorOperator.class);
		ContractorOperator link2 = mock(ContractorOperator.class);
		ContractorOperator linkOther = mock(ContractorOperator.class);

		List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
		operators.add(link1);
		operators.add(link2);
		operators.add(linkOther);

		when(contractor.getOperators()).thenReturn(operators);
		when(corporate.getId()).thenReturn(1);
		when(child1.getId()).thenReturn(2);
		when(child2.getId()).thenReturn(3);
		when(link1.getOperatorAccount()).thenReturn(child1);
		when(link2.getOperatorAccount()).thenReturn(child2);
		when(linkOther.getOperatorAccount()).thenReturn(otherOperator);
		when(otherOperator.getId()).thenReturn(4);
		when(permissions.getAccountId()).thenReturn(1);
		when(permissions.getOperatorChildren()).thenReturn(operatorChildren);
		when(permissions.isCorporate()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);

		requestNewContractorAccount.setContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertTrue(requestNewContractorAccount.getVisibleRelationships().contains(link1));
		assertTrue(requestNewContractorAccount.getVisibleRelationships().contains(link2));
		assertFalse(requestNewContractorAccount.getVisibleRelationships().contains(linkOther));
	}

	// If I'm a PICS employee, find all the relationships and list them in a
	// table with add/remove abilities
	@Test
	public void testExecute_FindAllRelationships() throws Exception {
		OperatorAccount operator1 = mock(OperatorAccount.class);
		OperatorAccount operator2 = mock(OperatorAccount.class);

		ContractorOperator link1 = mock(ContractorOperator.class);
		ContractorOperator link2 = mock(ContractorOperator.class);

		List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
		operators.add(link1);
		operators.add(link2);

		when(contractor.getOperators()).thenReturn(operators);
		when(link1.getOperatorAccount()).thenReturn(operator1);
		when(link2.getOperatorAccount()).thenReturn(operator2);
		when(permissions.isPicsEmployee()).thenReturn(true);

		requestNewContractorAccount.setContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertTrue(requestNewContractorAccount.getVisibleRelationships().contains(link1));
		assertTrue(requestNewContractorAccount.getVisibleRelationships().contains(link2));
	}

	// If I save a new request send the request an email. If I'm an operator I
	// should be set as the requesting operator. ALL entities should be new.
	@Test
	public void testSave_NewRequestOperator() throws Exception {
		User loggedIn = mock(User.class);

		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return args[0];
			}
		}).when(entityManager).persist(any(BaseTable.class));

		setPermissionsAsOperator();
		when(contractor.getId()).thenReturn(0);
		when(contractor.getNaics()).thenReturn(mock(Naics.class));
		when(contractor.getPrimaryContact()).thenReturn(user);
		when(entityManager.find(eq(User.class), anyInt())).thenReturn(loggedIn);
		when(loggedIn.getId()).thenReturn(1);
		when(loggedIn.getName()).thenReturn("Logged in user");
		when(permissions.getUserId()).thenReturn(1);
		when(relationship.getOperatorAccount()).thenReturn(operator);
		when(user.getEmail()).thenReturn("test@test.com");

		requestNewContractorAccount.setPrimaryContact(user);
		requestNewContractorAccount.setContractor(contractor);
		requestNewContractorAccount.setRequestRelationship(relationship);

		assertEquals(PicsActionSupport.REDIRECT, requestNewContractorAccount.save());

		verify(contractorOperatorService).publishEvent(relationship, ContractorOperatorEventType.RegistrationRequest, 1);
	}

	// As another requesting operator, another request email should be sent out
	// because this is a different request. A new legacy request should also be
	// created.
	@Test
	public void testSave_ExistingRequestOperator() throws Exception {
		OperatorAccount otherOperator = mock(OperatorAccount.class);

		relationship = requestNewContractorAccount.getRequestRelationship();
		relationship.setOperatorAccount(operator);

		List<ContractorRegistrationRequest> requests = new ArrayList<ContractorRegistrationRequest>();
		requests.add(request);

		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return args[0];
			}
		}).when(entityManager).persist(any(BaseTable.class));

		setPermissionsAsOperator();
		when(contractor.getPrimaryContact()).thenReturn(user);
		when(entityManager.find(eq(User.class), anyInt())).thenReturn(user);
		when(entityManager.merge(any(BaseTable.class))).thenReturn(contractor, contractor, contractor);
		when(permissions.getUserId()).thenReturn(1);
		when(query.getResultList()).thenReturn(requests);
		when(request.getRequestedBy()).thenReturn(otherOperator);
		when(user.getId()).thenReturn(1);
        when(requestNewContractorService.populateRelationship(contractor, relationship)).thenReturn(relationship);
        when(requestNewContractorService.saveRelationship(relationship)).thenReturn(relationship);

		requestNewContractorAccount.setPrimaryContact(user);
		requestNewContractorAccount.setContractor(contractor);
		requestNewContractorAccount.setRequestRelationship(relationship);

		assertEquals(PicsActionSupport.REDIRECT, requestNewContractorAccount.save());

        verify(contractorOperatorService).publishEvent(relationship, ContractorOperatorEventType.RegistrationRequest, 1);
	}

	@Test
	public void testIsContactable() {
		when(permissions.isOperatorCorporate()).thenReturn(true);
		assertFalse(requestNewContractorAccount.isContactable());

		when(permissions.isOperatorCorporate()).thenReturn(false);
		// Requested contractor id = 0
		assertFalse(requestNewContractorAccount.isContactable());

		requestNewContractorAccount.setContractor(contractor);
		// Missing primary user
		assertFalse(requestNewContractorAccount.isContactable());

		when(contractor.getPrimaryContact()).thenReturn(user);
		// Missing phone number
		assertFalse(requestNewContractorAccount.isContactable());

		when(user.getPhone()).thenReturn("Phone");
		assertTrue(requestNewContractorAccount.isContactable());
	}

	@Test
	public void testGetOperatorUsers() {
		List<User> opUserList1 = new ArrayList<User>();
		User opUser1 = mock(User.class);
		opUserList1.add(opUser1);

		List<User> opUserList2 = new ArrayList<User>();
		User opUser2 = mock(User.class);
		opUserList2.add(opUser2);

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(opUserList1, opUserList2);
		when(relationship.getOperatorAccount()).thenReturn(operator);

		requestNewContractorAccount.setRequestRelationship(relationship);

		assertTrue(requestNewContractorAccount.getOperatorUsers().containsAll(opUserList1));
		assertTrue(requestNewContractorAccount.getOperatorUsers().containsAll(opUserList2));
	}

	@Test
	public void testLoadTags_Permissions() throws Exception {
		OperatorTag tag = mock(OperatorTag.class);
		List<OperatorTag> tags = new ArrayList<OperatorTag>();
		tags.add(tag);

		ContractorTag contractorTag = mock(ContractorTag.class);
		List<ContractorTag> contractorTags = new ArrayList<ContractorTag>();
		contractorTags.add(contractorTag);

		setPermissionsAsOperator();
		when(contractor.getOperatorTags()).thenReturn(contractorTags);
		when(contractorTag.getContractor()).thenReturn(contractor);
		when(contractorTag.getTag()).thenReturn(tag);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(tags);
		when(tag.getOperator()).thenReturn(operator);
		when(tag.getTag()).thenReturn("Tag");

		requestNewContractorAccount.setContractor(contractor);

		Whitebox.invokeMethod(requestNewContractorAccount, "loadTags");

		assertTrue(requestNewContractorAccount.getOperatorTags().isEmpty());
		assertTrue(requestNewContractorAccount.getRequestedTags().contains(tag));
	}

	@Test
	public void testLoadTags_Relationship() throws Exception {
		OperatorTag tag = mock(OperatorTag.class);
		List<OperatorTag> tags = new ArrayList<OperatorTag>();
		tags.add(tag);

		ContractorTag contractorTag = mock(ContractorTag.class);
		List<ContractorTag> contractorTags = new ArrayList<ContractorTag>();
		contractorTags.add(contractorTag);

		when(contractor.getOperatorTags()).thenReturn(contractorTags);
		when(contractorTag.getContractor()).thenReturn(contractor);
		when(contractorTag.getTag()).thenReturn(tag);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(operator.getId()).thenReturn(1);
		when(operator.getTags()).thenReturn(tags);
		when(query.getResultList()).thenReturn(tags);
		when(relationship.getOperatorAccount()).thenReturn(operator);
		when(tag.getOperator()).thenReturn(operator);
		when(tag.getTag()).thenReturn("Tag");

		requestNewContractorAccount.setContractor(contractor);
		requestNewContractorAccount.setRequestRelationship(relationship);

		Whitebox.invokeMethod(requestNewContractorAccount, "loadTags");

		assertTrue(requestNewContractorAccount.getOperatorTags().isEmpty());
		assertTrue(requestNewContractorAccount.getRequestedTags().contains(tag));
	}

	@Test
	public void testSaveNoteIfContacted_NoContactType() throws Exception {
		Whitebox.invokeMethod(requestNewContractorAccount, "saveNoteIfContacted");

		assertEquals(0, requestNewContractorAccount.getContractor().getTotalContactCount());

		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testUpdateLastContactedFieldsOnContractorIfContacted_ContactTypes() throws Exception {
		when(entityManager.find(eq(User.class), anyInt())).thenReturn(user);
		when(permissions.getUserId()).thenReturn(1);
		when(relationship.getOperatorAccount()).thenReturn(operator);

		requestNewContractorAccount.setContactType(RequestContactType.EMAIL);
		requestNewContractorAccount.setRequestRelationship(relationship);

		Whitebox.invokeMethod(requestNewContractorAccount, "updateLastContactedFieldsOnContractorIfContacted");

		assertEquals(1, requestNewContractorAccount.getContractor().getTotalContactCount());

		requestNewContractorAccount.setContactType(RequestContactType.PHONE);

		Whitebox.invokeMethod(requestNewContractorAccount, "updateLastContactedFieldsOnContractorIfContacted");

		assertEquals(2, requestNewContractorAccount.getContractor().getTotalContactCount());
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

		assertFalse(requestNewContractorAccount.getContractor().getOperatorTags().isEmpty());
		assertEquals(tag, requestNewContractorAccount.getContractor().getOperatorTags().get(0).getTag());

		verify(entityManager).persist(any(ContractorTag.class));
	}

	@Test
	public void testIsContactNoteMissing() {
		testContactType(RequestContactType.EMAIL);
		testContactType(RequestContactType.PHONE);
		testContactType(RequestContactType.DECLINED);

		requestNewContractorAccount.setContactType(null);
		assertFalse(requestNewContractorAccount.isContactNoteMissing());

		requestNewContractorAccount.setContactNote("Test");
		assertFalse(requestNewContractorAccount.isContactNoteMissing());
	}

    @Test
    public void testResolveDuplicate() {
        int requestRelationshipId = 1;
        int duplicateContractorId = 2;
        ContractorAccount oldContractor = ContractorAccount.builder().id(requestRelationshipId).build();
        ContractorOperator requestRelationship = ContractorOperator.builder()
                .contractor(oldContractor)
                .build();
        ContractorAccount duplicateContractor = ContractorAccount.builder()
                .primaryContact(User.builder().build())
                .id(duplicateContractorId)
                .build();
        requestNewContractorAccount.setRequestRelationship(requestRelationship);
        requestNewContractorAccount.setDuplicateContractor(duplicateContractor);

        assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.resolveDuplicate());
        assertEquals(duplicateContractorId, requestNewContractorAccount.getContractor().getId());
        assertEquals(duplicateContractor, requestNewContractorAccount.getRequestRelationship().getContractorAccount());
        assertEquals(duplicateContractor.getPrimaryContact(), requestNewContractorAccount.getPrimaryContact());
        assertEquals(RequestNewContractorAccount.REASON_REQUEST_DECLINED, oldContractor.getReason());
        assertEquals(RequestNewContractorAccount.DUPLICATE_CONTRACTOR_NAME + duplicateContractor.getId(), oldContractor.getName());
        assertEquals(AccountStatus.Deleted, oldContractor.getStatus());
    }

    @Test
    public void testResolveDuplicate_NullDuplicateContractor() {
        when(translationService.getText(eq(RequestNewContractorAccount.DUPLICATE_ID_MISSING_ERROR_MESSAGE),
                any(Locale.class), anyObject())).thenReturn(RequestNewContractorAccount.DUPLICATE_ID_MISSING_ERROR_MESSAGE);
        when(translationService.hasKey(eq(RequestNewContractorAccount.DUPLICATE_ID_MISSING_ERROR_MESSAGE),
                any(Locale.class))).thenReturn(true);

        assertEquals(PicsActionSupport.INPUT, requestNewContractorAccount.resolveDuplicate());
        assertEquals(RequestNewContractorAccount.DUPLICATE_ID_MISSING_ERROR_MESSAGE,
                requestNewContractorAccount.getFieldErrors().get(RequestNewContractorAccount.DUPLICATE_CONTRACTOR_FIELD_NAME).get(0));
    }

    @Test
    public void testResolveDuplicate_SameDuplicatedContractorId() {
        when(translationService.getText(eq(RequestNewContractorAccount.SAME_DUPLICATED_CONTRACTOR_ID_ERROR_MESSAGE),
                any(Locale.class), anyObject())).thenReturn(RequestNewContractorAccount.SAME_DUPLICATED_CONTRACTOR_ID_ERROR_MESSAGE);
        when(translationService.hasKey(eq(RequestNewContractorAccount.SAME_DUPLICATED_CONTRACTOR_ID_ERROR_MESSAGE),
                any(Locale.class))).thenReturn(true);

        int contractorId = 1;
        ContractorAccount oldContractorAccount = ContractorAccount.builder().id(contractorId).build();
        ContractorAccount duplicateContractor = ContractorAccount.builder()
                .primaryContact(User.builder().build())
                .id(contractorId)
                .build();
        requestNewContractorAccount.setContractor(oldContractorAccount);
        requestNewContractorAccount.setDuplicateContractor(duplicateContractor);

        assertEquals(PicsActionSupport.INPUT, requestNewContractorAccount.resolveDuplicate());
        assertEquals(RequestNewContractorAccount.SAME_DUPLICATED_CONTRACTOR_ID_ERROR_MESSAGE,
                requestNewContractorAccount.getFieldErrors().get(RequestNewContractorAccount.DUPLICATE_CONTRACTOR_FIELD_NAME).get(0));
    }

    @Test
    public void testSaveRequestComponentsAndEmailIfNew_VerifyProxiesContractorPrimaryContactAndRelationshipSaves() throws Exception {
        setupSaveRequestComponentsAndEmailIfNew();

        Whitebox.invokeMethod(requestNewContractorAccount, "saveRequestComponentsAndEmailIfNew", true);

//        verify(requestNewContractorService).saveRequestingContractor(contractor, operator);
//        verify(requestNewContractorService).savePrimaryContact(contractor, user);
//        verify(requestNewContractorService).saveRelationship(contractor, relationship);
    }

    @Test
    public void testSaveRequestComponentsAndEmailIfNew_IfContactTypeDeclinedSetsDeclinedStatusAndReason() throws Exception {
        setupSaveRequestComponentsAndEmailIfNew();
        Whitebox.setInternalState(requestNewContractorAccount, "contactType", RequestContactType.DECLINED);
        Whitebox.setInternalState(requestNewContractorAccount, "contactNote", "Test Note");

        Whitebox.invokeMethod(requestNewContractorAccount, "saveRequestComponentsAndEmailIfNew", true);

        verify(requestNewContractorService).populateRequestedContractor(any(ContractorAccount.class), any(OperatorAccount.class), any(RequestContactType.class), any(String.class));
    }

    @Test
    public void testSaveRequestComponentsAndEmailIfNew_IfNotNewRequestNoEmailIsSent() throws Exception {
        setupSaveRequestComponentsAndEmailIfNew();

        Whitebox.invokeMethod(requestNewContractorAccount, "saveRequestComponentsAndEmailIfNew", false);

        verify(emailHelper, never()).sendInitialEmail(any(ContractorAccount.class), any(User.class), any(ContractorOperator.class), anyString());
    }

    @Test
    public void testSaveRequestComponentsAndEmailIfNew_IfNewRequestEmailIsSent() throws Exception {
        setupSaveRequestComponentsAndEmailIfNew();

        Whitebox.invokeMethod(requestNewContractorAccount, "saveRequestComponentsAndEmailIfNew", true);

        verify(contractorOperatorService).publishEvent(any(ContractorOperator.class), any(ContractorOperatorEventType.class), any(Integer.class));
    }

    @Test
    public void testSaveRequestComponentsAndEmailIfNew_IfNewRequestContactPropertiesAreSetOnContractor() throws Exception {
        setupSaveRequestComponentsAndEmailIfNew();
        requestNewContractorAccount.setContactType(RequestContactType.EMAIL);

        Whitebox.invokeMethod(requestNewContractorAccount, "saveRequestComponentsAndEmailIfNew", true);

        verify(contractor).setLastContactedByInsideSalesDate(any(Date.class));
    }

    private void setupSaveRequestComponentsAndEmailIfNew() {
        Whitebox.setInternalState(requestNewContractorAccount, "requestRelationship", relationship);
        Whitebox.setInternalState(requestNewContractorAccount, "contractor", contractor);
        Whitebox.setInternalState(requestNewContractorAccount, "primaryContact", user);
        Whitebox.setInternalState(requestNewContractorAccount, "noteDao", noteDao);
        when(relationship.getOperatorAccount()).thenReturn(operator);
        when(contractorAccountDAO.save(contractor)).thenReturn(contractor);
    }

    private void setPermissionsAsOperator() {
		when(entityManager.find(eq(OperatorAccount.class), anyInt())).thenReturn(operator);
		when(operator.getId()).thenReturn(1);
		when(operator.getStatus()).thenReturn(AccountStatus.Active);
		when(permissions.getAccountId()).thenReturn(1);
		when(permissions.isOperator()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);
	}

	private void testContactType(RequestContactType type) {
		requestNewContractorAccount.setContactType(type);
		assertTrue(requestNewContractorAccount.isContactNoteMissing());

		requestNewContractorAccount.setContactNote("Note");
		assertFalse(requestNewContractorAccount.isContactNoteMissing());

		requestNewContractorAccount.setContactNote("");
		assertTrue(requestNewContractorAccount.isContactNoteMissing());
		// reset
		requestNewContractorAccount.setContactType(null);
		requestNewContractorAccount.setContactNote(null);
	}
}