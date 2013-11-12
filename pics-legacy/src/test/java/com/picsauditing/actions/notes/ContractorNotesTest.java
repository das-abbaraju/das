package com.picsauditing.actions.notes;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.audits.AuditActionSupport;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ContractorNotesTest extends PicsTest {
    private ContractorNotes testClass;

    private final int everybodyEmailId = 1;
    private final int corpEmailId = 2;
    private final int siteEmailId = 3;
    private final int privateEmailId = 4;
    private final int otherSiteEmailId = 5;
    private final int ptherPrivateEmailId = 6;

    @Mock
    Permissions permissions;

    User corpUser = EntityFactory.makeUser();
    User siteUser = EntityFactory.makeUser();
    User otherUser = EntityFactory.makeUser();

    Account corpAccount = createAccount(200);
    Account siteAccount = createAccount(210);
    Account otherAccount = createAccount(300);
    Account everyAccount = createAccount(Account.EVERYONE);
    Account privateAccount = createAccount(Account.PRIVATE);
    Account otherPrivateAccount = createAccount(Account.PRIVATE);

    List<EmailQueue> emailList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        MockitoAnnotations.initMocks(this);

        testClass = new ContractorNotes();
        autowireEMInjectedDAOs(testClass);
        PicsTestUtil.forceSetPrivateField(testClass, "permissions", permissions);
    }

    @Test
    public void testCanPreviewEmail_CorporateOperator() throws Exception {
        Set<Integer> visibleAccounts = new HashSet<>();
        visibleAccounts.add(corpAccount.getId());
        visibleAccounts.add(siteAccount.getId());
        when(permissions.getVisibleAccounts()).thenReturn(visibleAccounts);

        initializeEmailList();

        assertTrue(testClass.canPreviewEmail(everybodyEmailId));
        assertTrue(testClass.canPreviewEmail(corpEmailId));
        assertTrue(testClass.canPreviewEmail(siteEmailId));
        assertFalse(testClass.canPreviewEmail(privateEmailId));
        assertFalse(testClass.canPreviewEmail(otherSiteEmailId));
        assertFalse(testClass.canPreviewEmail(ptherPrivateEmailId));
    }

    @Test
    public void testCanPreviewEmail_SiteOperator() throws Exception {
        Set<Integer> visibleAccounts = new HashSet<>();
        visibleAccounts.add(siteAccount.getId());
        when(permissions.getVisibleAccounts()).thenReturn(visibleAccounts);

        initializeEmailList();

        assertTrue(testClass.canPreviewEmail(everybodyEmailId));
        assertFalse(testClass.canPreviewEmail(corpEmailId));
        assertTrue(testClass.canPreviewEmail(siteEmailId));
        assertFalse(testClass.canPreviewEmail(privateEmailId));
        assertFalse(testClass.canPreviewEmail(otherSiteEmailId));
        assertFalse(testClass.canPreviewEmail(ptherPrivateEmailId));
    }

    @Test
    public void testCanPreviewEmail_Contractor() throws Exception {
        initializeEmailList();

        when(permissions.isContractor()).thenReturn(true);
        for (EmailQueue email:emailList) {
            assertTrue(testClass.canPreviewEmail(email.getId()));
        }
    }

    @Test
    public void testCanPreviewEmail_Admin() throws Exception {
        initializeEmailList();

        when(permissions.isAdmin()).thenReturn(true);
        for (EmailQueue email:emailList) {
            assertTrue(testClass.canPreviewEmail(email.getId()));
        }
    }

    private void initializeEmailList() {
        emailList.clear();

        emailList.add(createEmail(everybodyEmailId, everyAccount, corpUser));
        emailList.add(createEmail(corpEmailId, corpAccount, corpUser));
        emailList.add(createEmail(siteEmailId, siteAccount, siteUser));
        emailList.add(createEmail(privateEmailId, privateAccount, siteUser));
        emailList.add(createEmail(otherSiteEmailId, otherAccount, otherUser));
        emailList.add(createEmail(ptherPrivateEmailId, privateAccount, otherUser));

        PicsTestUtil.forceSetPrivateField(testClass, "emailList", emailList);
    }

    private Account createAccount(int id) {
        Account account = new Account();
        account.setId(id);
        return account;
    }

    private EmailQueue createEmail(int emailId, Account account, User user) {
        EmailQueue email = new EmailQueue();
        email.setId(emailId);
        email.setBodyViewableBy(account);
        email.setCreatedBy(user);
        return email;
    }
}
