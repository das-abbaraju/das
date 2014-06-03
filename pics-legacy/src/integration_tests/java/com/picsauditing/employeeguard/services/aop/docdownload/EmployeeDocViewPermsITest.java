package com.picsauditing.employeeguard.services.aop.docdownload;

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
public class EmployeeDocViewPermsITest {


	@Mock
	private CorpOpDocViewPerms nextInChain;

	@Mock
	private SessionInfoProvider sessionInfoProvider;

	@Autowired
	private ProfileDocumentService profileDocumentService;

	private static final int SKILL_ID = 11;
	private static final int DOCUMENT_ID = 9;
	private static final int APP_USER_ID = 131073;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testChkPermissions_Allowed() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(APP_USER_ID);

		EmployeeDocViewPerms employeeDocViewPerms = new EmployeeDocViewPerms();

		DocViewableStatus docViewableStatus = employeeDocViewPerms.chkPermissions(DOCUMENT_ID, SKILL_ID);

		assertEquals("Expected Result to be " + DocViewableStatus.ALLOWED.toString(), DocViewableStatus.ALLOWED.toString(), docViewableStatus.toString());

	}

	@Test
	public void testChkPermissions_VerifyNextInChainIsCalled() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(-999);

		EmployeeDocViewPerms employeeDocViewPerms = new EmployeeDocViewPerms();
		employeeDocViewPerms.attach(nextInChain);

		int skillId=-999;
		employeeDocViewPerms.chkPermissions(DOCUMENT_ID, skillId);

		verify(nextInChain).chkPermissions(DOCUMENT_ID, skillId);

	}

	@Test
	public void testChkPermissions_VerifyUNKNOWNStatusIsEvaluated() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(-999);

		EmployeeDocViewPerms employeeDocViewPerms = new EmployeeDocViewPerms();

		int skillId=-999;
		DocViewableStatus docViewableStatus = employeeDocViewPerms.chkPermissions(DOCUMENT_ID, skillId);

		assertEquals("Expected Result to be " + DocViewableStatus.UNKNOWN.toString(), DocViewableStatus.UNKNOWN.toString(), docViewableStatus.toString());

	}

}
