package com.picsauditing.salecommission.service.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.dao.PaymentCommissionDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentAppliedToRefund;
import com.picsauditing.jpa.entities.PaymentCommission;
import com.picsauditing.jpa.entities.Transaction;

public class PaymentRemoveStrategy extends AbstractPaymentCommissionStrategy {
	
	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;
//	@Override
//	public List<PaymentCommission> calculatePaymentCommission(Payment payment) {
////		row = Payment
////		
////		if (row != null) {
////			em.remove(row);
////		}
////		for (PaymentAppliedToInvoice ip : row.getInvoices()) {
////			ip.getInvoice().updateAmountApplied();
////		}
////		for (PaymentAppliedToRefund ip : row.getRefunds()) {
////			ip.getRefund().updateAmountApplied();
////		}
////		row = null;
//		
//		List<Transaction> transactions = getTransactions(payment.getApplied());
//		List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findByTransactions(transactions);
//		if (CollectionUtils.isEmpty(invoiceCommissions)) {
//			return Collections.emptyList();
//		}
//
//		List<PaymentCommission> results = new ArrayList<PaymentCommission>();
//		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
//			PaymentCommission paymentCommission = new PaymentCommission();
//			paymentCommission.setInvoiceCommission(invoiceCommission);
//			paymentCommission.setPayment(payment);
//			paymentCommission.setAuditColumns(payment.getUpdatedBy());
//			paymentCommission.setActivationPoints(0);
//			paymentCommission.setPaymentAmount(calculateRevenueAmount(payment, invoiceCommission));
////			paymentCommissionDAO.save(paymentCommission);
//			results.add(paymentCommission);
//		}
//		
//		
//		
////		if (eventType == PaymentEventType.REMOVE) {
////			// remove related rows
////			List<PaymentCommission> paymentCommissions = paymentCommissionDAO.findByPaymentId(payment.getId());
////			for (PaymentCommission pc : paymentCommissions) {
////				paymentCommissionDAO.remove(pc);
////			}
//			
////			return;
////		}
//		
////		return Collections.emptyList();
//		return results;
//	}
//	
//	private List<Transaction> getTransactions(List<PaymentApplied> paymentsApplied) {
//		if (CollectionUtils.isEmpty(paymentsApplied)) {
//			return Collections.emptyList();
//		}
//		
//		List<Transaction> transactions = new ArrayList<Transaction>();
//		for (PaymentApplied paymentApplied : paymentsApplied) {
//			if (paymentApplied instanceof PaymentAppliedToInvoice) {
//				transactions.add(((PaymentAppliedToInvoice) paymentApplied).getInvoice());
//			}
//			else if (paymentApplied instanceof PaymentAppliedToRefund) {
//				transactions.add(((PaymentAppliedToRefund) paymentApplied).getRefund());
//			}
//		}
//		
//		return transactions;
//	}
	
	@Override
	protected boolean hasStrategyAlreadyBeenProcessed(List<Transaction> transactions) { 
		return false;
	}	

	@Override
	protected List<InvoiceCommission> findInvoiceCommissions(List<Transaction> transactions) {
		return  invoiceCommissionDAO.findByTransactions(transactions);
	}

//	@Override
//	protected List<PaymentCommission> buildPaymentCommissions(Payment payment, List<InvoiceCommission> invoiceCommissions) {
//		List<PaymentCommission> results = new ArrayList<PaymentCommission>();
//		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
//			PaymentCommission paymentCommission = new PaymentCommission();
//			paymentCommission.setInvoiceCommission(invoiceCommission);
//			paymentCommission.setPayment(payment);
//			paymentCommission.setAuditColumns(payment.getUpdatedBy());
//			paymentCommission.setActivationPoints(calculateActivatePoints(invoiceCommission, payment));
//			paymentCommission.setPaymentAmount(calculateRevenueAmount(payment, invoiceCommission));
////			paymentCommissionDAO.save(paymentCommission);
//			results.add(paymentCommission);
//		}
//
//		return results;
//	}
	
	/**
	 * The formula is (paymentAmount * revenuePercentage * -1) because this is a payment removal, so we store 
	 * it as a negative number so it will zero-out the previous payment when all amounts are aggregated.
	 */
	@Override
	protected BigDecimal calculateRevenueAmount(InvoiceCommission invoiceCommission, Payment payment) {
		return payment.getAmountApplied().multiply(BigDecimal.valueOf(invoiceCommission.getRevenuePercent())).multiply(BigDecimal.valueOf(-1));
	}
	
	@Override
	protected float calculateActivatePoints(InvoiceCommission invoiceCommision, Payment payment) {
		return 0;
	}

}
