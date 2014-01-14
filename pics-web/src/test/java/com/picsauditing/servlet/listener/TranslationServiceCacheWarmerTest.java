package com.picsauditing.servlet.listener;

import com.picsauditing.dao.TranslationUsageDAO;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.scheduling.SchedulingTaskExecutor;

import javax.servlet.ServletContextEvent;

import java.util.*;

import static org.mockito.Mockito.*;

public class TranslationServiceCacheWarmerTest {
    private static String TEST_KEY = "TestKey";
    private static String TEST_LOCALE = "TestLocale";

    private TranslationServiceCacheWarmer translationServiceCacheWarmer;
    private Set<String> usageLocales;
    private Map<String, Set<String>> usages;

    @Mock
    private TranslationService translationService;
    @Mock
    private FeatureToggle featureToggleChecker;
    @Mock
    private ServletContextEvent arg0;
    @Mock
    private TranslationUsageDAO usageDAO;
    @Mock
    private SchedulingTaskExecutor taskExecutor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translationServiceCacheWarmer = new TranslationServiceCacheWarmer();

        usageLocales = new HashSet<>();
        usageLocales.add(TEST_LOCALE);
        usages = new HashMap<>();
        usages.put(TEST_KEY, usageLocales);

        Whitebox.setInternalState(translationServiceCacheWarmer, "translationService", translationService);
        Whitebox.setInternalState(translationServiceCacheWarmer, "featureToggleChecker", featureToggleChecker);
        Whitebox.setInternalState(translationServiceCacheWarmer, "taskExecutor", taskExecutor);
        Whitebox.setInternalState(TranslationServiceCacheWarmer.class, "usageDAO", usageDAO);

        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(true);
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_DISABLE_TRANSLATION_SERVICE_CACHE_WARMING)).thenReturn(false);
        when(usageDAO.translationsUsedSince(any(Date.class))).thenReturn(usages);
    }

    @Test
    public void testContextInitialized_TranslationServiceToggleOff_ServiceNeverCalled() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(false);

        translationServiceCacheWarmer.contextInitialized(arg0);

        verify(taskExecutor, never()).execute(any(Runnable.class));
    }

    @Test
    public void testContextInitialized_TranslationServiceToggleOn_ServiceCalled() throws Exception {
        translationServiceCacheWarmer.contextInitialized(arg0);

        verify(taskExecutor).execute(any(Runnable.class));
    }

    @Test
    public void testContextInitialized_TranslationServiceToggleOnButCacheWarmingDisabled_ServiceNeverCalled() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_DISABLE_TRANSLATION_SERVICE_CACHE_WARMING)).thenReturn(true);

        translationServiceCacheWarmer.contextInitialized(arg0);

        verify(taskExecutor, never()).execute(any(Runnable.class));
    }

    @Test
    public void testContextInitialized_TranslationServiceToggleOn_En_US_Also_Called_For_En_Log() throws Exception {
        usageLocales = new HashSet<>();
        usageLocales.add(TranslationServiceCacheWarmer.ENGLISH_LOCALE);
        usages = new HashMap<>();
        usages.put(TEST_KEY, usageLocales);
        when(usageDAO.translationsUsedSince(any(Date.class))).thenReturn(usages);

        translationServiceCacheWarmer.run();

        verify(translationService).getText(TEST_KEY, TranslationServiceCacheWarmer.ENGLISH_LOCALE);
        verify(translationService).getText(TEST_KEY, TranslationServiceCacheWarmer.ENGLISH_US_LOCALE);
    }

}
