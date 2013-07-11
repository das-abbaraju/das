package com.picsauditing.jpa.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("C")
public class CreditMemoAppliedToInvoice extends TransactionApplied {

    private InvoiceCreditMemo creditMemo;
    private Invoice invoice;

	@ManyToOne(optional = false)
	@JoinColumn(name = "invoiceID")
	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

    @ManyToOne(optional = false)
    @JoinColumn(name = "paymentID")
    public InvoiceCreditMemo getCreditMemo() {
        return creditMemo;
    }

    public void setCreditMemo(InvoiceCreditMemo creditMemo) {
        this.creditMemo = creditMemo;
    }

}
