package com.picsauditing.actions.contractors;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

public class ContractorEditTest extends PicsActionTest {
	private ContractorEdit classUnderTest;

	@Mock
	private ContractorAccount mockContractor;
	@Mock
	private Country mockCountry;
	@Mock
	private CountrySubdivision countrySubdivision;
	@Mock
	private CountrySubdivisionDAO countrySubdivisionDAO;
	@Mock
	private Note note;
	@Mock
	private BasicDAO basicDAO;
	@Mock
	private ContractorAccountDAO mockContractorAccountDao;
	@Mock
	private ContractorValidator mockConValidator;
	@Mock
	private User mockUser;
	@Mock
	private UserDAO mockUserDao;
	@Mock
	private ServletContext mockServletContext;
	@Mock
	private NoteDAO mockNoteDao;

    //Recreating Test Class --BLatner
    private final static int TESTING_CONTACT_ID = 555;
    private final static int TESTING_ACCOUNT_ID = 2323;
    private final static int NON_MATHCHING_ID = 23456;

    @Before
	public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new ContractorEdit();
		super.setUp(classUnderTest);

        classUnderTest.setContractor(mockContractor);
        classUnderTest.contractorAccountDao = mockContractorAccountDao;
        classUnderTest.contractorValidator = mockConValidator;
        classUnderTest.userDAO = mockUserDao;
        setInternalState(classUnderTest, "noteDao", mockNoteDao);

	    when(mockContractor.getCountry()).thenReturn(mockCountry);
        when(mockContractor.getId()).thenReturn(TESTING_ACCOUNT_ID);

		when(i18nCache.hasKey(anyString(), any(Locale.class))).thenReturn(true);
		when(i18nCache.getText(anyString(), any(Locale.class))).thenReturn("foo");
		when(i18nCache.getText(anyString(), any(Locale.class), any())).thenReturn("foo");
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
		when(request.getParameter(anyString())).thenReturn(null);

        classUnderTest.save();

        verify(mockNoteDao, never()).save(any(Note.class));
    }

    @Test
    public void testSave_AddNote_StatusChanged() throws Exception {
        classUnderTest.setContactID(0);
        save_justGetThroughTheMethod();
		when(request.getParameter(anyString())).thenReturn(AccountStatus.Deactivated.toString());
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Active);

        classUnderTest.save();

        verify(mockNoteDao).save(any(Note.class));
    }

    @Test
    public void testSave_DoNotAddNote_NoStatusChange() throws Exception {
        classUnderTest.setContactID(0);
        save_justGetThroughTheMethod();
		when(request.getParameter(anyString())).thenReturn(AccountStatus.Active.toString());
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Active);

        classUnderTest.save();

        verify(mockNoteDao, never()).save(any(Note.class));

    }

    private void save_justGetThroughTheMethod() {
        when(mockConValidator.validateContractor(mockContractor)).thenReturn(new Vector<String>());
        when(mockContractor.getAccountLevel()).thenReturn(AccountLevel.Full);
		when(permissions.isContractor()).thenReturn(true);
    }

    @Test
    public void testCheckContractorTypes_isNotContractor () {
		when(permissions.isContractor()).thenReturn(false);
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
		when(permissions.isContractor()).thenReturn(true);
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
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.hasPermission(OpPerms.ContractorAccounts, OpType.Edit)).thenReturn(false);

        classUnderTest.save();

        verify(basicDAO, never()).save((BaseTable) any());
        verify(mockContractorAccountDao, never()).save((BaseTable) any());
        verify(mockUserDao, never()).save((User) any());
        verify(mockNoteDao, never()).save((Note) any());
    }
}
