package com.picsauditing.billing;

import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

public class PaymentServiceFactory {

    private static FeatureToggle featureToggle;

    public static PaymentService paymentService() {
        if (featureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_USE_MOCK_PAYMENT_SERVICE)) {
            return new MockPaymentService();
        } else {
            return SpringUtils.getBean(SpringUtils.BrainTree);
        }
    }

    private static FeatureToggle featureToggle() {
        if (featureToggle == null) {
            return SpringUtils.getBean(SpringUtils.FEATURE_TOGGLE);
        } else {
            return featureToggle;
        }
    }
}
