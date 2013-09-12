package com.picsauditing.actions.operators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.model.operators.FacilitiesEditStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.validator.ValidationException;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.UserAccountRole;
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
import com.picsauditing.model.operators.FacilitiesEditModel;
import com.picsauditing.toggle.FeatureToggle;

public class FacilitiesEditTest extends PicsActionTest {
	private int NON_ZERO_OPERATOR_ID = 123;

	private FacilitiesEdit facilitiesEdit;
	private User user;
	private OperatorAccount operator;
    private FacilitiesEditStatus status;

	@Mock
	private CountrySubdivisionDAO countrySubdivisionDAO;
	@Mock
	private FacilitiesEditModel facilitiesEditModel;
	@Mock
	private UserDAO userDAO;
	@Mock
	private OperatorAccountDAO operatorDAO;
	@Mock
	private FeatureToggle featureToggle;
	@Mock
	private AccountStatusChanges accountStatusChanges;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

        facilitiesEdit = new FacilitiesEdit();
        super.setUp(facilitiesEdit);

        PicsTestUtil.autowireDAOsFromDeclaredMocks(facilitiesEdit, this);

		user = EntityFactory.makeUser();
        status = new FacilitiesEditStatus();

		when(permissions.getUserId()).thenReturn(user.getId());
        when(facilitiesEditModel.manageSingleCurrentAccountUser(any(Permissions.class), any(OperatorAccount.class), any(AccountUser.class))).thenReturn(status);
        when(facilitiesEditModel.addOneToManyAccountUser(any(Permissions.class), any(OperatorAccount.class), any(AccountUser.class))).thenReturn(status);
        when(featureToggle.isFeatureEnabled(any(String.class))).thenReturn(true);

        Whitebox.setInternalState(facilitiesEdit, "facilitiesEditModel", facilitiesEditModel);
        Whitebox.setInternalState(facilitiesEdit, "accountStatusChanges", accountStatusChanges);
        Whitebox.setInternalState(facilitiesEditModel, "featureToggle", featureToggle);

		operator = new OperatorAccount();
		operator.setId(NON_ZERO_OPERATOR_ID);
		facilitiesEdit.setOperator(operator);
	}

	@Test
	public void testAddSalesRepresentative_NoErrorAddsActionMessage() throws Exception {
        status.isOkMessage = "OK MESSAGE";
        AccountUser rep = salesRep();
		facilitiesEdit.setSalesRep(rep);
        when(facilitiesEditModel.addOneToManyAccountUser(permissions, operator, rep)).thenReturn(status);

        facilitiesEdit.addSalesRepresentative();

        assertTrue(facilitiesEdit.getActionMessages().contains(status.isOkMessage));
	}

    @Test
    public void testAddSalesRepresentative_ErrorAddsActionError() throws Exception {
        status.isOk = false;
        status.notOkErrorMessage = "NOT OK MESSAGE";
        AccountUser rep = salesRep();
        facilitiesEdit.setSalesRep(rep);
        when(facilitiesEditModel.addOneToManyAccountUser(permissions, operator, rep)).thenReturn(status);

        facilitiesEdit.addSalesRepresentative();

        assertTrue(facilitiesEdit.getActionErrors().contains(status.notOkErrorMessage));
    }

    @Test
    public void testManageAccountRepresentative_NoErrorAddsActionMessage() throws Exception {
        status.isOkMessage = "OK MESSAGE";
        AccountUser rep = accountRep();
        facilitiesEdit.setAccountRep(rep);
        when(facilitiesEditModel.manageSingleCurrentAccountUser(permissions, operator, rep)).thenReturn(status);

        facilitiesEdit.manageAccountRepresentative();

        assertTrue(facilitiesEdit.getActionMessages().contains(status.isOkMessage));
    }

    @Test
    public void testManageAccountRepresentative_ErrorAddsActionError() throws Exception {
        status.isOk = false;
        status.notOkErrorMessage = "NOT OK MESSAGE";
        AccountUser rep = accountRep();
        facilitiesEdit.setAccountRep(rep);
        when(facilitiesEditModel.manageSingleCurrentAccountUser(permissions, operator, rep)).thenReturn(status);

        facilitiesEdit.manageAccountRepresentative();

        assertTrue(facilitiesEdit.getActionErrors().contains(status.notOkErrorMessage));
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

		verify(operatorDAO, never()).save(any(OperatorAccount.class));
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

		verify(accountStatusChanges, times(1)).deactivateClientSite(operator, permissions,
                AccountStatusChanges.OPERATOR_MANUALLY_DEACTIVATED_REASON,
                permissions.getName() + " has deactivated this account.");
		verify(operator, times(1)).setNeedsIndexing(true);
		verify(operatorDAO, times(1)).save(operator);
	}

	@Test
	public void testSaveClientSite_ActiveAccount() throws Exception {
		setupForSaveClientSiteTests(AccountStatus.Active);

		Whitebox.invokeMethod(facilitiesEdit, "saveClientSite");

		verify(accountStatusChanges, never()).deactivateClientSite(any(OperatorAccount.class),
                any(Permissions.class), anyString(), anyString());
		verify(operator, times(1)).setNeedsIndexing(true);
		verify(operatorDAO, times(1)).save(operator);
	}

}
