package com.picsauditing.PICS;

import com.picsauditing.jpa.entities.*;

import java.math.BigDecimal;
import java.util.Date;

// TODO: This should be part of a TransactionVisitor
public class PaymentProcessor {
	public static PaymentAppliedToInvoice ApplyPaymentToInvoice(Payment payment, Invoice invoice, User user, BigDecimal amount) {
		payment.setCurrency(invoice.getCurrency());

		PaymentAppliedToInvoice pa = new PaymentAppliedToInvoice();
		pa.setPayment(payment);
		pa.setInvoice(invoice);
		pa.setAmount(amount);
		pa.setAuditColumns(user);
		payment.getApplied().add(pa);

		payment.updateAmountApplied();
		invoice.getPayments().add(pa);
		invoice.updateAmountApplied();

        if (invoice.getStatus() == TransactionStatus.Paid) {
            invoice.setPaidDate(new Date());
        }

        return pa;
	}

	public static void ApplyPaymentToRefund(Payment payment, Refund refund, User user, BigDecimal amount) {
		refund.setCurrency(payment.getCurrency());

		PaymentAppliedToRefund pa = new PaymentAppliedToRefund();
		pa.setPayment(payment);
		pa.setRefund(refund);
		pa.setAmount(amount);
		pa.setAuditColumns(user);
		payment.getApplied().add(pa);

		payment.updateAmountApplied();
	}

	public static Payment PayOffInvoice(Invoice invoice, User user, PaymentMethod paymentMethod) {
		Payment payment = new Payment();
		payment.setAccount(invoice.getAccount());
		payment.setCurrency(invoice.getCurrency());
		payment.setTotalAmount(invoice.getBalance());
		payment.setPaymentMethod(paymentMethod);
		payment.setAuditColumns(user);

		return payment;
	}
}
