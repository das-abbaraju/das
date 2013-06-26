package com.picsauditing.model.i18n;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditType;

public class EntityTranslationHelperTest extends PicsTranslationTest {

	@Mock
	private AuditType auditType;
	@Mock
	private Permissions permissions;
	@Captor
	private ArgumentCaptor<List<String>> captor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();
	}

	@Test
	public void test_saveRequiredTranslationsForAuditTypeName_SavesNameInUsersLocale() throws Exception {
		commonSetupSaveRequiredTranslationsForAuditTypeName();

		EntityTranslationHelper.saveRequiredTranslationsForAuditTypeName(auditType, permissions);

		verify(translationService).saveTranslation(eq("KEY"), eq("NAME"), captor.capture());

		assertTrue(captor.getValue().contains(Locale.ENGLISH.getLanguage()));
	}

	@Test
	public void test_saveRequiredTranslationsForAuditTypeName_SavesInAllRequiredLanguages() throws Exception {
		commonSetupSaveRequiredTranslationsForAuditTypeName();

		List<String> requiredLanguages = new ArrayList<>();
		requiredLanguages.add(Locale.ENGLISH.getLanguage());
		requiredLanguages.add(Locale.FRENCH.getLanguage());
		requiredLanguages.add(Locale.JAPANESE.getLanguage());
		when(auditType.getLanguages()).thenReturn(requiredLanguages);

		EntityTranslationHelper.saveRequiredTranslationsForAuditTypeName(auditType, permissions);

		verify(translationService).saveTranslation(eq("KEY"), eq("NAME"), captor.capture());

		// the required plus always the logged in user's locale since they're
		// editing the name in their language
		assertTrue(captor.getValue().size() == 4);
	}

	private void commonSetupSaveRequiredTranslationsForAuditTypeName() {
		when(auditType.getI18nKey("name")).thenReturn("KEY");
		when(auditType.getName()).thenReturn("NAME");
		when(permissions.getLocale()).thenReturn(Locale.ENGLISH);
	}

}
