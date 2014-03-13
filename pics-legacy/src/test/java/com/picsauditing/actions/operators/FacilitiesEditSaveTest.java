package com.picsauditing.actions.operators;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.model.operators.FacilitiesEditModel;
import com.picsauditing.service.contractor.ContractorOperatorService;
import com.picsauditing.toggle.FeatureToggle;
import junit.framework.AssertionFailedError;
import org.junit.*;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FacilitiesEditSaveTest extends PicsActionTest {
    private static final int NON_ZERO_OPERATOR_ID = 123;
    private static final int NON_ZERO_USER_ID = 321;
    private static final int ZERO_OPERATOR_ID = 0;
    private static final String REMEMBER_ME_TIME_IN_DAYS = "7";
    private static final String REMEMBER_ME_TIME_IN_DAYS_BAD_VALUE = "7.5";
    private static final String SESSION_TIMEOUT_IN_MINUTES = "60";
    private static final String SESSION_TIMEOUT_IN_MINUTES_BAD_VALUE = "60.7";
    private static final String OPERATOR_NAME = "Test Operator";
    private static final String OPERATOR_TYPE = "Operator";
    private static final String REMEMBER_ME_INTEGER_ERROR_KEY = "FacilitiesEdit.RememberMeInteger";
    private static final String SESSION_TIMEOUT_INTEGER_ERROR_KEY = "FacilitiesEdit.SessionTimeoutInteger";
    private static final String CYCLICAL_RELATIONSHIP_ERROR_KEY = "FacilitiesEdit.CyclicalRelationship";
    private static final String EMPTY_COMPANY_NAME_ERROR_KEY = "FacilitiesEdit.PleaseFillInCompanyName";
    private static final String SUCCESS_KEY = "FacilitiesEdit.SuccessfullySaved";
    private static final String NEW_COUNTRY_SUBDIVISION_ISO_CODE = "ThisCountryRocks";

    private FacilitiesEdit facilitiesEdit;
    // these are the facilities added or removed (e.g. via the UI)
    private List<Integer> facilities;
    // these are the facilities that an existing operator already has
    private List<Facility> operatorFacilities;
    private List<ContractorOperator> contractorOperators;

    @Mock
    private CountrySubdivisionDAO countrySubdivisionDAO;
    @Mock
    private FacilitiesDAO facilitiesDAO;
    @Mock
    private FacilitiesEditModel facilitiesEditModel;
    @Mock
    private UserDAO userDAO;
    @Mock
    private User user;
    @Mock
    private OperatorAccountDAO operatorDAO;
    @Mock
    private FeatureToggle featureToggle;
    @Mock
    private AccountStatusChanges accountStatusChanges;
    @Mock
    private OperatorAccount operator;
    @Mock
    private OperatorAccount operator2;
    @Mock
    private Country country;
    @Mock
    private Country country2;
    @Mock
    private CountrySubdivision countrySubdivision;
    @Mock
    private CountrySubdivision newCountrySubdivision;
    @Mock
    private ContractorOperatorService contractorOperatorService;
    @Mock
    private ContractorOperator contractorOperator1;
    @Mock
    private ContractorOperator contractorOperator2;
    @Mock
    private Naics naics;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        facilitiesEdit = new FacilitiesEdit();
        super.setUp(facilitiesEdit);

        PicsTestUtil.autowireDAOsFromDeclaredMocks(facilitiesEdit, this);

        facilities = new ArrayList<>();
        operatorFacilities = new ArrayList<>();
        contractorOperators = new ArrayList<>();
        contractorOperators.add(contractorOperator1);
        contractorOperators.add(contractorOperator2);

        when(operatorDAO.save(operator)).thenReturn(operator);
        when(operatorDAO.find(operator.getId())).thenReturn(operator);
        when(operator.getId()).thenReturn(NON_ZERO_OPERATOR_ID);
        when(operator.getCountry()).thenReturn(country);
        when(operator.getCountrySubdivision()).thenReturn(countrySubdivision);
        when(operator.getParent()).thenReturn(operator);
        when(operator.getDiscountPercent()).thenReturn(BigDecimal.ZERO);
        when(operator.getName()).thenReturn(OPERATOR_NAME);
        when(operator.getStatus()).thenReturn(AccountStatus.Active);
        when(operator.isRememberMeTimeEnabled()).thenReturn(true);
        when(operator.isCorporate()).thenReturn(false);
        when(operator.getRememberMeTimeInDays()).thenReturn(Integer.parseInt(REMEMBER_ME_TIME_IN_DAYS));
        when(operator.getDiscountPercent()).thenReturn(BigDecimal.ZERO);
        when(user.getId()).thenReturn(NON_ZERO_USER_ID);
        when(permissions.getUserId()).thenReturn(NON_ZERO_USER_ID);
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(false);
        when(countrySubdivision.getCountry()).thenReturn(country);
        when(newCountrySubdivision.toString()).thenReturn(NEW_COUNTRY_SUBDIVISION_ISO_CODE);

        Whitebox.setInternalState(facilitiesEdit, "facilitiesEditModel", facilitiesEditModel);
        Whitebox.setInternalState(facilitiesEdit, "contractorOperatorService", contractorOperatorService);
        Whitebox.setInternalState(facilitiesEdit, "operatorDao", operatorDAO);
        Whitebox.setInternalState(facilitiesEdit, "accountStatusChanges", accountStatusChanges);

        facilitiesEdit.setOperator(operator);
        facilitiesEdit.setTimeoutDays(REMEMBER_ME_TIME_IN_DAYS);
        facilitiesEdit.setSessionTimeout(SESSION_TIMEOUT_IN_MINUTES);
        facilitiesEdit.setFacilities(facilities);
        facilitiesEdit.setCreateType(OPERATOR_TYPE);
    }

    @Test
    public void testSave_IfOperatorNewCreateTypeSet() {
        when(operator.getId()).thenReturn(ZERO_OPERATOR_ID);

        facilitiesEdit.save();

        verify(operator).setType(OPERATOR_TYPE);
    }

    @Test
    public void testSave_RememberMeNotEnabledDoesNotSetTimeoutInDays() {
        when(operator.isRememberMeTimeEnabled()).thenReturn(false);
        when(operator.getRememberMeTimeInDays()).thenReturn(0);

        facilitiesEdit.save();

        assertEquals(0, operator.getRememberMeTimeInDays());
    }

    @Test
    public void testSave_WhenRememberMeIsNotAnIntegerAnErrorIsRecorded() {
        facilitiesEdit.setTimeoutDays(REMEMBER_ME_TIME_IN_DAYS_BAD_VALUE);

        facilitiesEdit.save();

        assertTrue(facilitiesEdit.hasActionErrors());
        verify(translationService).getText(REMEMBER_ME_INTEGER_ERROR_KEY, Locale.ENGLISH, null);
    }

    @Test
    public void testSave_WhenSessionTimeoutIsNotAnIntegerAnErrorIsRecorded() {
        facilitiesEdit.setSessionTimeout(SESSION_TIMEOUT_IN_MINUTES_BAD_VALUE);

        facilitiesEdit.save();

        assertTrue(facilitiesEdit.hasActionErrors());
        verify(translationService).getText(SESSION_TIMEOUT_INTEGER_ERROR_KEY, Locale.ENGLISH, null);
    }

    @Test
    public void testSave_OperatorFacilitiesAreMovedToFacilitiesForNullFacilities() {
        facilitiesEdit.setFacilities(null);
        Facility fac1 = new Facility();
        OperatorAccount op1 = new OperatorAccount();
        op1.setId(500);
        fac1.setOperator(op1);
        OperatorAccount op2 = new OperatorAccount();
        op2.setId(501);
        Facility fac2 = new Facility();
        fac2.setOperator(op2);
        operatorFacilities.add(fac1);
        operatorFacilities.add(fac2);
        when(operator.getOperatorFacilities()).thenReturn(operatorFacilities);

        facilitiesEdit.save();

        assertThat(facilitiesEdit.getFacilities(), hasItem(500));
        assertThat(facilitiesEdit.getFacilities(), hasItem(501));
    }

    @Test
    public void testSave_PreventSettingSelfAsParent() {
        facilities.add(NON_ZERO_OPERATOR_ID);

        String result = facilitiesEdit.save();

        assertEquals(PicsActionSupport.REDIRECT, result);
        assertFalse(facilitiesEdit.hasActionMessages());
        assertTrue(facilitiesEdit.hasActionErrors());
        verify(operatorDAO, never()).save(any(OperatorAccount.class));
        verify(translationService).getText(eq(CYCLICAL_RELATIONSHIP_ERROR_KEY), eq(Locale.ENGLISH), anyVararg());
    }

    @Test
    public void testSave_CountryChangeIsCopiedToOperator() {
        facilitiesEdit.setCountry(country2);

        facilitiesEdit.save();

        verify(operator).setCountry(country2);
    }

    @Test
    public void testSave_NullCountryIsNotCopiedToOperator() {
        facilitiesEdit.setCountry(null);

        facilitiesEdit.save();

        verify(operator, never()).setCountry(country2);
    }

    @Test
    public void testSave_SameCountryIsNotCopiedToOperator() {
        facilitiesEdit.setCountry(country);

        facilitiesEdit.save();

        verify(operator, never()).setCountry(country2);
    }

    @Test
    public void testSave_NullCountrySubdivisionDoesNotGetSearchedFor() {
        facilitiesEdit.setCountrySubdivision(null);

        facilitiesEdit.save();

        verify(countrySubdivisionDAO, never()).find(anyString());
    }

    @Test
    public void testSave_NullCountrySubdivisionDoesNotSetSetOnOperator() {
        facilitiesEdit.setCountrySubdivision(null);

        facilitiesEdit.save();

        verify(operator, never()).setCountrySubdivision(any(CountrySubdivision.class));
    }

    @Test
    public void testSave_ChangeOfCountrySubdivisionFindsAndSetsResultOfFindOnOperator() {
        facilitiesEdit.setCountrySubdivision(newCountrySubdivision);
        when(countrySubdivisionDAO.find(newCountrySubdivision.toString())).thenReturn(newCountrySubdivision);

        facilitiesEdit.save();

        verify(countrySubdivisionDAO).find(newCountrySubdivision.toString());
        verify(operator).setCountrySubdivision(newCountrySubdivision);
    }

    @Test
    public void testSave_When_NullOperatorCountrySubdivisionAndSetFromUI_Then_FindsAndSetsResultOfFindOnOperator() {
        facilitiesEdit.setCountrySubdivision(newCountrySubdivision);
        when(operator.getCountrySubdivision()).thenReturn(null);
        when(countrySubdivisionDAO.find(newCountrySubdivision.toString())).thenReturn(newCountrySubdivision);

        facilitiesEdit.save();

        verify(countrySubdivisionDAO).find(newCountrySubdivision.toString());
        verify(operator).setCountrySubdivision(newCountrySubdivision);
    }

    @Test
    public void testSave_WithActionErrorsGetSuccessResponseRatherThanRedirect() {
        facilitiesEdit.addActionError("This is a test error");

        String result = facilitiesEdit.save();

        assertEquals(PicsActionSupport.SUCCESS, result);
    }

    @Test
    public void testSave_NoValidationErrorsFromAccountValidationDoesNotClearDao() {
        facilitiesEdit.save();

        verify(operatorDAO, never()).clear();
    }

    @Test
    public void testSave_ValidationErrorFromAccountValidationClearsDaoAndReloadsOperator() {
        // trigger validation error
        when(operator.getName()).thenReturn(null);

        facilitiesEdit.save();

        verify(operatorDAO).clear();
        verify(operatorDAO).find(operator.getId());
    }

    @Test
    public void testSave_ValidationErrorFromAccountValidationNullOperatorOnReloadSkipsFacilityCopy() {
        // trigger validation error
        when(operator.getName()).thenReturn(null);
        when(operatorDAO.find(operator.getId())).thenReturn(null);

        facilitiesEdit.save();

        verify(operator, never()).getOperatorFacilities();
    }

    @Test
    public void testSave_ValidationErrorFromAccountValidationNullOperatorOnReloadStillSetsError() {
        // trigger validation error
        when(operator.getName()).thenReturn(null);
        when(operatorDAO.find(operator.getId())).thenReturn(null);
        when(translationService.getText(EMPTY_COMPANY_NAME_ERROR_KEY, Locale.ENGLISH, null)).thenReturn("CompanyNameMissing");

        facilitiesEdit.save();

        assertThat(facilitiesEdit.getActionErrors(), hasItem("CompanyNameMissing"));
    }

    @Test
    public void testSave_ValidationErrorFromAccountValidationOperatorOnReloadSetsError() {
        // trigger validation error
        when(operator.getName()).thenReturn(null);
        when(translationService.getText(EMPTY_COMPANY_NAME_ERROR_KEY, Locale.ENGLISH, null)).thenReturn("CompanyNameMissing");

        facilitiesEdit.save();

        assertThat(facilitiesEdit.getActionErrors(), hasItem("CompanyNameMissing"));
    }

    @Ignore("PICS-14800: It actually doesn't as I think there's a defect in the code. The relevant block really does nothing")
    @Test
    public void testSave_ValidationErrorFromAccountValidationRecopiesFacilities() {
        // trigger validation error
        when(operator.getName()).thenReturn(null);
        setupOperatorFacilities(500, 501);
        when(operatorDAO.find(operator.getId())).thenReturn(operator);

        facilitiesEdit.save();

        assertThat(facilitiesEdit.getFacilities(), hasItem(500));
        assertThat(facilitiesEdit.getFacilities(), hasItem(501));
    }

    @Test
    public void testSave_HasManageOperatorsEdit_TurningAutoApprovesOffDoesNotAutoApproveAndSetsOperatorProperty() {
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);
        when(contractorOperator1.getWorkStatus()).thenReturn(ApprovalStatus.Pending);
        when(contractorOperator2.getWorkStatus()).thenReturn(ApprovalStatus.Pending);
        when(operator.getContractorOperators()).thenReturn(contractorOperators);
        when(operator.isAutoApproveRelationships()).thenReturn(true);
        facilitiesEdit.setAutoApproveRelationships(false);

        facilitiesEdit.save();

        verify(contractorOperator1, never()).setWorkStatus(ApprovalStatus.Y);
        verify(contractorOperatorService, never()).cascadeWorkStatusToParent(contractorOperator1);
        verify(operator).setAutoApproveRelationships(false);
    }

    @Test
    public void testSave_HasManageOperatorsEdit_TurningAutoApprovesOnAutoApproveAndSetsOperatorProperty() {
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);
        when(contractorOperator1.getWorkStatus()).thenReturn(ApprovalStatus.Pending);
        when(contractorOperator2.getWorkStatus()).thenReturn(ApprovalStatus.Pending);
        when(operator.getContractorOperators()).thenReturn(contractorOperators);
        when(operator.isAutoApproveRelationships()).thenReturn(false);
        facilitiesEdit.setAutoApproveRelationships(true);

        facilitiesEdit.save();

        verify(contractorOperator1).setWorkStatus(ApprovalStatus.Y);
        verify(contractorOperatorService).cascadeWorkStatusToParent(contractorOperator1);
        verify(operator).setAutoApproveRelationships(true);
    }

    @Test
    public void testSave_NoChangeToAutoApprovesDoesNotChangeTheProperty() {
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);
        when(operator.isAutoApproveRelationships()).thenReturn(true);
        facilitiesEdit.setAutoApproveRelationships(true);

        facilitiesEdit.save();

        verify(operator, never()).setAutoApproveRelationships(anyBoolean());
    }

    @Test
    public void testSave_NoNewlyAddedFacilitiesRemovesNothingAndAddsNothing() {
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);
        when(operator.isAutoApproveRelationships()).thenReturn(true);
        facilitiesEdit.setAutoApproveRelationships(true);
        when(operator.isCorporate()).thenReturn(true);
        setupOperatorFacilities(500, 501);
        facilitiesEdit.setFacilities(Arrays.asList(500, 501));

        facilitiesEdit.save();

        verify(facilitiesDAO, never()).remove(any(Facility.class));
        verify(facilitiesDAO, never()).save(any(Facility.class));
    }

    @Test
    public void testSave_OneNewlyAddedFacilityRemovesAndAddsNothingIfTheOperatorCannotBeFound() {
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);
        when(operator.isAutoApproveRelationships()).thenReturn(true);
        facilitiesEdit.setAutoApproveRelationships(true);
        when(operator.isCorporate()).thenReturn(true);
        setupOperatorFacilities(500, 501);
        facilitiesEdit.setFacilities(Arrays.asList(500, 501, 502));
        // return an existing facility so that the facilitiesDAO save is not called for a new facility
        when(facilitiesDAO.findByCorpOp(NON_ZERO_OPERATOR_ID, 502)).thenReturn(mock(Facility.class));

        facilitiesEdit.save();

        verify(facilitiesDAO, never()).remove(any(Facility.class));
        verify(facilitiesDAO, never()).save(any(Facility.class));
    }

    @Test
    public void testSave_OneNewlyAddedFacilityRemovesNothingAndSavesNewFacilityIfTheOperatorCanBeFound() {
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);
        when(operator.isAutoApproveRelationships()).thenReturn(true);
        facilitiesEdit.setAutoApproveRelationships(true);
        when(operator.isCorporate()).thenReturn(true);
        setupOperatorFacilities(500, 501);
        facilitiesEdit.setFacilities(Arrays.asList(500, 501, 502));
        when(operator2.getId()).thenReturn(502);
        when(operatorDAO.find(502)).thenReturn(operator2);

        facilitiesEdit.save();

        ArgumentCaptor<Facility> captor = ArgumentCaptor.forClass(Facility.class);

        verify(facilitiesDAO, never()).remove(any(Facility.class));
        verify(facilitiesDAO, times(2)).save(captor.capture());
        List<Facility> newFacilities = captor.getAllValues();
        Facility newFacility = newFacilities.get(1);

        assertThatOperatorIsOneOfTheFacilityOperators(newFacility.getOperator());
    }

    @Test
    public void testSave_OneNewlyRemovedFacilityRemovesIt() {
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);
        when(operator.isAutoApproveRelationships()).thenReturn(true);
        facilitiesEdit.setAutoApproveRelationships(true);
        when(operator.isCorporate()).thenReturn(true);
        setupOperatorFacilities(500, 501);
        facilitiesEdit.setFacilities(Arrays.asList(500));

        facilitiesEdit.save();

        ArgumentCaptor<Facility> captor = ArgumentCaptor.forClass(Facility.class);
        verify(facilitiesDAO).remove(captor.capture());
        Facility removedFacility = captor.getValue();

        assertTrue(removedFacility.getOperator().getId() == 501);
        assertTrue(operatorFacilities.get(0).getOperator().getId() == 500);
    }

    @Test
    public void testSave_OneNewlyRemovedFacilityIfItIsOperatorParentThenParentIsNulledAndOperatorIsSaved() {
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);
        when(operator.isAutoApproveRelationships()).thenReturn(true);
        facilitiesEdit.setAutoApproveRelationships(true);
        when(operator.isCorporate()).thenReturn(true);
        setupOperatorFacilities(500, 501);
        facilitiesEdit.setFacilities(Arrays.asList(500));
        OperatorAccount removedOperator = operatorFacilities.get(1).getOperator();
        removedOperator.setParent(operator);

        facilitiesEdit.save();

        assertNull(removedOperator.getParent());
        verify(operatorDAO).save(removedOperator);
    }

    private void assertThatOperatorIsOneOfTheFacilityOperators(OperatorAccount operatorFromNewFacility) {
        boolean found = false;
        for (Facility facility : operatorFacilities) {
            if (facility.getOperator().getId() == operatorFromNewFacility.getId()) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new AssertionFailedError();
        }
    }

    @Test
    public void testSave_AuditColumnsAreSetOnOperator() {

    }

    private void setupOperatorFacilities(Integer... ids) {
        facilitiesEdit.setFacilities(new ArrayList<Integer>());
        for (Integer id : ids) {
            Facility fac = new Facility();
            OperatorAccount op = new OperatorAccount();
            op.setId(id);
            fac.setOperator(op);
            operatorFacilities.add(fac);
        }
        when(operator.getOperatorFacilities()).thenReturn(operatorFacilities);
    }

    @Test
    public void testSave_PicsGlobalIsAdded() {
        facilitiesEdit.save();

        verify(facilitiesEditModel).addPicsGlobal(operator, permissions);
    }

    @Test
    public void testSave_PicsCountryAddedIfNotInPicsConsortium() {
        when(operator.isInPicsConsortium()).thenReturn(false);

        facilitiesEdit.save();

        verify(facilitiesEditModel).addPicsCountry(operator, permissions);
    }

    @Test
    public void testSave_PicsCountryNotAddedIfInPicsConsortium() {
        when(operator.isInPicsConsortium()).thenReturn(true);

        facilitiesEdit.save();

        verify(facilitiesEditModel, never()).addPicsCountry(operator, permissions);
    }

    @Test
    public void testSave_ContactIdNotSetDoesNotSetPrimaryContact() {
        facilitiesEdit.setContactID(0);

        facilitiesEdit.save();

        verify(operator, never()).setPrimaryContact(any(User.class));
    }

    @Test
    public void testSave_NullExistingPrimaryContactWillSetPrimaryContact() {
        facilitiesEdit.setContactID(NON_ZERO_USER_ID);
        when(operator.getPrimaryContact()).thenReturn(null);

        facilitiesEdit.save();

        verify(userDAO).find(NON_ZERO_USER_ID);
        verify(operator).setPrimaryContact(any(User.class));
    }

    @Test
    public void testSave_SameExistingPrimaryContactWillNotSetPrimaryContact() {
        facilitiesEdit.setContactID(NON_ZERO_USER_ID);
        when(operator.getPrimaryContact()).thenReturn(new User(NON_ZERO_USER_ID));

        facilitiesEdit.save();

        verify(operator, never()).setPrimaryContact(any(User.class));
    }

    @Test
    public void testSave_SaveClientSite_SetsNeedIndexing() {
        facilitiesEdit.save();

        verify(operator).setNeedsIndexing(true);
    }

    @Test
    public void testSave_SaveClientSite_Saves() {
        facilitiesEdit.save();

        // there are mulitple saves. We could perhaps differentiate by what has been set in the operator using an
        // argument capture?
        verify(operatorDAO, atLeastOnce()).save(operator);
    }

    @Test
    public void testSave_SaveClientSite_DeactivatedDeactivates() {
        when(operator.getStatus()).thenReturn(AccountStatus.Deactivated);

        facilitiesEdit.save();

        verify(accountStatusChanges).deactivateClientSite(eq(operator), eq(permissions), eq(AccountStatusChanges.OPERATOR_MANUALLY_DEACTIVATED_REASON), anyString());
    }

    @Test
    public void testSave_SuccessReturnsSuccessActionMesssageAndRedirect() {
        facilitiesEdit.save();

        verify(translationService).getText(eq(SUCCESS_KEY), eq(Locale.ENGLISH), anyVararg());
    }

    @Test
    public void testSave_IfOperatorNewAndManageOperatorEditPermissionThenNaicsSet() {
        when(operator.getId()).thenReturn(ZERO_OPERATOR_ID);
        when(operator.getNaics()).thenReturn(naics);
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);

        facilitiesEdit.save();

        verify(operator).setNaics(any(Naics.class));
        verify(naics).setCode("0");
    }

    @Test
    public void testSave_IfOperatorNewAndManageOperatorEditPermissionThenInheritCriteriaSet() {
        when(operator.getId()).thenReturn(ZERO_OPERATOR_ID);
        when(operator.getNaics()).thenReturn(naics);
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);

        facilitiesEdit.save();

        verify(operator).setInheritFlagCriteria(operator);
        verify(operator).setInheritInsuranceCriteria(operator);
    }

    @Test
    public void testSave_IfOperatorNewAndManageOperatorEditPermissionThenQuickbooksInfoSet() {
        when(operator.getId()).thenReturn(ZERO_OPERATOR_ID);
        when(operator.getNaics()).thenReturn(naics);
        when(permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)).thenReturn(true);
        when(operatorDAO.save(operator)).thenAnswer(new Answer<OperatorAccount>() {
            @Override
            public OperatorAccount answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                OperatorAccount op = (OperatorAccount) arguments[0];
                when(op.getId()).thenReturn(NON_ZERO_OPERATOR_ID);
                return op;
            }
        });

        facilitiesEdit.save();

        verify(operator).setQbListID("NOLOAD" + NON_ZERO_OPERATOR_ID);
        verify(operator).setQbListCAID("NOLOAD" + NON_ZERO_OPERATOR_ID);
    }

}