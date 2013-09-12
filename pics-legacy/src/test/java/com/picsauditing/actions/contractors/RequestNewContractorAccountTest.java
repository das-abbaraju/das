package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		requestNewContractorAccount = new RequestNewContractorAccount();
		picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(requestNewContractorAccount, entityManager);

		when(contractor.getId()).thenReturn(1);
		when(contractor.getStatus()).thenReturn(AccountStatus.Requested);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(Collections.emptyList());
		when(relationship.getOperatorAccount()).thenReturn(operator);
		when(urlUtil.getActionUrl(anyString(), any(HashMap.class))).thenReturn("URL");

		Whitebox.setInternalState(requestNewContractorAccount, "emailHelper", emailHelper);
		Whitebox.setInternalState(requestNewContractorAccount, "featureToggle", featureToggle);
		Whitebox.setInternalState(requestNewContractorAccount, "permissions", permissions);
		Whitebox.setInternalState(requestNewContractorAccount, "urlUtil", urlUtil);
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

		verify(contractor).generateRegistrationHash();
		verify(contractor).setLastContactedByAutomatedEmailDate(any(Date.class));
		verify(contractor).setLastContactedByInsideSales(anyInt());
		verify(contractor).setLastContactedByInsideSalesDate(any(Date.class));
		verify(emailHelper).sendInitialEmail(eq(contractor), eq(user), eq(relationship), anyString());
		verify(entityManager, times(5)).persist(any(BaseTable.class));
		verify(entityManager, never()).merge(any(BaseTable.class));
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
		when(entityManager.merge(any(BaseTable.class))).thenReturn(contractor, user, contractor);
		when(permissions.getUserId()).thenReturn(1);
		when(query.getResultList()).thenReturn(requests);
		when(request.getRequestedBy()).thenReturn(otherOperator);
		when(user.getId()).thenReturn(1);

		requestNewContractorAccount.setPrimaryContact(user);
		requestNewContractorAccount.setContractor(contractor);
		requestNewContractorAccount.setRequestRelationship(relationship);

		assertEquals(PicsActionSupport.REDIRECT, requestNewContractorAccount.save());

		verify(emailHelper).sendInitialEmail(eq(contractor), eq(user), eq(relationship), anyString());
		// Contractor, user and request already exist
		verify(entityManager, times(3)).merge(any(BaseTable.class));
		// New contractorOperator, note and email
		verify(entityManager, times(2)).persist(any(BaseTable.class));
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
	public void testSaveNoteIfContacted_ContactTypes() throws Exception {
		when(entityManager.find(eq(User.class), anyInt())).thenReturn(user);
		when(permissions.getUserId()).thenReturn(1);
		when(relationship.getOperatorAccount()).thenReturn(operator);

		requestNewContractorAccount.setContactType(RequestContactType.EMAIL);
		requestNewContractorAccount.setRequestRelationship(relationship);

		Whitebox.invokeMethod(requestNewContractorAccount, "saveNoteIfContacted");

		assertEquals(1, requestNewContractorAccount.getContractor().getTotalContactCount());

		requestNewContractorAccount.setContactType(RequestContactType.PHONE);

		Whitebox.invokeMethod(requestNewContractorAccount, "saveNoteIfContacted");

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