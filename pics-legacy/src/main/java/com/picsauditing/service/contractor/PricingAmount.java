package com.picsauditing.service.contractor;

import com.picsauditing.jpa.entities.FeeClass;

import java.math.BigDecimal;

public class PricingAmount {
    private FeeClass feeClass;

    private BigDecimal feeAmount;

    private boolean applies;

    public PricingAmount(FeeClass feeClass, BigDecimal feeAmount) {
        this.feeClass = feeClass;
        this.feeAmount = feeAmount;
    }

    public FeeClass getFeeClass() {
        return feeClass;
    }

    public void setFeeClass(FeeClass feeClass) {
        this.feeClass = feeClass;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public boolean isApplies() {
        return applies;
    }

    public void setApplies(boolean applies) {
        this.applies = applies;

    }

    public String toString() {
        return feeClass + "-" + feeAmount + "-" + applies;
    }
}