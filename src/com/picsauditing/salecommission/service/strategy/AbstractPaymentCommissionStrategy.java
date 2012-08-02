package com.picsauditing.salecommission.service.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentAppliedToRefund;
import com.picsauditing.jpa.entities.PaymentCommission;
import com.picsauditing.jpa.entities.Transaction;

/**
 * This class contains all the logic for the processing of PaymentCommissions 
 */
public abstract class AbstractPaymentCommissionStrategy implements PaymentCommissionStrategy<Payment> {
	
	public final List<PaymentCommission> calculatePaymentCommission(final Payment payment) {
		final List<Transaction> transactions = getTransactions(payment.getApplied());
		if (hasStrategyAlreadyBeenProcessed(transactions)) {
			return Collections.emptyList();
		}
		
		final List<InvoiceCommission> invoiceCommissions = findInvoiceCommissions(transactions);
		if (CollectionUtils.isEmpty(invoiceCommissions)) {
			return Collections.emptyList();
		}
		
		return buildPaymentCommissions(payment, invoiceCommissions);
	}
	
	protected abstract boolean hasStrategyAlreadyBeenProcessed(final List<Transaction> transactions);
	
	protected abstract List<InvoiceCommission> findInvoiceCommissions(final List<Transaction> transactions);
	
	protected List<PaymentCommission> buildPaymentCommissions(final Payment payment, final List<InvoiceCommission> invoiceCommissions) {
		List<PaymentCommission> results = new ArrayList<PaymentCommission>();
		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
			PaymentCommission paymentCommission = new PaymentCommission();
			paymentCommission.setInvoiceCommission(invoiceCommission);
			paymentCommission.setPayment(payment);
			paymentCommission.setAuditColumns(payment.getUpdatedBy());
			paymentCommission.setActivationPoints(calculateActivatePoints(invoiceCommission, payment));
			paymentCommission.setPaymentAmount(calculateRevenueAmount(invoiceCommission, payment));
			results.add(paymentCommission);
		}

		return results;
	}
	
	protected abstract float calculateActivatePoints(InvoiceCommission invoiceCommission, Payment payment);
	
	protected abstract BigDecimal calculateRevenueAmount(InvoiceCommission invoiceCommission, Payment payment); 
	
	private final List<Transaction> getTransactions(final List<PaymentApplied> paymentsApplied) {
		if (CollectionUtils.isEmpty(paymentsApplied)) {
			return Collections.emptyList();
		}
		
		List<Transaction> transactions = new ArrayList<Transaction>();
		for (PaymentApplied paymentApplied : paymentsApplied) {
			if (paymentApplied instanceof PaymentAppliedToInvoice) {
				transactions.add(((PaymentAppliedToInvoice) paymentApplied).getInvoice());
			}
			else if (paymentApplied instanceof PaymentAppliedToRefund) {
				transactions.add(((PaymentAppliedToRefund) paymentApplied).getRefund());
			}
		}
		
		return transactions;
	}

}
