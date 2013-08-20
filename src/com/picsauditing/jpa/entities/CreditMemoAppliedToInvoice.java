package com.picsauditing.jpa.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue(value = "C")
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
        applied.setCreationDate(new Date());
        applied.setUpdateDate(new Date());
        applied.setInvoice(inv);
        applied.setCreditMemo(new InvoiceCreditMemo());
        applied.getCreditMemo().setAccount(inv.getAccount());
        return applied;
    }

}
