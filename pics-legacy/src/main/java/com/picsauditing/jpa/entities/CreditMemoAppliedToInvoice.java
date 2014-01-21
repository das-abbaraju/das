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

    @Transient
    public void updateAmountApplied() {
        invoice.updateTotalAmount();
        creditMemo.updateAmountApplied();
        setAmount(creditMemo.getTotalAmount());
        invoice.updateAmountApplied();
    }

    public static CreditMemoAppliedToInvoice from(Invoice inv) {
        CreditMemoAppliedToInvoice applied = new CreditMemoAppliedToInvoice();
        applied.setCreationDate(new Date());
        applied.setUpdateDate(new Date());
        applied.setInvoice(inv);
		InvoiceCreditMemo creditMemo = new InvoiceCreditMemo();
		creditMemo.setCurrency(inv.getCurrency());
		creditMemo.setAccount(inv.getAccount());
        applied.setCreditMemo(creditMemo);
        inv.getCreditMemos().add(applied);
        return applied;
    }

}
