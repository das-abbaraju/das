package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.picsauditing.PICS.BrainTreeService;

@Entity
@DiscriminatorValue(value = "R")
public class Refund extends Transaction {

	private PaymentMethod paymentMethod;
	private String checkNumber;
	private String transactionID;
	private String ccNumber;

	private List<PaymentAppliedToRefund> payments = new ArrayList<PaymentAppliedToRefund>();

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
		BrainTreeService.CreditCard cc = new BrainTreeService.CreditCard();
		cc.setCardNumber(ccNumber);
		return cc.getCardType();
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
