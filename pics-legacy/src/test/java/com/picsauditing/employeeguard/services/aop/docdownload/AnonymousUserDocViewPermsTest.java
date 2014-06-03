package com.picsauditing.employeeguard.services.aop.docdownload;

import com.picsauditing.access.Permissions;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.DocumentViewAccessDeniedException;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnonymousUserDocViewPermsTest {
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
		when(skillEntityService.find(any(Integer.class))).thenReturn(accountSkill);
		when(accountSkill.getAccountId()).thenReturn(ACCOUNT_ID);

	}

	@Test(expected = DocumentViewAccessDeniedException.class)
	public void testChkPermissions_DocumentViewAccessDeniedException() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(accountSkill.getAccountId()).thenReturn(ANOTHER_ACCOUNT_ID);

		AnonymousUserDocViewPerms anonymouseViewPerms = new AnonymousUserDocViewPerms();

		anonymouseViewPerms.chkPermissions(DOCUMENT_ID, SKILL_ID);


	}

}
