package com.picsauditing.jpa.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Entity
@DiscriminatorValue("C")
public class InvoiceCreditMemo extends Transaction {

    private PaymentMethod paymentMethod = PaymentMethod.ReturnCreditMemo;
    private List<RefundItem> refundItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

	@OneToMany(mappedBy = "creditMemo", cascade = { CascadeType.ALL })
	public List<RefundItem> getItems() {
		Collections.sort(refundItems, new Comparator<RefundItem>() {
            @Override
            public int compare(RefundItem o1, RefundItem o2) {
                return o1.getInvoiceFee().getDisplayOrder().compareTo(o2.getInvoiceFee().getDisplayOrder());
            }
        });
		return refundItems;
	}

	public void setItems(List<RefundItem> items) {
        refundItems = items;
	}

    @Override
    @Transient
    public void updateAmountApplied() {
        BigDecimal total = BigDecimal.ZERO;
        for (RefundItem item : refundItems) {
            total = total.add(item.getAmount());
        }
        total = total.negate();
        setTotalAmount(total);
        setAmountApplied(total);
    }

    @Override
    @Transient
    public BigDecimal getBalance() {
        return amountApplied;
    }

}
