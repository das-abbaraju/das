package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.web.SessionInfoProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:EGITest-localhost-context.xml"} )
public class ProfileDocumentServiceITest {

	@Mock
	private Permissions permissions;

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
	public void testUpdate_getDocumentThumbnail_CheckEmployeePermissions() throws Exception {
		when(sessionInfoProvider.getAppUserId()).thenReturn(APP_USER_ID);
		profileDocumentService.getAccountSkillProfileForEmployeeAndSkill(SKILL_ID, DOCUMENT_ID);
	}

}
