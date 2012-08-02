package com.picsauditing.salecommission.service.strategy;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentCommission;
import com.picsauditing.jpa.entities.Transaction;

public class PaymentStrategy extends AbstractPaymentCommissionStrategy {
	
	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;
	
	
//	@Override
//	public List<PaymentCommission> calculatePaymentCommission(Payment payment) {
//		List<Transaction> transactions = getTransactions(payment.getApplied());
//		List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findByTransactions(transactions);
//		if (CollectionUtils.isEmpty(invoiceCommissions)) {
//			return Collections.emptyList();
//		}
//
//		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
//			PaymentCommission paymentCommission = new PaymentCommission();
//			paymentCommission.setInvoiceCommission(invoiceCommission);
//			paymentCommission.setPayment(payment);
//			paymentCommission.setAuditColumns(payment.getUpdatedBy());
//			paymentCommission.setActivationPoints(0);
//			paymentCommission.setPaymentAmount(calculateRevenueAmount(payment, invoiceCommission));
//			paymentCommissionDAO.save(paymentCommission);
//		}
//
//	}

	@Override
	protected boolean hasStrategyAlreadyBeenProcessed(List<Transaction> transactions) {
		return false;
	}
	
	@Override
	protected List<InvoiceCommission> findInvoiceCommissions(List<Transaction> transactions) {
		return invoiceCommissionDAO.findByTransactions(transactions);
	}

	@Override
	protected float calculateActivatePoints(InvoiceCommission invoiceCommission, Payment payment) {
		return 0;
	}

	@Override
	protected BigDecimal calculateRevenueAmount(InvoiceCommission invoiceCommission, Payment payment) {
		return payment.getAmountApplied().multiply(BigDecimal.valueOf(invoiceCommission.getRevenuePercent()));
	}	

}
