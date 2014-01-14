package com.picsauditing.servlet.listener;

import com.picsauditing.dao.TranslationUsageDAO;
import com.picsauditing.i18n.model.database.TranslationUsage;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.model.general.AppPropertyProvider;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.servlet.ServletContextEvent;

import java.util.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class TranslationServiceCacheWarmerTest {
    private static String TEST_KEY = "TestKey";
    private static String TEST_LOCALE = "TestLocale";

    private TranslationServiceCacheWarmer translationServiceCacheWarmer;
    private List<TranslationUsage> translationUsages;

    @Mock
    private TranslationService translationService;
    @Mock
    private FeatureToggle featureToggleChecker;
    @Mock
    private AppPropertyProvider appPropertyProvider;
    @Mock
    private ServletContextEvent arg0;
    @Mock
    private TranslationUsageDAO usageDAO;
    @Mock
    private TranslationUsage translationUsage;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translationServiceCacheWarmer = new TranslationServiceCacheWarmer();

        translationUsages = new ArrayList<>();
        translationUsages.add(translationUsage);

        Whitebox.setInternalState(TranslationServiceCacheWarmer.class, "appPropertyProvider", appPropertyProvider);
        Whitebox.setInternalState(translationServiceCacheWarmer, "translationService", translationService);
        Whitebox.setInternalState(translationServiceCacheWarmer, "featureToggleChecker", featureToggleChecker);
        Whitebox.setInternalState(TranslationServiceCacheWarmer.class, "usageDAO", usageDAO);

        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(true);
        when(usageDAO.translationsUsedSince(any(Date.class))).thenReturn(translationUsages);
        when(translationUsage.getMsgKey()).thenReturn(TEST_KEY);
        when(translationUsage.getMsgLocale()).thenReturn(TEST_LOCALE);
    }

    @Test
    public void testContextInitialized_TranslationServiceToggleOff_ServiceNeverCalled() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(false);

        translationServiceCacheWarmer.contextInitialized(arg0);

        verify(translationService, never()).getText(anyString(), anyString());
    }

    @Test
    public void testContextInitialized_TranslationServiceToggleOn_ServiceCalled() throws Exception {
        translationServiceCacheWarmer.contextInitialized(arg0);

        verify(translationService, atLeastOnce()).getText(anyString(), anyString());
    }

    @Test
    public void testContextInitialized_TranslationServiceToggleOnButCacheWarmingDisabled_ServiceNeverCalled() throws Exception {
        when(appPropertyProvider.findAppProperty(TranslationServiceCacheWarmer.DISABLE_CACHE_WARMING)).thenReturn("NotEmpty");

        translationServiceCacheWarmer.contextInitialized(arg0);

        verify(translationService, never()).getText(anyString(), anyString());
    }

    @Test
    public void testContextInitialized_TranslationServiceToggleOn_En_US_Also_Called_For_En_Log() throws Exception {
        when(translationUsage.getMsgLocale()).thenReturn(TranslationServiceCacheWarmer.ENGLISH_LOCALE);

        translationServiceCacheWarmer.contextInitialized(arg0);

        verify(translationService).getText(TEST_KEY, TranslationServiceCacheWarmer.ENGLISH_LOCALE);
        verify(translationService).getText(TEST_KEY, TranslationServiceCacheWarmer.ENGLISH_US_LOCALE);
    }

}
