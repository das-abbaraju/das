package com.picsauditing.PICS;

import java.math.BigDecimal;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentAppliedToRefund;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.Refund;
import com.picsauditing.jpa.entities.User;

public class PaymentProcessor {
	public static void ApplyPaymentToInvoice(Payment payment, Invoice invoice, User user, BigDecimal amount) {
		payment.setCurrency(invoice.getCurrency());

		PaymentAppliedToInvoice pa = new PaymentAppliedToInvoice();
		pa.setPayment(payment);
		pa.setInvoice(invoice);
		pa.setAmount(amount);
		pa.setAuditColumns(user);
		payment.getApplied().add(pa);

		payment.updateAmountApplied();
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
