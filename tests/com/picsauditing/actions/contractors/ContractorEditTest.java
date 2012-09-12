package com.picsauditing.actions.contractors;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import org.apache.struts2.ServletActionContext;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.util.SpringUtils;

import java.util.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({I18nCache.class, SpringUtils.class, ServletActionContext.class, AccountStatus.class })
public class ContractorEditTest {
	ContractorEdit classUnderTest;

	@Mock private ContractorAccount mockContractor;
	@Mock private Country mockCountry;
	@Mock private CountrySubdivision countrySubdivision;
	@Mock private CountrySubdivisionDAO countrySubdivisionDAO;
	@Mock private Note note;
	@Mock private BasicDAO basicDAO;
	@Mock private HttpServletRequest mockRequest;
	@Mock private AccountStatus accountStatus;
//    @Mock private AuditBuilder mockAuditBuilder;
    @Mock private ContractorAccountDAO mockContractorAccountDao;
    @Mock private ContractorValidator mockConValidator;
    @Mock private Permissions mockPermissions;
    @Mock private User mockUser;
	@Mock private UserDAO mockUserDao;
    @Mock private ServletContext mockServletContext;
    @Mock private I18nCache mockCache;
    @Mock private NoteDAO mockNoteDao;

    //Recreating Test Class --BLatner
    private final static int TESTING_CONTACT_ID = 555;
    private final static int TESTING_ACCOUNT_ID = 2323;
    private final static int NON_MATHCHING_ID = 23456;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ServletActionContext.class);
        when(ServletActionContext.getRequest()).thenReturn(mockRequest);
        when(ServletActionContext.getServletContext()).thenReturn(mockServletContext);
        PowerMockito.mockStatic(I18nCache.class);
        when(I18nCache.getInstance()).thenReturn(mockCache);

        classUnderTest = new ContractorEdit();
        classUnderTest.setContractor(mockContractor);
