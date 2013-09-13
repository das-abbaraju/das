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
    private List<ReturnItem> returnItems = new ArrayList<>();

    private List<RefundAppliedToCreditMemo> refunds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

	@OneToMany(mappedBy = "creditMemo", cascade = { CascadeType.ALL })
	public List<ReturnItem> getItems() {
		Collections.sort(returnItems, new Comparator<ReturnItem>() {
            @Override
            public int compare(ReturnItem o1, ReturnItem o2) {
                return o1.getInvoiceFee().getDisplayOrder().compareTo(o2.getInvoiceFee().getDisplayOrder());
            }
        });
		return returnItems;
	}

	public void setItems(List<ReturnItem> items) {
        returnItems = items;
	}

    @OneToMany(mappedBy = "creditMemo", cascade = { CascadeType.REMOVE })
    public List<RefundAppliedToCreditMemo> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<RefundAppliedToCreditMemo> refunds) {
        this.refunds = refunds;
    }

    @Transient
    public BigDecimal getCreditLeft() {
        BigDecimal refundBalance = BigDecimal.ZERO;
        refundBalance = refundBalance.add(totalAmount);
        for (RefundAppliedToCreditMemo refundAppliedToCreditMemo : refunds) {
            refundBalance = refundBalance.subtract(refundAppliedToCreditMemo.getAmount());
        }

        return refundBalance;
    }

    @Override
    @Transient
    public void updateAmountApplied() {
        BigDecimal total = BigDecimal.ZERO;
        for (ReturnItem item : returnItems) {
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
