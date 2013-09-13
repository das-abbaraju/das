package com.picsauditing.jpa.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue(value = "M")
public class RefundAppliedToCreditMemo extends TransactionApplied {

    private InvoiceCreditMemo creditMemo;
    private Refund refund;

	@ManyToOne(optional = false, cascade = {CascadeType.ALL})
	@JoinColumn(name = "paymentID")
	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}

    @ManyToOne(optional = false, cascade = {CascadeType.ALL})
    @JoinColumn(name = "invoiceID")
    public InvoiceCreditMemo getCreditMemo() {
        return creditMemo;
    }

    public void setCreditMemo(InvoiceCreditMemo creditMemo) {
        this.creditMemo = creditMemo;
    }

    public static RefundAppliedToCreditMemo from(InvoiceCreditMemo invoiceCreditMemo) {
        RefundAppliedToCreditMemo refundApplied = new RefundAppliedToCreditMemo();
        refundApplied.setAuditColumns();
        refundApplied.setCreditMemo(invoiceCreditMemo);
        return refundApplied;
    }

}
