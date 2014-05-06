package com.picsauditing.featuretoggle;
import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

    @Label("Translations as a Service")
    USE_TRANSLATION_SERVICE_ADAPTER,
    @Label("StrikeIron address verification service")
    USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE,
    @Label("EmployeeGUARD Billing Rules")
    USE_NEW_EMPLOYEE_GUARD_RULES,
    @Label("Require users to accept a EULA on login")
    USE_EULA;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
