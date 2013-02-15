package com.picsauditing.actions.operators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.validator.ValidationException;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.models.operators.FacilitiesEditModel;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;
import com.picsauditing.validator.FacilitiesEditValidator;

public class FacilitiesEditTest extends PicsTest {
	private int NON_ZERO_OPERATOR_ID = 123;

	private FacilitiesEdit facilitiesEdit;
	private User user;
	private OperatorAccount operator;

	@Mock
	private CountrySubdivisionDAO countrySubdivisionDAO;
	@Mock
	private FacilitiesEditModel facilitiesEditModel;

	@Mock
	private Permissions permissions;
	@Mock
	private UserDAO userDAO;
	@Mock
	private OperatorAccountDAO operatorDAO;
	@Mock
	private FeatureToggle featureToggle;
	@Mock
	private FacilitiesEditValidator facilitiesEditValidator;
	@Mock
	private AccountStatusChanges accountStatusChanges;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		facilitiesEdit = new FacilitiesEdit();
		autowireEMInjectedDAOs(facilitiesEdit);

		user = EntityFactory.makeUser();
		// the copy of user.id to permisions.userId happens only on
		// loadPermissions which
		// happens in login, which we are not doing here. stub it
		when(permissions.getUserId()).thenReturn(user.getId());

		Whitebox.setInternalState(facilitiesEdit, "facilitiesEditModel", facilitiesEditModel);
		Whitebox.setInternalState(facilitiesEdit, "permissions", permissions);
		Whitebox.setInternalState(facilitiesEdit, "countrySubdivisionDAO", countrySubdivisionDAO);
		Whitebox.setInternalState(facilitiesEdit, "facilitiesEditValidator", facilitiesEditValidator);
		Whitebox.setInternalState(facilitiesEditModel, "operatorDAO", operatorDAO);
		Whitebox.setInternalState(facilitiesEdit, "accountStatusChanges", accountStatusChanges);

		// specific call the real method in the FacilitiesEditMode when adding
		// roles.
		Whitebox.setInternalState(facilitiesEditModel, "featureToggle", featureToggle);
		when(featureToggle.isFeatureEnabled(any(String.class))).thenReturn(true);

		Whitebox.setInternalState(facilitiesEditModel, "operatorDAO", operatorDAO);
		doCallRealMethod().when(facilitiesEditModel).addRole(any(Permissions.class), any(OperatorAccount.class),
				any(AccountUser.class));

