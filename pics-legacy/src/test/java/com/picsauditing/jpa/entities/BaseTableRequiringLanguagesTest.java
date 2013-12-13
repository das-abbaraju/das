package com.picsauditing.jpa.entities;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.model.general.AppPropertyProvider;
import com.picsauditing.model.i18n.ThreadLocalLocale;
import com.picsauditing.service.i18n.ExplicitUsageContext;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BaseTableRequiringLanguagesTest extends PicsTranslationTest {
    private TestableBaseTableRequiringLanguages baseTableRequiringLanguages;

    @Mock
    private ExplicitUsageContext explicitUsageContext;
    @Mock
    private FeatureToggle featureToggleChecker;
    @Mock
    private AppPropertyProvider appPropertyProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        baseTableRequiringLanguages = new TestableBaseTableRequiringLanguages();
        ThreadLocalLocale.INSTANCE.set(Locale.ENGLISH);
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(true);
        Whitebox.setInternalState(TranslationServiceFactory.class, "featureToggleChecker", featureToggleChecker);
        Whitebox.setInternalState(TranslationServiceFactory.class, "appPropertyProvider", appPropertyProvider);
    }

    @Test
    public void testTranslatedString() throws Exception {
        String i18nKey = baseTableRequiringLanguages.getI18nKey("name");

        baseTableRequiringLanguages.translatedString("name");

        verify(translationService).getText(i18nKey, Locale.ENGLISH);
    }

    private class TestableBaseTableRequiringLanguages extends BaseTableRequiringLanguages {

        @Override
        public void cascadeRequiredLanguages(List<String> add, List<String> remove) {

        }

        @Override
        protected ExplicitUsageContext context() {
            return explicitUsageContext;
        }

        @Override
        public boolean hasMissingChildRequiredLanguages() {
            return false;
        }
    }
}
