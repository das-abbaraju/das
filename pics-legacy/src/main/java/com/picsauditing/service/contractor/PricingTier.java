package com.picsauditing.service.contractor;

import com.picsauditing.util.Strings;

import java.util.Iterator;
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

    public String computeFeeString(){
        if (pricingAmounts == null || pricingAmounts.size() == 0) {
            return Strings.EMPTY_STRING;
        }
        StringBuilder retStr = new StringBuilder();
        for (Iterator<PricingAmount> iterator = pricingAmounts.iterator(); iterator.hasNext(); ) {
            PricingAmount pricingAmount = iterator.next();
            retStr.append(pricingAmount.getFeeAmount()).append(",");
        }
        return retStr.toString();
    }

    public int hashCode() {
        if(pricingAmounts == null) return super.hashCode();
        return computeFeeString().hashCode();
    }

    public boolean equals(Object obj) {
        if(pricingAmounts == null) return super.equals(obj);
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PricingTier)) {
            return false;
        }
        PricingTier other = (PricingTier) obj;
        return this.computeFeeString().equals(other.computeFeeString());
    }

}