//        classUnderTest.auditBuilder = mockAuditBuilder;
        classUnderTest.contractorAccountDao = mockContractorAccountDao;
        classUnderTest.contractorValidator = mockConValidator;
        classUnderTest.userDAO = mockUserDao;
        setInternalState(classUnderTest, "noteDao", mockNoteDao);
        setInternalState(classUnderTest, "permissions", mockPermissions);

	    when(mockContractor.getCountry()).thenReturn(mockCountry);
        when(mockContractor.getId()).thenReturn(TESTING_ACCOUNT_ID);

        when(mockCache.hasKey(anyString(), any(Locale.class))).thenReturn(true);
        when(mockCache.getText(anyString(), any(Locale.class))).thenReturn("foo");
        when(mockCache.getText(anyString(), any(Locale.class), any())).thenReturn("foo");
    }

    /**
     * This test is inspired by PICS-6840. The solution to which was to immediately invoke AuditBuilder whenever
     * ContractorEdit.save() is called (as opposed to waiting for the contractor cron), because billing calculations
     * are based on the existence of assigned audits per contractor.
     * @throws Exception
     */
    @Test
    public void testSave_rebuildAudits() throws Exception {
        classUnderTest.setContactID(TESTING_CONTACT_ID);
        save_justGetThroughTheMethod();
        when(mockContractor.getPrimaryContact()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(NON_MATHCHING_ID);
        when(mockUserDao.find(TESTING_CONTACT_ID)).thenReturn(mockUser);

        // Now calls auditBuilder.buildAudits(contractor);
        classUnderTest.save();

//        verify(mockAuditBuilder).buildAudits(mockContractor);
        verify(mockContractor).setQbSync(true);
        verify(mockContractor).incrementRecalculation();
        verify(mockContractor).setNameIndex();
        verify(mockContractor).setPrimaryContact(mockUser);
        verify(mockContractorAccountDao).save(mockContractor);
    }

    @Test
    public void testHandleLocationChange_country () {
        when(mockContractor.getCountry()).thenReturn(new Country("US", "United States"));
        classUnderTest.setCountry(new Country("FR", "France"));

        classUnderTest.handleLocationChange();

        verify(mockContractor).setCountry(any(Country.class));
        verify(mockConValidator).setOfficeLocationInPqfBasedOffOfAddress(mockContractor);
        verify(mockNoteDao).save(any(Note.class));
    }

    //Rewrites of the original non-functional tests.
    @Test
    public void testSave_DoNotAddNote_NullCurrentStatus() throws Exception {
        classUnderTest.setContactID(0);
        save_justGetThroughTheMethod();
        when(mockRequest.getParameter(anyString())).thenReturn(null);

        classUnderTest.save();

        verify(mockNoteDao, never()).save(any(Note.class));
    }

    @Test
    public void testSave_AddNote_StatusChanged() throws Exception {
        classUnderTest.setContactID(0);
        save_justGetThroughTheMethod();
        when(mockRequest.getParameter(anyString())).thenReturn(AccountStatus.Deactivated.toString());
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Active);

        classUnderTest.save();

        verify(mockNoteDao).save(any(Note.class));
    }

    @Test
    public void testSave_DoNotAddNote_NoStatusChange() throws Exception {
        classUnderTest.setContactID(0);
        save_justGetThroughTheMethod();
        when(mockRequest.getParameter(anyString())).thenReturn(AccountStatus.Active.toString());
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Active);

        classUnderTest.save();

        verify(mockNoteDao, never()).save(any(Note.class));

    }

    private void save_justGetThroughTheMethod() {
        when(mockConValidator.validateContractor(mockContractor)).thenReturn(new Vector<String>());
        when(mockContractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        when(mockPermissions.isContractor()).thenReturn(true);
    }

    @Test
    public void testCheckContractorTypes_isNotContractor () {
        when(mockPermissions.isContractor()).thenReturn(false);
        when(mockContractor.isContractorTypeRequired(ContractorType.Onsite)).thenReturn(false);
        when(mockContractor.isContractorTypeRequired(ContractorType.Offsite)).thenReturn(true);
        when(mockContractor.isContractorTypeRequired(ContractorType.Supplier)).thenReturn(true);
        when(mockContractor.isContractorTypeRequired(ContractorType.Transportation)).thenReturn(true);
        classUnderTest.checkContractorTypes();
        verify(mockContractor).setAccountTypes((List<ContractorType>) any());
        verify(mockContractor).resetRisksBasedOnTypes();
    }

    @Test
    public void testCheckContractor_Types_isContractor() {
        when(mockPermissions.isContractor()).thenReturn(true);
        classUnderTest.checkContractorTypes();
        verify(mockContractor, never()).isContractorTypeRequired((ContractorType) any());
        verify(mockContractor, never()).setAccountTypes((List<ContractorType>) any());
        verify(mockContractor, never()).resetRisksBasedOnTypes();
    }

    @Test
    public void testConfirmConTypesOK_match () {
        OperatorAccount testOperator = mock(OperatorAccount.class);
        Set<ContractorType> testSet = new HashSet<ContractorType>();
        testSet.add(ContractorType.Offsite);
        testSet.add(ContractorType.Transportation);
        when(testOperator.getAccountTypes()).thenReturn(testSet);
        when(mockContractor.getOperatorAccounts()).thenReturn(Arrays.asList(new OperatorAccount[] {testOperator}));
        classUnderTest.setConTypes(Arrays.asList(new ContractorType[]{ContractorType.Transportation}));

        classUnderTest.confirmConTypesOK();

        assertFalse(classUnderTest.hasActionErrors());
    }

    @Test
    public void testConfirmConTypesOK_problem () {
        OperatorAccount testOperator = mock(OperatorAccount.class);
        Set<ContractorType> testSet = new HashSet<ContractorType>();
        testSet.add(ContractorType.Offsite);
        testSet.add(ContractorType.Transportation);
        when(testOperator.getAccountTypes()).thenReturn(testSet);
        when(mockContractor.getOperatorAccounts()).thenReturn(Arrays.asList(new OperatorAccount[] {testOperator}));
        classUnderTest.setConTypes(Arrays.asList(new ContractorType[]{ContractorType.Onsite}));

        classUnderTest.confirmConTypesOK();

        assertTrue(classUnderTest.hasActionErrors());
    }

    @Test
    public void testCheckListOnlyAcceptability_notListOnly() {
        when(mockContractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        classUnderTest.checkListOnlyAcceptability();
        assertFalse(classUnderTest.hasActionErrors());
    }

    @Test
    public void testCheckListOnlyAcceptability_notListOnlyEligible() {
        when(mockContractor.getAccountLevel()).thenReturn(AccountLevel.ListOnly);
        when(mockContractor.isListOnlyEligible()).thenReturn(false);
        when(mockContractor.getNonCorporateOperators()).thenReturn(new ArrayList<ContractorOperator>());

        classUnderTest.checkListOnlyAcceptability();

        assertTrue(classUnderTest.hasActionErrors());
        assertTrue(classUnderTest.getActionErrors().size() == 1);
    }

    @Test
    public void testCheckListOnlyAcceptability_hasOperatorsWhoDontAcceptListOnly () {
        ContractorOperator mockCO1 = mock(ContractorOperator.class),
                mockCO2 = mock(ContractorOperator.class);
        OperatorAccount mockOperator1 = mock(OperatorAccount.class),
                mockOperator2 = mock(OperatorAccount.class);
        when(mockCO1.getOperatorAccount()).thenReturn(mockOperator1);
        when(mockCO2.getOperatorAccount()).thenReturn(mockOperator2);
        when(mockOperator1.isAcceptsList()).thenReturn(true);
        when(mockOperator2.isAcceptsList()).thenReturn(false);
        when(mockOperator2.getName()).thenReturn("Failing Operator");

        when(mockContractor.getAccountLevel()).thenReturn(AccountLevel.ListOnly);
        when(mockContractor.isListOnlyEligible()).thenReturn(true);
        when(mockContractor.getNonCorporateOperators()).thenReturn(
                Arrays.asList(new ContractorOperator[]{mockCO1, mockCO2}));

        classUnderTest.checkListOnlyAcceptability();

        assertTrue(classUnderTest.hasActionErrors());
        assertTrue(classUnderTest.getActionErrors().size() == 1);
    }

    @Test
    public void testRunContractorValidator_newVAT_returnsError() {
        classUnderTest.setVatId("testVAT");
        Vector<String> testErrors = new Vector<String>();
        testErrors.add("foo");
        when(mockConValidator.validateContractor(mockContractor)).thenReturn(testErrors);
        classUnderTest.runContractorValidator();
        verify(mockConValidator).validateContractor(mockContractor);
        assertTrue(classUnderTest.hasActionErrors());
        verify(mockContractor).setVatId(anyString());
    }

    @Test
    public void testRunContractorValidator_newVAT_noErrors() {
        classUnderTest.setVatId("testVAT");
        when(mockConValidator.validateContractor(mockContractor)).thenReturn(new Vector<String>());
        classUnderTest.runContractorValidator();
        verify(mockConValidator).validateContractor(mockContractor);
        assertFalse(classUnderTest.hasActionErrors());
        verify(mockContractor).setVatId(anyString());
    }

    @Test
    public void testRunContractorValidator_noNewVAT_returnsError() {
        Vector<String> testErrors = new Vector<String>();
        testErrors.add("foo");
        when(mockConValidator.validateContractor(mockContractor)).thenReturn(testErrors);
        classUnderTest.runContractorValidator();
        verify(mockConValidator).validateContractor(mockContractor);
        assertTrue(classUnderTest.hasActionErrors());
        verify(mockContractor, never()).setVatId(anyString());
    }

    @Test
    public void testRunContractorValidator_noNewVAT_noErrors() {
        when(mockConValidator.validateContractor(mockContractor)).thenReturn(new Vector<String>());
        classUnderTest.runContractorValidator();
        verify(mockConValidator).validateContractor(mockContractor);
        assertFalse(classUnderTest.hasActionErrors());
        verify(mockContractor, never()).setVatId(anyString());
    }

    @Test
    public void testSave_wrongPerms() throws Exception {
        when(mockPermissions.isContractor()).thenReturn(false);
        when(mockPermissions.hasPermission(OpPerms.ContractorAccounts, OpType.Edit)).thenReturn(false);

        classUnderTest.save();

        verify(basicDAO, never()).save((BaseTable) any());
        verify(mockContractorAccountDao, never()).save((BaseTable) any());
        verify(mockUserDao, never()).save((User) any());
        verify(mockNoteDao, never()).save((Note) any());
    }
}
