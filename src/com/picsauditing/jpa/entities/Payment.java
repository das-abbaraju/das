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

import org.hibernate.annotations.Where;

import com.picsauditing.PICS.BrainTreeService;

@Entity
@DiscriminatorValue(value = "P")
public class Payment extends Transaction {

	private PaymentMethod paymentMethod;
	private String checkNumber;
	private String transactionID;
	private String ccNumber;

	private List<PaymentAppliedToInvoice> invoices = new ArrayList<PaymentAppliedToInvoice>();
	private List<PaymentAppliedToRefund> refunds = new ArrayList<PaymentAppliedToRefund>();

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

	@OneToMany(mappedBy = "payment", targetEntity = PaymentApplied.class, cascade = { CascadeType.ALL })
	@Where(clause = "paymentType='I'")
	public List<PaymentAppliedToInvoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<PaymentAppliedToInvoice> invoices) {
		this.invoices = invoices;
	}

	@OneToMany(mappedBy = "payment", targetEntity = PaymentApplied.class, cascade = { CascadeType.ALL })
	@Where(clause = "paymentType='R'")
	public List<PaymentAppliedToRefund> getRefunds() {
		return refunds;
	}

	public void setRefunds(List<PaymentAppliedToRefund> refunds) {
		this.refunds = refunds;
	}

	@Transient
	public void updateAmountApplied() {
		amountApplied = BigDecimal.ZERO;
		for (PaymentApplied ip : invoices) {
			amountApplied = amountApplied.add(ip.getAmount());
		}
		super.updateAmountApplied();
	}

}
