package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.persistence.Column;

import com.picsauditing.braintree.CreditCard;

@Entity
@DiscriminatorValue(value = "R")
public class Refund extends Transaction {

	private PaymentMethod paymentMethod;
	private String checkNumber;
	private String transactionID;
	private String ccNumber;

	private List<PaymentAppliedToRefund> payments = new ArrayList<PaymentAppliedToRefund>();

    private RefundAppliedToCreditMemo creditMemo;

    @Enumerated(EnumType.STRING)
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	@OneToMany(mappedBy = "refund", cascade = { CascadeType.REMOVE })
	public List<PaymentAppliedToRefund> getPayments() {
		return payments;
	}

	public void setPayments(List<PaymentAppliedToRefund> payments) {
		this.payments = payments;
	}

	@Column(name = "ccNumber")
	public String getCcNumber() {
		return ccNumber;
	}

	public void setCcNumber(String ccNumber) {
		this.ccNumber = ccNumber;
	}

	@Transient
	public String getCcType() {
		return new CreditCard(ccNumber).getCardType();
	}

    @OneToOne(mappedBy = "refund", cascade = { CascadeType.REMOVE })
    public RefundAppliedToCreditMemo getCreditMemo() {
        return creditMemo;
    }

    public void setCreditMemo(RefundAppliedToCreditMemo creditMemo) {
        this.creditMemo = creditMemo;
    }

    @Transient
	public void updateAmountApplied() {
		amountApplied = BigDecimal.ZERO;
		for (PaymentApplied ip : payments) {
			amountApplied = amountApplied.add(ip.getAmount());
		}
		super.updateAmountApplied();
	}

}
