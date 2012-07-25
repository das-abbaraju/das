package com.picsauditing.actions.operators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;

public class FacilitiesEditTest extends PicsTest {
	private int NON_ZERO_OPERATOR_ID = 123;

	FacilitiesEdit facilitiesEdit;
	User user;

	@Mock
	OperatorAccount operator;
	@Mock
	private Permissions permissions;
	@Mock
	private CountrySubdivisionDAO countrySubdivisionDAO;
	@Mock
	private CountrySubdivision countrySubdivision;
	@Mock
	private State state;

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
		PicsTestUtil.forceSetPrivateField(facilitiesEdit, "permissions", permissions);
		PicsTestUtil.forceSetPrivateField(facilitiesEdit, "countrySubdivisionDAO", countrySubdivisionDAO);

		when(operator.getId()).thenReturn(NON_ZERO_OPERATOR_ID);
		facilitiesEdit.setOperator(operator);
	}

	@Test
	public void testAddRoleAccountRep() throws Exception {
		AccountUser rep = accountRep();
		facilitiesEdit.setAccountRep(rep);
		accountUserRolesCommonAssertions(rep);
		datesConformToBusinessRulesAccountRep(rep);
	}

	@Test
	public void testSaveRole() throws Exception {
		String strutsReturn = facilitiesEdit.saveRole();
		assertEquals(PicsActionSupport.REDIRECT, strutsReturn);
		// not a new operator, so merged not persisted
		verify(em, times(1)).merge(operator);
		verify(em, never()).persist(operator);
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

		accountUserRolesCommonAssertions(rep);

		datesConformToBusinessRulesSalesRep(rep);
	}

	private void accountUserRolesCommonAssertions(AccountUser rep) {
		Date now = new Date();
		String strutsReturn = facilitiesEdit.addRole();
		assertEquals(PicsActionSupport.REDIRECT, strutsReturn);

		assertEquals("didn't get the expected operator back from the account rep", operator, rep.getAccount());
		assertNotNull("start date should not be null", rep.getStartDate());
		assertNotNull("end date should not be null", rep.getEndDate());

		auditColumnsAreSet(rep, now);

		// not a new operator, so merged not persisted
		verify(em, atLeast(1)).merge(operator);
		assertNull("account rep should have been nulled at end of use case", facilitiesEdit.getAccountRep());
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
	public void testAddRoleOwnerPercentViolatesBusinessRule() {
		// error: account users with same role owner percentage < 100 trigger
		// action message
		// error: account users with same role owner percentage > 100 trigger
		// action message

		FacilitiesEdit facilitiesEditSpy = spy(facilitiesEdit);

		AccountUser accountRep = accountUser();
		accountRep.setRole(UserAccountRole.PICSAccountRep);
		facilitiesEditSpy.setAccountRep(accountRep);

		AccountUser accountRep2 = accountUser();
		accountRep2.setRole(UserAccountRole.PICSAccountRep);

		List<AccountUser> accountUsers = new ArrayList<AccountUser>();
		accountUsers.add(accountRep2);

		when(operator.getAccountUsers()).thenReturn(accountUsers);

		facilitiesEditSpy.setOperator(operator);

		facilitiesEditSpy.addRole();

		verify(facilitiesEditSpy, times(1)).addActionMessage(
				UserAccountRole.PICSAccountRep.getDescription() + " is not 100 percent");
	}

	@Test
	public void testAddRoleOwnerPercentViolatesBusinessRuleRightWay() {
		// error: account users with same role owner percentage < 100 trigger
		// action message
		// error: account users with same role owner percentage > 100 trigger
		// action message

		AccountUser accountRep = accountUser();
		accountRep.setRole(UserAccountRole.PICSAccountRep);
		facilitiesEdit.setAccountRep(accountRep);

		AccountUser accountRep2 = accountUser();
		accountRep2.setRole(UserAccountRole.PICSAccountRep);

		List<AccountUser> accountUsers = new ArrayList<AccountUser>();
		accountUsers.add(accountRep2);

		when(operator.getAccountUsers()).thenReturn(accountUsers);

		facilitiesEdit.addRole();

		assertTrue(
				"Business rule violation was not noticed",
				facilitiesEdit.getActionMessages().contains(
						UserAccountRole.PICSAccountRep.getDescription() + " is not 100 percent"));
	}

	@Test
	public void testUpdateStateAndCountrySubdivision_countryHasState() throws Exception{
		state = new State("CA");
		state.setCountry(new Country("US"));
		countrySubdivision = new CountrySubdivision("US-CA");
		Whitebox.setInternalState(facilitiesEdit, "countrySubdivision", countrySubdivision);
		when(operator.getState()).thenReturn(state);
		when(operator.getCountry()).thenReturn(new Country("US"));
		when(countrySubdivisionDAO.find(anyString())).thenReturn(countrySubdivision);

		Whitebox.invokeMethod(facilitiesEdit, "updateStateAndCountrySubdivision");
		verify(operator).setCountrySubdivision(countrySubdivision);
	}

	@Test
	public void testUpdateStateAndCountrySubdivision_countryHasWrongState() throws Exception{
		state = new State("CA");
		state.setCountry(new Country("CA"));
		countrySubdivision = new CountrySubdivision("CA-CA");
		when(operator.getState()).thenReturn(state);
		when(operator.getCountry()).thenReturn(new Country("US"));
		when(countrySubdivisionDAO.find(anyString())).thenReturn(countrySubdivision);
		Whitebox.invokeMethod(facilitiesEdit, "updateStateAndCountrySubdivision");

		verify(operator).setState(null);
		verify(operator).setCountrySubdivision(null);
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

}
