package com.picsauditing.actions.contractors;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.PICS.I18nCache;
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

import java.util.Locale;
import java.util.Vector;

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
    @Mock private AuditBuilder mockAuditBuilder;
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
        classUnderTest.auditBuilder = mockAuditBuilder;
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

        verify(mockAuditBuilder).buildAudits(mockContractor);
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
}
