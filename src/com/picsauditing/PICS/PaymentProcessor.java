package com.picsauditing.PICS;

import java.math.BigDecimal;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentAppliedToRefund;
import com.picsauditing.jpa.entities.Refund;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.quickbooks.qbxml.PayeeEntityRef;

public class PaymentProcessor {
	public static void ApplyPaymentToInvoice(Payment payment, Invoice invoice, User user, BigDecimal amount) {
		PaymentAppliedToInvoice pa = new PaymentAppliedToInvoice();
		pa.setPayment(payment);
		pa.setInvoice(invoice);
		pa.setAmount(amount);
		pa.setAuditColumns(user);
		payment.getApplied().add(pa);
	}
	
	public static void ApplyPaymentToRefund(Payment payment, Refund refund, User user, BigDecimal amount) {
		PaymentAppliedToRefund pa = new PaymentAppliedToRefund();
		pa.setPayment(payment);
		pa.setRefund(refund);
		pa.setAmount(amount);
		pa.setAuditColumns(user);
		payment.getApplied().add(pa);
	}
	
	public static Payment PayOffInvoice(Invoice invoice, User user) {
		Payment payment = new Payment();
		payment.setAmountApplied(invoice.getBalance());
		payment.setAuditColumns(user);
		
		ApplyPaymentToInvoice(payment, invoice, user, invoice.getBalance());
		
		return payment;
	}
}