		operator = new OperatorAccount();
		operator.setId(NON_ZERO_OPERATOR_ID);
		facilitiesEdit.setOperator(operator);
	}

	@Test
	public void testAddRoleAccountRep() throws Exception {
		AccountUser rep = accountRep();
		facilitiesEdit.setAccountRep(rep);

		doCallRealMethod().when(facilitiesEditModel).addRole(any(Permissions.class), any(OperatorAccount.class),
				any(AccountUser.class));

		ArgumentCaptor<OperatorAccount> argument = ArgumentCaptor.forClass(OperatorAccount.class);
		String strutsReturn = facilitiesEdit.addRole();
		verify(operatorDAO).save(argument.capture());
		rep = getAccountUserFromOperator(argument);

		accountUserRolesCommonAssertions(strutsReturn, rep);
		datesConformToBusinessRulesAccountRep(rep);
	}

	@Test
	public void testSaveRole() throws Exception {
		when(facilitiesEditValidator.validateOwnershipPercentage(anyListOf(AccountUser.class))).thenReturn(
				Strings.EMPTY_STRING);

		String strutsReturnValue = facilitiesEdit.saveRole();

		saveRoleVerifications(strutsReturnValue, times(1));
	}

	@Test
	public void testSaveRole_ValidationError() throws Exception {
		when(facilitiesEditValidator.validateOwnershipPercentage(anyListOf(AccountUser.class))).thenReturn(
				"Validation Error");

		String strutsReturnValue = facilitiesEdit.saveRole();

		assertEquals("Validation Error", facilitiesEdit.getActionMessages().iterator().next());
		saveRoleVerifications(strutsReturnValue, never());
	}

	private void saveRoleVerifications(String strutsReturnValue, VerificationMode mergeVerificationMode) {
		assertEquals(PicsActionSupport.REDIRECT, strutsReturnValue);
		verify(em, mergeVerificationMode).merge(operator);
		verify(em, never()).persist(operator); // not a new operator, so merged
												// not persisted
	}

	@Test
	public void testAddRoleSalesRep() throws Exception {
		AccountUser rep = salesRep();
		facilitiesEdit.setSalesRep(rep);
		AccountUser accountRep = new AccountUser();
		User user = new User();
		user.setId(0);
		accountRep.setUser(user);
		facilitiesEdit.setAccountRep(accountRep);

		ArgumentCaptor<OperatorAccount> argument = ArgumentCaptor.forClass(OperatorAccount.class);
		String strutsReturn = facilitiesEdit.addRole();
		verify(operatorDAO).save(argument.capture());
		rep = getAccountUserFromOperator(argument);

		accountUserRolesCommonAssertions(strutsReturn, rep);
		datesConformToBusinessRulesSalesRep(rep);
	}

	private AccountUser getAccountUserFromOperator(ArgumentCaptor<OperatorAccount> argument) {
		List<AccountUser> accountUsers = argument.getValue().getAccountUsers();
		return accountUsers.get(0);
	}

	private void accountUserRolesCommonAssertions(String strutsReturn, AccountUser rep) {
		Date now = new Date();

		assertEquals(PicsActionSupport.REDIRECT, strutsReturn);
		assertEquals("didn't get the expected operator back from the account rep", operator, rep.getAccount());
		assertNotNull("start date should not be null", rep.getStartDate());
		assertNotNull("end date should not be null", rep.getEndDate());

		auditColumnsAreSet(rep, now);

		// not a new operator, so merged not persisted
		verify(operatorDAO, times(1)).save(operator);
	}

	@Test
	public void testAddOneAccountRepThenOwnerPercentConformsToBusinessRule() {
		// success: account users with same role owner percentage = 100 do not
		// trigger action message

		FacilitiesEdit facilitiesEditSpy = spy(facilitiesEdit);

		AccountUser accountRep = accountUser();
		accountRep.setRole(UserAccountRole.PICSAccountRep);
		facilitiesEditSpy.setAccountRep(accountRep);

		// TODO: figure out why I have to reset this from the mock for the spy
		// to work
		OperatorAccount operator = EntityFactory.makeOperator();
		facilitiesEditSpy.setOperator(operator);

		facilitiesEditSpy.addRole();

		verify(facilitiesEditSpy, never()).addActionMessage(anyString());
	}

	@Test
	public void testAddRole_ViolatesBusinessRule() throws ValidationException {
		FacilitiesEdit facilitiesEditSpy = additionSetupForAddRoleTest();
		when(facilitiesEditValidator.validateOwnershipPercentage(anyListOf(AccountUser.class), any(AccountUser.class)))
				.thenReturn("Validation Error");

		String strutsReturnValue = facilitiesEditSpy.addRole();

		assertEquals("Validation Error", facilitiesEditSpy.getActionMessages().iterator().next());
		assertEquals(PicsActionSupport.REDIRECT, strutsReturnValue);
		verify(facilitiesEditSpy, never()).addActionMessage(
				UserAccountRole.PICSAccountRep.getDescription() + " is not 100 percent");
	}

	private FacilitiesEdit additionSetupForAddRoleTest() {
		FacilitiesEdit facilitiesEditSpy = spy(facilitiesEdit);

		AccountUser accountRep = accountUser();
		accountRep.setRole(UserAccountRole.PICSAccountRep);
		facilitiesEditSpy.setAccountRep(accountRep);

		AccountUser accountRep2 = accountUser();
		accountRep2.setRole(UserAccountRole.PICSAccountRep);

		List<AccountUser> accountUsers = new ArrayList<AccountUser>();
		accountUsers.add(accountRep2);

		operator.setAccountUsers(accountUsers);
		facilitiesEditSpy.setOperator(operator);

		return facilitiesEditSpy;
	}

	@Test
	public void testGetPrimaryOperatorContactUsers() throws Exception {
		Set<User> primaryContactSet = new HashSet<User>();
		List<User> findUser = new ArrayList<User>();
		List<User> findGroup = new ArrayList<User>();
		when(userDAO.findByAccountID(operator.getId(), "Yes", "No")).thenReturn(findUser);
		when(userDAO.findByAccountID(operator.getId(), "Yes", "Yes")).thenReturn(findGroup);
		List<User> userList = new ArrayList<User>();
		Whitebox.invokeMethod(facilitiesEdit, "addParentPrimaryOperatorContactUsers", primaryContactSet);
		assertEquals(userList.size(), primaryContactSet.size());
	}

	@Test
	public void testSave_PreventSettingSelfAsParent() {
		Country country = mock(Country.class);
		CountrySubdivision countrySubdivision = mock(CountrySubdivision.class);

		when(countrySubdivision.getCountry()).thenReturn(country);
		when(em.merge(operator)).thenReturn(operator);

		operator.setCountry(country);
		operator.setCountrySubdivision(countrySubdivision);
		operator.setDiscountPercent(BigDecimal.ZERO);
		operator.setParent(operator);
		operator.setName("Operator");

		List<Integer> facilities = new ArrayList<Integer>();
		facilities.add(NON_ZERO_OPERATOR_ID);

		facilitiesEdit.setOperator(operator);
		facilitiesEdit.setFacilities(facilities);

		assertEquals(PicsActionSupport.REDIRECT, facilitiesEdit.save());
		assertFalse(facilitiesEdit.hasActionMessages());
		assertTrue(facilitiesEdit.hasActionErrors());

		verify(em, never()).merge(any(OperatorAccount.class));
	}

	private AccountUser accountRep() {
		AccountUser accountRep = accountUser();
		accountRep.setRole(UserAccountRole.PICSAccountRep);
		return accountRep;
	}

	private AccountUser salesRep() {
		AccountUser salesRep = accountUser();
		salesRep.setRole(UserAccountRole.PICSSalesRep);
		return salesRep;
	}

	private AccountUser accountUser() {
		AccountUser accountUser = new AccountUser();
		accountUser.setUser(user);
		return accountUser;
	}

	private void datesConformToBusinessRulesAccountRep(AccountUser accountRep) {
		startDateConformsToBusinessRuleAllRep(accountRep);
		endDateConformsToBusinessRuleAccountRep(accountRep);
	}

	private void datesConformToBusinessRulesSalesRep(AccountUser salesRep) {
		startDateConformsToBusinessRuleAllRep(salesRep);
		endDateConformsToBusinessRuleSalesRep(salesRep);
	}

	private void startDateConformsToBusinessRuleAllRep(AccountUser accountRep) {
		// success: start date is first of this month
		Calendar testStartDate = Calendar.getInstance();
		testStartDate.set(Calendar.DATE, 1);
		checkDate(accountRep.getStartDate(), testStartDate);
	}

	private void endDateConformsToBusinessRuleAccountRep(AccountUser accountRep) {
		// success: end date is first of month 20 years from now
		Calendar testEndDate = Calendar.getInstance();
		testEndDate.set(Calendar.DATE, 1);
		testEndDate.add(Calendar.YEAR, 20);
		checkDate(accountRep.getEndDate(), testEndDate);
	}

	private void endDateConformsToBusinessRuleSalesRep(AccountUser salesRep) {
		Calendar testEndDate = Calendar.getInstance();
		testEndDate.set(Calendar.DATE, 1);
		testEndDate.add(Calendar.YEAR, 1);
		testEndDate.add(Calendar.DATE, -1);
		checkDate(salesRep.getEndDate(), testEndDate);
	}

	private void checkDate(Date actual, Calendar expected) {
		Calendar actualDate = Calendar.getInstance();
		actualDate.setTime(actual);
		assertTrue("end date does not conform to business rule",
				expected.get(Calendar.YEAR) == actualDate.get(Calendar.YEAR)
						&& expected.get(Calendar.DAY_OF_YEAR) == actualDate.get(Calendar.DAY_OF_YEAR));
	}

	private void auditColumnsAreSet(AccountUser accountRep, Date now) {
		assertEquals("audit column has the wrong (or no) createdBy user", user.getId(), accountRep.getCreatedBy()
				.getId());
		assertEquals("audit column has the wrong (or no) updatedBy user", user.getId(), accountRep.getUpdatedBy()
				.getId());
		Date creationDate = accountRep.getCreationDate();
		assertTrue("the creation date is too far in the past", (now.getTime() - creationDate.getTime()) < 1000);
		Date updateDate = accountRep.getUpdateDate();
		assertTrue("the update date is too far in the past", (now.getTime() - updateDate.getTime()) < 1000);
	}

	// get account users by role instead of the comp loop
	// Q: method assumes permissions have been loaded. is this a valid
	// assumption?
	// A: yes, the security interceptor will force this load. is this enough to
	// keep this order dependency?

	/**
	 * Overrides the operator set in the FacilitiesEdit action class in the Setup method so
	 * we use a Mock instead of a Fake.
	 *
	 * Also overrides the operatorDAO used by setup() because the test is trying to set the same
	 * mock in the FacilitiesEdit and FacilitiesEditModel class.
	 */
	private void setupForSaveClientSiteTests(AccountStatus clientSiteStatus) {
		operatorDAO = Mockito.mock(OperatorAccountDAO.class);
		Whitebox.setInternalState(facilitiesEdit, "operatorDao", operatorDAO);

		operator = Mockito.mock(OperatorAccount.class);
		when(operator.getStatus()).thenReturn(clientSiteStatus);
		facilitiesEdit.setOperator(operator);
	}

	@Test
	public void testSaveClientSite_DeactivatedAccount() throws Exception {
		setupForSaveClientSiteTests(AccountStatus.Deactivated);

		Whitebox.invokeMethod(facilitiesEdit, "saveClientSite");

		verify(accountStatusChanges, times(1)).deactivateClientSite(operator, permissions, permissions.getName() + " has deactivated this account.");
		verify(operator, times(1)).setNeedsIndexing(true);
		verify(operatorDAO, times(1)).save(operator);
	}

	@Test
	public void testSaveClientSite_ActiveAccount() throws Exception {
		setupForSaveClientSiteTests(AccountStatus.Active);

		Whitebox.invokeMethod(facilitiesEdit, "saveClientSite");

		verify(accountStatusChanges, never()).deactivateClientSite(any(OperatorAccount.class), any(Permissions.class), anyString());
		verify(operator, times(1)).setNeedsIndexing(true);
		verify(operatorDAO, times(1)).save(operator);
	}

}
