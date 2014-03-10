package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;

import java.math.BigDecimal;

public class InvoiceFeeBuilder {
    private InvoiceFee invoiceFee = new InvoiceFee();

    public InvoiceFee build() {
        return invoiceFee;
    }

    public InvoiceFeeBuilder feeClass(FeeClass feeClass) {
        invoiceFee.setFeeClass(feeClass);
        return this;
    }

    public InvoiceFeeBuilder minFacilities(int minFacilities) {
        invoiceFee.setMinFacilities(minFacilities);
        return this;
    }

    public InvoiceFeeBuilder amount(BigDecimal amount) {
        invoiceFee.setAmount(amount);
        return this;
    }

    public InvoiceFeeBuilder maxFacilities(int maxFacilities) {
        invoiceFee.setMaxFacilities(maxFacilities);
        return this;
    }
}
