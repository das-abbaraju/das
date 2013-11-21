package com.picsauditing.i18n.service;

import com.picsauditing.i18n.model.DefaultUsageContext;
import com.picsauditing.i18n.model.UsageContext;
import com.picsauditing.i18n.model.logging.TranslationKeyDoNothingLogger;
import com.picsauditing.i18n.model.logging.TranslationUsageLogger;
import com.picsauditing.i18n.model.strategies.EmptyTranslationStrategy;
import com.picsauditing.i18n.model.strategies.TranslationStrategy;
import com.picsauditing.i18n.service.validation.TranslationKeyValidator;

public class TranslationServiceProperties {
    private TranslationStrategy translationStrategy;
    private TranslationUsageLogger translationUsageLogger;
    private String translationCommandKey;
    private TranslationKeyValidator translationKeyValidator;
    private UsageContext context;

    public TranslationServiceProperties() {}

    public TranslationServiceProperties(UsageContext context) {
        context = context;
    }

    public TranslationStrategy getTranslationStrategy() {
        return translationStrategy;
    }

    public TranslationUsageLogger getTranslationUsageLogger() {
        return translationUsageLogger;
    }

    public String getTranslationCommandKey() {
        return translationCommandKey;
    }

    public TranslationKeyValidator getTranslationKeyValidator() {
        return translationKeyValidator;
    }

    public UsageContext getContext() {
        return context;
    }

    public static class Builder {
        private TranslationStrategy translationStrategy = new EmptyTranslationStrategy();
        private TranslationUsageLogger translationUsageLogger = new TranslationKeyDoNothingLogger();
        private String translationCommandKey;
        private TranslationKeyValidator translationKeyValidator = new TranslationKeyValidator();
        private UsageContext context = new DefaultUsageContext();

        public Builder() {

        }

        public Builder translationStrategy(TranslationStrategy translationStrategy) {
            this.translationStrategy = translationStrategy;
            return this;
        }

        public Builder translationUsageLogger(TranslationUsageLogger translationUsageLogger) {
            this.translationUsageLogger = translationUsageLogger;
            return this;
        }

        public Builder translationCommandKey(String translationCommandKey) {
            this.translationCommandKey = translationCommandKey;
            return this;
        }

        public Builder translationKeyValidator(TranslationKeyValidator translationKeyValidator) {
            this.translationKeyValidator = translationKeyValidator;
            return this;
        }

        public Builder context(UsageContext context) {
            this.context = context;
            return this;
        }

        public TranslationServiceProperties build() {
            TranslationServiceProperties properties = new TranslationServiceProperties();
            properties.translationStrategy = translationStrategy;
            properties.translationUsageLogger = translationUsageLogger;
            properties.translationCommandKey = translationCommandKey;
            properties.translationKeyValidator = translationKeyValidator;
            properties.context = context;
            return properties;
        }
    }
}
