package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;

public class ContractorFeeBuilder {
    private ContractorFee contractorFee = new ContractorFee();

    public ContractorFee build() {
        return contractorFee;
    }


    public ContractorFeeBuilder currentLevel(InvoiceFee invoiceFee) {
        contractorFee.setCurrentLevel(invoiceFee);
        return this;
    }

    public ContractorFeeBuilder newLevel(InvoiceFee invoiceFee) {
        contractorFee.setNewLevel(invoiceFee);
        return this;
    }

    public ContractorFeeBuilder feeClass(FeeClass feeClass) {
        contractorFee.setFeeClass(feeClass);
        return this;
    }
}
