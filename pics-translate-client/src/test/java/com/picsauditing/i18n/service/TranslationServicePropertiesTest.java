package com.picsauditing.i18n.service;

import com.picsauditing.i18n.model.UsageContext;
import com.picsauditing.i18n.model.logging.TranslationUsageLogger;
import com.picsauditing.i18n.model.strategies.TranslationStrategy;
import com.picsauditing.i18n.service.validation.TranslationKeyValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;

public class TranslationServicePropertiesTest {

    @Mock
    private TranslationStrategy translationStrategy;
    @Mock
    private TranslationUsageLogger translationUsageLogger;
    @Mock
    private TranslationKeyValidator translationKeyValidator;
    @Mock
    private UsageContext context;

    private String translationCommandKey = "TranslateCommandKey";

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuildTranslationServiceProperties() throws Exception {
        TranslationServiceProperties properties = new TranslationServiceProperties.Builder()
                .context(context)
                .translationCommandKey(translationCommandKey)
                .translationKeyValidator(translationKeyValidator)
                .translationStrategy(translationStrategy)
                .translationUsageLogger(translationUsageLogger)
                .build();

        assertTrue(properties.getContext().equals(context));
        assertTrue(properties.getTranslationCommandKey().equals(translationCommandKey));
        assertTrue(properties.getTranslationKeyValidator().equals(translationKeyValidator));
        assertTrue(properties.getTranslationStrategy().equals(translationStrategy));
        assertTrue(properties.getTranslationUsageLogger().equals(translationUsageLogger));
    }
}
