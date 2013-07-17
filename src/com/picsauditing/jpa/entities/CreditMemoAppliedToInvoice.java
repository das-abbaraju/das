package com.picsauditing.jpa.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("C")
public class CreditMemoAppliedToInvoice extends TransactionApplied {

    private InvoiceCreditMemo creditMemo;
    private Invoice invoice;

	@ManyToOne(optional = false, cascade = {CascadeType.ALL})
	@JoinColumn(name = "invoiceID")
	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

    @ManyToOne(optional = false, cascade = {CascadeType.ALL})
    @JoinColumn(name = "paymentID")
    public InvoiceCreditMemo getCreditMemo() {
        return creditMemo;
    }

    public void setCreditMemo(InvoiceCreditMemo creditMemo) {
        this.creditMemo = creditMemo;
    }

    public static CreditMemoAppliedToInvoice from(Invoice inv) {
        CreditMemoAppliedToInvoice applied = new CreditMemoAppliedToInvoice();
        applied.setInvoice(inv);
        applied.setCreditMemo(new InvoiceCreditMemo());
        return applied;
    }

}
