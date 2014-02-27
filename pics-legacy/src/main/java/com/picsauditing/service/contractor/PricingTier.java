package com.picsauditing.service.contractor;

import java.util.List;

public class PricingTier {
    private String level;

    private List<PricingAmount> pricingAmounts;

    public PricingTier(String level, List<PricingAmount> pricingAmounts) {
        this.level = level;
        this.pricingAmounts = pricingAmounts;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<PricingAmount> getPricingAmounts() {
        return pricingAmounts;
    }

    public void setPricingAmounts(List<PricingAmount> pricingAmounts) {
        this.pricingAmounts = pricingAmounts;
    }

    public String toString() {
        return level + ": " + pricingAmounts;
    }
}
