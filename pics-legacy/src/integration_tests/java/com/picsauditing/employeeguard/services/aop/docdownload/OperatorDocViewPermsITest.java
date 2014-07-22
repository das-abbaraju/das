package com.picsauditing.employeeguard.services.aop.docdownload;

import com.picsauditing.access.Permissions;
import com.picsauditing.employeeguard.exceptions.DocumentViewAccessDeniedException;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:EGITest-localhost-context.xml"} )
@WebAppConfiguration
@Transactional
public class OperatorDocViewPermsITest {

	@Mock
	private Permissions permissions;

	@Mock
	private ContractorDocViewPerms nextInChain;

	@Mock
	private SessionInfoProvider sessionInfoProvider;

	@Autowired
	private ProfileDocumentService profileDocumentService;

	private static final int SKILL_ID = 11;
	private static final int EMPLOYEE_ID = 9;
	private static final int APP_USER_ID = 131073;
	private static final int ACCOUNT_ID = 55653;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testChkPermissions_Allowed() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(APP_USER_ID);
		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);

		OperatorDocViewPerms corpOpViewPerms = new OperatorDocViewPerms();

		DocViewableStatus docViewableStatus = corpOpViewPerms.chkPermissions(EMPLOYEE_ID, SKILL_ID);

		assertEquals("Expected Result to be " + DocViewableStatus.ALLOWED.toString(), DocViewableStatus.ALLOWED.toString(), docViewableStatus.toString());

	}

	@Test(expected = DocumentViewAccessDeniedException.class)
	public void testChkPermissions_DocumentViewAccessDeniedException() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(APP_USER_ID);
		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);

		OperatorDocViewPerms corpOpViewPerms = new OperatorDocViewPerms();

		int contractorSkillId = 18;
		corpOpViewPerms.chkPermissions(EMPLOYEE_ID, contractorSkillId);

	}

	@Test
	public void testChkPermissions_VerifyNextInChainIsCalled() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(-999);
		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(false);


		OperatorDocViewPerms corpOpViewPerms = new OperatorDocViewPerms();
		corpOpViewPerms.attach(nextInChain);

		corpOpViewPerms.chkPermissions(EMPLOYEE_ID, SKILL_ID);

		verify(nextInChain).chkPermissions(EMPLOYEE_ID, SKILL_ID);

	}

	@Test
	public void testChkPermissions_VerifyUNKNOWNStatusIsEvaluated() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(-999);
		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(false);

		OperatorDocViewPerms corpOpViewPerms = new OperatorDocViewPerms();

		DocViewableStatus docViewableStatus = corpOpViewPerms.chkPermissions(EMPLOYEE_ID, SKILL_ID);

		assertEquals("Expected Result to be " + DocViewableStatus.UNKNOWN.toString(), DocViewableStatus.UNKNOWN.toString(), docViewableStatus.toString());

	}

}
