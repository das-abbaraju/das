package com.picsauditing.featuretoggle;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

    @Label("Translations as a Service")
    USE_TRANSLATION_SERVICE_ADAPTER;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
