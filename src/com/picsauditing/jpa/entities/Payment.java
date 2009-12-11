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
@DiscriminatorValue(value = "P")
public class Payment extends Transaction {

	private PaymentMethod paymentMethod;
	private String checkNumber;
	private String transactionID;
	private String ccNumber;

	private List<PaymentApplied> applied = new ArrayList<PaymentApplied>();

	@Column(nullable = false)
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
	
	

	@OneToMany(mappedBy = "payment", cascade = { CascadeType.ALL })
	public List<PaymentApplied> getApplied() {
		return applied;
	}

	public void setApplied(List<PaymentApplied> applied) {
		this.applied = applied;
	}

	@Transient
	public List<PaymentAppliedToInvoice> getInvoices() {
		List<PaymentAppliedToInvoice> list = new ArrayList<PaymentAppliedToInvoice>();
		for(PaymentApplied pa : getApplied())
			if (pa.getClass().getSimpleName().equals("PaymentAppliedToInvoice"))
				list.add((PaymentAppliedToInvoice)pa);
		return list;
	}

	@Transient
	public List<PaymentAppliedToRefund> getRefunds() {
		List<PaymentAppliedToRefund> list = new ArrayList<PaymentAppliedToRefund>();
		for(PaymentApplied pa : getApplied())
			if (pa.getClass().getSimpleName().equals("PaymentAppliedToRefund"))
				list.add((PaymentAppliedToRefund)pa);
		return list;
	}

	@Transient
	public void updateAmountApplied() {
		amountApplied = BigDecimal.ZERO.setScale(2);
		for (PaymentApplied ip : applied) {
			amountApplied = amountApplied.add(ip.getAmount());
		}
		super.updateAmountApplied();
	}

}
