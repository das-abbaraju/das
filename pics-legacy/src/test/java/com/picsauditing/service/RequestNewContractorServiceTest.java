package com.picsauditing.service;

import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.contractors.RequestNewContractorAccount;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.user.UserManagementService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class RequestNewContractorServiceTest extends PicsTest {
    private static final int NON_ZERO_CONTRACTOR_ID = 765;
    private static final int NON_ZERO_USER_ID = 711;
    private static final int NON_ZERO_RELATIONSHIP_ID = 1278;
    private static final int LOGGED_IN_USER_ID = 941;
    private static final String CONTACT_FIRST_NAME = "Boba";
    private static final String CONTACT_LAST_NAME = "Fett";
    private static final String CONTACT_EMAIL = "BobaFett@example.com";
    private static final String CONTACT_PHONE = "123.123.1234";
    private static final String CONTACT_NOTE = "This contractor has been contacted";

    @Mock
    private ContractorAccountDAO contractorAccountDAO;
    @Mock
    private ContractorOperatorDAO contractorOperatorDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private UserManagementService userManagementService;
    @Mock
    private ContractorTagDAO contractorTagDAO;
    @Mock
    private Permissions permissions;
    @Mock
    private ContractorOperator requestRelationship;
    @Mock
    private User primaryContact;
    @Mock
    private ContractorAccount contractor;
    @Mock
    private OperatorAccount requestingOperator;
    @Mock
    private Naics naics;
    @Mock
    private OperatorTag operatorTag1;
    @Mock
    private OperatorTag operatorTag2;

    private RequestNewContractorService requestNewContractorService;
    private List<User> contractorUsers;
    private List<OperatorTag> operatorTags;
    private List<ContractorTag> contractorTags;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        requestNewContractorService = new RequestNewContractorService();
        requestNewContractorService.setPermissions(permissions);
        contractorUsers = new ArrayList<>();
        operatorTags = new ArrayList<>();
        operatorTags.add(operatorTag1);
        operatorTags.add(operatorTag2);
        contractorTags = new ArrayList<>();

        PicsTestUtil.autowireDAOsFromDeclaredMocks(requestNewContractorService, this);

        Whitebox.setInternalState(requestNewContractorService, "userManagementService", userManagementService);

        when(primaryContact.getId()).thenReturn(0);
        when(primaryContact.getFirstName()).thenReturn(CONTACT_FIRST_NAME);
        when(primaryContact.getLastName()).thenReturn(CONTACT_LAST_NAME);
        when(primaryContact.getEmail()).thenReturn(CONTACT_EMAIL);
        when(primaryContact.getPhone()).thenReturn(CONTACT_PHONE);

        when(contractor.getUsers()).thenReturn(contractorUsers);
        when(contractor.getId()).thenReturn(NON_ZERO_CONTRACTOR_ID);
        when(contractor.getNaics()).thenReturn(naics);
        when(contractor.getOperatorTags()).thenReturn(contractorTags);

        when(requestingOperator.getStatus()).thenReturn(AccountStatus.Active);
        when(permissions.getUserId()).thenReturn(LOGGED_IN_USER_ID);
    }

    @Test
    public void testSaveRelationship_WillCallDaoForSave() throws Exception {
        requestNewContractorService.saveRelationship(requestRelationship);

        verify(contractorOperatorDAO).save(requestRelationship);
    }

    @Test
    public void testPopulateRelationship_SetsAppropriateFieldsExistingRelationship() throws Exception {
        when(requestRelationship.getId()).thenReturn(NON_ZERO_RELATIONSHIP_ID);

        requestNewContractorService.populateRelationship(contractor, requestRelationship);

        verify(requestRelationship).setContractorAccount(contractor);
        verify(requestRelationship).setAuditColumns(permissions);
    }

    @Test
    public void testPopulateRelationship_NewRelationshipSetsFlagColorToClear() throws Exception {
        when(requestRelationship.getId()).thenReturn(0);

        requestNewContractorService.populateRelationship(contractor, requestRelationship);

        verify(requestRelationship).setFlagColor(FlagColor.Clear);
    }

    @Test
    public void testPopulateRelationship_ExistingRelationshipDoesNotSetFlagColorToClear() throws Exception {
        when(requestRelationship.getId()).thenReturn(NON_ZERO_RELATIONSHIP_ID);

        requestNewContractorService.populateRelationship(contractor, requestRelationship);

        verify(requestRelationship, never()).setFlagColor(FlagColor.Clear);
    }

    @Test
    public void testSavePrimaryContact() throws Exception {
        requestNewContractorService.savePrimaryContact(primaryContact);

        verify(userManagementService).saveWithAuditColumnsAndRefresh(primaryContact, permissions);
    }

    @Test
    public void testPopulatePrimaryContact_NewContactNewUsernameSetsAppropriateFields() throws Exception {
        requestNewContractorService.populatePrimaryContact(contractor, primaryContact);

        verify(primaryContact).setAccount(contractor);
        verify(primaryContact).setIsGroup(YesNo.No);
        verify(primaryContact).setUsername(CONTACT_EMAIL);
        verify(primaryContact).setName(CONTACT_FIRST_NAME + " " + CONTACT_LAST_NAME);
        verify(primaryContact).setPhoneIndex(anyString());
        verify(contractor).setPrimaryContact(primaryContact);
        assertThat(contractorUsers, hasItem(primaryContact));
    }

    @Test
    public void testPopulatePrimaryContact_NewContactSetsAuditColumns() throws Exception {
        requestNewContractorService.populatePrimaryContact(contractor, primaryContact);

        verify(primaryContact).setAuditColumns(permissions);
    }

    @Test
    public void testPopulatePrimaryContact_NewContactExistingUsernameAdjustsUsername() throws Exception {
        when(userDAO.duplicateUsername(primaryContact.getEmail(), 0)).thenReturn(true);

        requestNewContractorService.populatePrimaryContact(contractor, primaryContact);

        verify(primaryContact).setUsername(CONTACT_EMAIL + "-" + NON_ZERO_CONTRACTOR_ID);
    }

    // why do we set phone index only on existing contact? No idea, but that's the behavior that was in the legacy code
    @Test
    public void testPopulatePrimaryContact_ExistingContactOnlySetsPhoneIndex() throws Exception {
        when(primaryContact.getId()).thenReturn(NON_ZERO_USER_ID);

        requestNewContractorService.populatePrimaryContact(contractor, primaryContact);

        verify(primaryContact).getId();
        verify(primaryContact).getPhone();
        verify(primaryContact).setPhoneIndex(anyString());
        verifyNoMoreInteractions(primaryContact);
    }

    @Test
    public void testSaveRequestedContractor_WillCallDaoForSave() throws Exception {
        requestNewContractorService.saveRequestedContractor(contractor);

        verify(contractorAccountDAO).save(contractor);
    }

    @Test
    public void testPopulateRequestedContractor_ExistingContractorNoContact_OnlySetAuditColumns() throws Exception {
        requestNewContractorService.populateRequestedContractor(contractor, requestingOperator, null, null);

        verify(contractor).getId();
        verify(contractor).setAuditColumns(permissions);
        verifyNoMoreInteractions(contractor);
    }

    @Test
    public void testPopulateRequestedContractor_NewContractorSetsAppropriateFields() throws Exception {
        when(contractor.getId()).thenReturn(0);

        requestNewContractorService.populateRequestedContractor(contractor, requestingOperator, null, null);

        verify(contractor).setNaics(any(Naics.class));
        verify(naics).setCode("0");
        verify(contractor).setRequestedBy(requestingOperator);
        verify(contractor).generateRegistrationHash();
    }

    @Test
    public void testPopulateRequestedContractor_NewContractorActivePayingRequestingOperatorSetsPayingFacilities() throws Exception {
        when(contractor.getId()).thenReturn(0);
        when(requestingOperator.getDoContractorsPay()).thenReturn("Yes");

        requestNewContractorService.populateRequestedContractor(contractor, requestingOperator, null, null);

        verify(contractor).setPayingFacilities(1);
    }

    @Test
    public void testPopulateRequestedContractor_NewContractorNotActiveRequestingOperatorDoesNotSetPayingFacilities() throws Exception {
        when(requestingOperator.getStatus()).thenReturn(AccountStatus.Deactivated);

        requestNewContractorService.populateRequestedContractor(contractor, requestingOperator, null, null);

        verify(contractor, never()).setPayingFacilities(1);
    }

    @Test
    public void testPopulateRequestedContractor_ContractorsDoNotPayDoesNotSetPayingFacilities() throws Exception {
        when(contractor.getId()).thenReturn(0);
        when(requestingOperator.getDoContractorsPay()).thenReturn("No");

        requestNewContractorService.populateRequestedContractor(contractor, requestingOperator, null, null);

        verify(contractor, never()).setPayingFacilities(1);
    }

    @Test
    public void testPopulateRequestedContractor_NullRequestingOperatorDoesNotThrow() throws Exception {
        try {
            requestNewContractorService.populateRequestedContractor(contractor, null, null, null);
        } catch (Exception e) {
            fail("This should be null guarded");
        }
    }

    @Test
    public void testPopulateRequestedContractor_DeclinedContactTypeSetsAppropriateFields() throws Exception {
        requestNewContractorService.populateRequestedContractor(
                contractor, requestingOperator, RequestNewContractorAccount.RequestContactType.DECLINED, CONTACT_NOTE);

        verify(contractor).setStatus(AccountStatus.Declined);
        verify(contractor).setReason(CONTACT_NOTE);
    }

    @Test
    public void testPopulateRequestedContractor_EmailContactTypeSetsAppropriateFields() throws Exception {
        requestNewContractorService.populateRequestedContractor(
                contractor, requestingOperator, RequestNewContractorAccount.RequestContactType.EMAIL, CONTACT_NOTE);
        verify(contractor).contactByEmail();
    }

    @Test
    public void testPopulateRequestedContractor_PhoneContactTypeSetsAppropriateFields() throws Exception {
        requestNewContractorService.populateRequestedContractor(
                contractor, requestingOperator, RequestNewContractorAccount.RequestContactType.PHONE, CONTACT_NOTE);
        verify(contractor).contactByPhone();
    }

    @Test
    public void testPopulateRequestedContractor_AnyContactTypeSetsLastContactedFields_Declined() throws Exception {
        requestNewContractorService.populateRequestedContractor(
                contractor, requestingOperator, RequestNewContractorAccount.RequestContactType.DECLINED, CONTACT_NOTE);
        verify(contractor).setLastContactedByInsideSales(LOGGED_IN_USER_ID);
        verify(contractor).setLastContactedByInsideSalesDate(any(Date.class));
    }

    @Test
    public void testPopulateRequestedContractor_AnyContactTypeSetsLastContactedFields_Email() throws Exception {
        requestNewContractorService.populateRequestedContractor(
                contractor, requestingOperator, RequestNewContractorAccount.RequestContactType.EMAIL, CONTACT_NOTE);
        verify(contractor).setLastContactedByInsideSales(LOGGED_IN_USER_ID);
        verify(contractor).setLastContactedByInsideSalesDate(any(Date.class));

        requestNewContractorService.populateRequestedContractor(
                contractor, requestingOperator, RequestNewContractorAccount.RequestContactType.PHONE, CONTACT_NOTE);
    }

    @Test
    public void testPopulateRequestedContractor_AnyContactTypeSetsLastContactedFields_Phone() throws Exception {
        requestNewContractorService.populateRequestedContractor(
                contractor, requestingOperator, RequestNewContractorAccount.RequestContactType.PHONE, CONTACT_NOTE);
        verify(contractor).setLastContactedByInsideSales(LOGGED_IN_USER_ID);
        verify(contractor).setLastContactedByInsideSalesDate(any(Date.class));
    }

    @Test
    public void testAddTagsToContractor_NullTagsDoesNotThrow() throws Exception {
        try {
            requestNewContractorService.addTagsToContractor(contractor, null);
        } catch (Exception e) {
            fail("This should not throw an exception");
        }
    }

    @Test
    public void testAddTagsToContractor_OperatorTagsAreCopiedToContractorTags() throws Exception {
        requestNewContractorService.addTagsToContractor(contractor, operatorTags);

        for(ContractorTag tag: contractorTags) {
            assertEquals(tag.getContractor(), contractor);
            assertThat(operatorTags, hasItem(tag.getTag()));
        }
        verify(contractorTagDAO, times(2)).save(any(ContractorTag.class));
    }

    // currently the contractor is saved regardless of if tags are added. This seems wasteful, and this test should be
    // adjusted if this behavior is adjusted
    @Test
    public void testAddTagsToContractor_ContractorIsSaved() throws Exception {
        requestNewContractorService.addTagsToContractor(contractor, operatorTags);
        verify(contractorAccountDAO).save(contractor);
    }

}
