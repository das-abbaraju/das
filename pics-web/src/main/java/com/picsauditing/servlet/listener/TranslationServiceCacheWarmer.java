package com.picsauditing.servlet.listener;

import com.picsauditing.dao.TranslationUsageDAO;
import com.picsauditing.dao.jdbc.JdbcFeatureToggleProvider;
import com.picsauditing.i18n.model.database.TranslationUsage;
import com.picsauditing.i18n.model.logging.TranslationKeyDoNothingLogger;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.i18n.service.TranslationServiceProperties;
import com.picsauditing.service.i18n.ExplicitUsageContext;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.toggle.FeatureToggleCheckerGroovy;
import com.picsauditing.util.SpringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.SchedulingTaskExecutor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Date;
import java.util.List;

public class TranslationServiceCacheWarmer implements ServletContextListener, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TranslationServiceCacheWarmer.class);
    private static Date WARM_CACHE_WITH_VALUES_USED_SINCE = new DateTime().minusDays(2).toDate();

    public static final String ENGLISH_LOCALE = "en";
    public static final String ENGLISH_US_LOCALE = "en_US";

    // for test injection
    private TranslationService translationService;
    private SchedulingTaskExecutor taskExecutor;
    private static TranslationUsageDAO usageDAO;
    private FeatureToggle featureToggleChecker;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // do nothing
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        if (shouldWarmCache()) {
            logger.info("Executing async task to warm cache");
            taskExecutor().execute(this);
        }
    }

    public void run() {
        List<TranslationUsage> usages = translationUsageDAO().translationsUsedSince(WARM_CACHE_WITH_VALUES_USED_SINCE);
        logger.info("Warming {} msgKeys", usages.size());
        for(TranslationUsage usage : usages) {
            TranslationService service = nonLoggingTranslationServiceSpecificToPageName(usage);
            String locale = usage.getMsgLocale();
            service.getText(usage.getMsgKey(), locale);
            // since we log returned locales and not requested locales, but we cache requested locales and not
            // returned locales, and most of our locales requested for en_US will actually be defined in "en", let's always
            // warm keys for en_US as well
            if (ENGLISH_LOCALE.equals(locale)) {
                service.getText(usage.getMsgKey(), ENGLISH_US_LOCALE);
            }
        }
        logger.info("Finished warming cache");
    }

    private boolean shouldWarmCache() {
        return featureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER) &&
               !featureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_DISABLE_TRANSLATION_SERVICE_CACHE_WARMING);
    }

    private TranslationService nonLoggingTranslationServiceSpecificToPageName(TranslationUsage usage) {
        if (translationService == null) {
            TranslationServiceProperties properties = new TranslationServiceProperties.Builder()
                    .translationUsageLogger(new TranslationKeyDoNothingLogger())
                    .context(new ExplicitUsageContext(usage.getPageName(), null))
                    .build();
            translationService = TranslationServiceFactory.getTranslationService(properties);
        }
        return translationService;
    }

    private FeatureToggle featureToggle() {
        if (featureToggleChecker != null) {
            return featureToggleChecker;
        } else {
            // we're creating a new one each time to get it to re-evaluate "permissions" since MDO changed that to hold
            // the permissions object
            FeatureToggleCheckerGroovy featureToggleChecker = new FeatureToggleCheckerGroovy(new JdbcFeatureToggleProvider(), null);
            return featureToggleChecker;
        }
    }

    private static TranslationUsageDAO translationUsageDAO() {
        if (usageDAO == null) {
            usageDAO = SpringUtils.getBean("translationKeyUsageDAO");
        }
        return usageDAO;
    }

    private SchedulingTaskExecutor taskExecutor() {
        if (taskExecutor == null) {
            taskExecutor = SpringUtils.getBean("AsyncTaskExecutor");
        }
        return taskExecutor;
    }

}