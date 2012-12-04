package com.picsauditing.salecommission.strategy.payment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.dao.PaymentCommissionDAO;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.Transaction;

/**
 * This class contains all the logic for the processing of PaymentCommissions 
 */
public abstract class AbstractPaymentCommissionStrategy implements PaymentCommissionStrategy<Payment> {
	
	@Autowired
	protected PaymentCommissionDAO paymentCommissionDAO;
	
	@Override
	public final void processPaymentCommission(Payment payment) {
		final List<Transaction> transactions = getTransactions(payment);
		if (strategyAlreadyProcessed(transactions)) {
			return;
		}

		processPaymentCommissions(payment);
	}
	
	protected abstract boolean strategyAlreadyProcessed(final List<Transaction> transactions);
	
	protected abstract void processPaymentCommissions(final Payment payment);
	
	private final List<Transaction> getTransactions(final Payment payment) {
		List<PaymentAppliedToInvoice> paymentAppliedToInvoices = payment.getInvoices();
		if (CollectionUtils.isEmpty(paymentAppliedToInvoices)) {
			return Collections.emptyList();
		}
		
		List<Transaction> transactions = new ArrayList<Transaction>();
		for (PaymentAppliedToInvoice paymentAppliedToInvoice : paymentAppliedToInvoices) {
			transactions.add(paymentAppliedToInvoice.getInvoice());
		}
		
		return transactions;
	}

}