package com.picsauditing.service.i18n;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.TranslationDAO;
import com.picsauditing.i18n.model.logging.TranslationKeyDoNothingLogger;
import com.picsauditing.i18n.model.logging.TranslationUsageLogger;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.i18n.service.TranslationServiceAdapter;
import com.picsauditing.model.general.AppPropertyProvider;
import com.picsauditing.model.i18n.*;
import com.picsauditing.search.Database;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Locale;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TranslationServiceFactoryTest {
    private static Locale localLocaleSave;
    private static final String TEST_COMMAND_KEY = "TestCommandKey";

    @Mock
    private FeatureToggle featureToggleChecker;
    @Mock
    private I18nCache i18nCache;
    @Mock
    private TranslationDAO translationDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(true);
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_DISABLE_LOG_TRANSLATION_USAGE)).thenReturn(false);

        Whitebox.setInternalState(TranslationServiceFactory.class, "featureToggleChecker", featureToggleChecker);
        Whitebox.setInternalState(I18nCache.class, "appTranslationDAO", translationDAO);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        localLocaleSave = ThreadLocalLocale.INSTANCE.load();
        ThreadLocalLocale.INSTANCE.set(Locale.ENGLISH);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        ThreadLocalLocale.INSTANCE.set(localLocaleSave);
    }

    @Test
    public void testGetNonLoggingTranslationService_NewService() throws Exception {
        TranslationService service = TranslationServiceFactory.getNonLoggingTranslationService();
        TranslationUsageLogger logger = Whitebox.getInternalState(service, "translationUsageLogger");
        assertTrue(logger instanceof TranslationKeyDoNothingLogger);
    }

    @Test
    public void testGetNonLoggingTranslationService_OldServiceIsJustI18nCache() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(false);
        TranslationService service = TranslationServiceFactory.getNonLoggingTranslationService();
        assertTrue(service instanceof I18nCache);
    }

    @Test
    public void testGetTranslationService_NewService() throws Exception {
        TranslationService service = TranslationServiceFactory.getTranslationService();
        TranslationUsageLogger logger = Whitebox.getInternalState(service, "translationUsageLogger");
        assertTrue(logger instanceof TranslationKeyAggregateUsageLogger);
    }

    @Test
    public void testGetTranslationService_OldServiceIsJustI18nCache() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(false);
        TranslationService service = TranslationServiceFactory.getTranslationService();
        assertTrue(service instanceof I18nCache);
    }

    @Test
    public void testGetTranslationService_ForSpecificCommandKey() throws Exception {
        TranslationService service = TranslationServiceFactory.getTranslationService(TEST_COMMAND_KEY);
        String commandKey = Whitebox.getInternalState(service, "translateCommandKey");
        assertTrue(TEST_COMMAND_KEY.equals(commandKey));
    }

    @Test
    public void testGetTranslationService_DisableLogging() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_DISABLE_LOG_TRANSLATION_USAGE)).thenReturn(true);
        TranslationService service = TranslationServiceFactory.getTranslationService();
        TranslationUsageLogger logger = Whitebox.getInternalState(service, "translationUsageLogger");
        assertTrue(logger instanceof TranslationKeyDoNothingLogger);
    }

}
