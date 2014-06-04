package com.picsauditing.employeeguard.services.aop.docdownload;

import com.picsauditing.access.Permissions;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.DocumentViewAccessDeniedException;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CorpOpDocViewPermsTest {
	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private Permissions permissions;

	@Mock
	private ContractorDocViewPerms nextInChain;

	@Mock
	private SessionInfoProvider sessionInfoProvider;

	@Mock
	private SkillEntityService skillEntityService;

	@Mock
	private AccountSkill accountSkill;

	@Mock
	private AccountService accountService;

	private static final int SKILL_ID = 11;
	private static final int DOCUMENT_ID = 9;
	private static final int APP_USER_ID = 131073;
	private static final int ACCOUNT_ID = 55653;
	private static final int ANOTHER_ACCOUNT_ID = 55654;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		SpringUtils springUtils = new SpringUtils();
		springUtils.setApplicationContext(applicationContext);
		when(applicationContext.getBean("SkillEntityService")).thenReturn(skillEntityService);
		when(applicationContext.getBean("AccountService")).thenReturn(accountService);
		when(skillEntityService.find(any(Integer.class))).thenReturn(accountSkill);
		when(accountService.getTopmostCorporateAccountIds(any(Integer.class))).thenReturn(Arrays.asList(ACCOUNT_ID));
		when(accountSkill.getAccountId()).thenReturn(ACCOUNT_ID);

	}

	@Test
	public void testChkPermissions_Allowed() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);

		CorpOpDocViewPerms corpOpViewPerms = new CorpOpDocViewPerms();

		DocViewableStatus docViewableStatus = corpOpViewPerms.chkPermissions(DOCUMENT_ID, SKILL_ID);

		assertEquals("Expected Result to be " + DocViewableStatus.ALLOWED.toString(), DocViewableStatus.ALLOWED.toString(), docViewableStatus.toString());

	}

	@Test(expected = DocumentViewAccessDeniedException.class)
	public void testChkPermissions_DocumentViewAccessDeniedException() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(accountSkill.getAccountId()).thenReturn(ANOTHER_ACCOUNT_ID);

		CorpOpDocViewPerms corpOpViewPerms = new CorpOpDocViewPerms();

		corpOpViewPerms.chkPermissions(DOCUMENT_ID, SKILL_ID);


	}

	@Test
	public void testChkPermissions_VerifyNextInChainIsCalled() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(false);


		CorpOpDocViewPerms corpOpViewPerms = new CorpOpDocViewPerms();
		corpOpViewPerms.attach(nextInChain);

		corpOpViewPerms.chkPermissions(DOCUMENT_ID, SKILL_ID);

		verify(nextInChain).chkPermissions(DOCUMENT_ID, SKILL_ID);

	}

	@Test
	public void testChkPermissions_VerifyUNKNOWNStatusIsEvaluated() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(-999);
		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(false);

		CorpOpDocViewPerms corpOpViewPerms = new CorpOpDocViewPerms();

		DocViewableStatus docViewableStatus = corpOpViewPerms.chkPermissions(DOCUMENT_ID, SKILL_ID);

		assertEquals("Expected Result to be " + DocViewableStatus.UNKNOWN.toString(), DocViewableStatus.UNKNOWN.toString(), docViewableStatus.toString());

	}

}
