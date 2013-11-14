package com.picsauditing.model.i18n;

import com.netflix.hystrix.*;
import com.picsauditing.dao.TranslationUsageDAO;
import com.picsauditing.util.SpringUtils;

public class TranslationUsageLogCommand extends HystrixCommand<Boolean> {
    private static final String HYSTRIX_COMMAND_GROUP = "TranslationUsageLog";
    private static final int THREAD_TIMEOUT_MS = 2000;
    private static final int THREAD_POOL_SIZE = 100;
    private static final int CIRCUIT_BREAKER_THRESHOLD = 90;

    private TranslationLookupData usage;

    public TranslationUsageLogCommand(TranslationLookupData usage) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(HYSTRIX_COMMAND_GROUP))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionIsolationThreadTimeoutInMilliseconds(THREAD_TIMEOUT_MS)
                                .withCircuitBreakerErrorThresholdPercentage(CIRCUIT_BREAKER_THRESHOLD)
                ).andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter().withCoreSize(THREAD_POOL_SIZE)
                )
        );
        this.usage = usage;
    }

    @Override
    protected Boolean run() throws Exception {
        TranslationUsageDAO usageLogger = SpringUtils.getBean("translationKeyUsageDAO");
        usageLogger.logKeyUsage(usage);
        return Boolean.TRUE;
    }

    @Override
    protected Boolean getFallback() {
        // if the logging fails, just ignore it
        return Boolean.TRUE;
    }

}