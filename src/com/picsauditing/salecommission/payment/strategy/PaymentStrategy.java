package com.picsauditing.salecommission.payment.strategy;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.PICS.data.DataEvent;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.PICS.data.InvoiceDataEvent.InvoiceEventType;
import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.dao.PaymentCommissionDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentCommission;
import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.util.Strings;

/**
 * Built this strategy because the payment might have been applied to an Invoice
 * or a Refund, but based on the context when the payment was saved, we are unsure.
 */
public class PaymentStrategy extends AbstractPaymentCommissionStrategy {

	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;
	@Autowired
	private PaymentCommissionDAO paymentCommissionDAO;
	@Autowired
	private DataObservable salesCommissionDataObservable;
	
	/**
	 * This will generate Invoice Commissions, if they do not exist
	 */
	@Override
	protected final void processPaymentCommissions(Payment payment) {
		if (payment == null) {
			return;
		}
		
		List<PaymentAppliedToInvoice> paymentsForInvoices = payment.getInvoices();
		if (CollectionUtils.isEmpty(paymentsForInvoices)) {
			return;
		}
		
		processInvoiceCommissions(payment);
		
		for (PaymentAppliedToInvoice paymentForInvoice : paymentsForInvoices) {
			Invoice invoice = paymentForInvoice.getInvoice();
			List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findByInvoiceId(invoice.getId());
			processPaymentCommissions(payment, invoiceCommissions);
		}
	}			
	
	/**
	 * This method is responsible for generating the InvoiceCommission for any invoices created before
	 * the InvoiceCommissions were out on Live.
	 */
	private void processInvoiceCommissions(Payment payment) {
		List<PaymentAppliedToInvoice> paymentAppliedToInvoices = payment.getInvoices();
		for (PaymentAppliedToInvoice appliedToInvoice : paymentAppliedToInvoices) {
			Invoice invoice = appliedToInvoice.getInvoice();
			if (invoice != null) {
				List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findByInvoiceId(invoice.getId());
				if (CollectionUtils.isEmpty(invoiceCommissions)) {
					notifyDataChange(new InvoiceDataEvent(invoice, InvoiceEventType.NEW));
				}
			}
		}
		
	}
	
	protected void processPaymentCommissions(final Payment payment, final List<InvoiceCommission> invoiceCommissions) {
		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
			PaymentCommission paymentCommission = new PaymentCommission();
			paymentCommission.setInvoiceCommission(invoiceCommission);
			paymentCommission.setPayment(payment);
			paymentCommission.setAuditColumns(payment.getUpdatedBy());
			paymentCommission.setActivationPoints(calculateActivationPoints(invoiceCommission, payment));
			paymentCommission.setPaymentAmount(calculateRevenueAmount(invoiceCommission, payment));
			
			paymentCommissionDAO.save(paymentCommission);
		}
	}
	
	/**
	 * TODO: Make sure that this is properly checking for transactions already processed.	
	 */
	@Override
	protected boolean strategyAlreadyProcessed(List<Transaction> transactions) {
		Collection<Integer> ids = Utilities.getIdsBaseTableEntities(transactions);
		return CollectionUtils.isNotEmpty(paymentCommissionDAO.findWhere("t.payment.id IN (" + Strings.implode(ids) + ")"));
	}

	private BigDecimal calculateActivationPoints(InvoiceCommission invoiceCommission, Payment payment) {
		if (invoiceCommission.getPoints().equals(BigDecimal.ZERO))
			return BigDecimal.ZERO;
		
		return calculatePaymentPercentOfInvoice(invoiceCommission, payment).multiply(invoiceCommission.getPoints());
	}

	private BigDecimal calculateRevenueAmount(InvoiceCommission invoiceCommission, Payment payment) {
		if (invoiceCommission.getRevenuePercent().equals(BigDecimal.ZERO)) {
			return BigDecimal.ZERO;
		}
		
		BigDecimal revenuePercent = calculatePaymentPercentOfInvoice(invoiceCommission, payment).multiply(invoiceCommission.getRevenuePercent());
		BigDecimal revenueAmount = invoiceCommission.getInvoice().getTotalCommissionEligibleInvoice(true).multiply(revenuePercent);
		return revenueAmount;
	}
	
	private BigDecimal calculatePaymentPercentOfInvoice(InvoiceCommission invoiceCommission, Payment payment) {
		BigDecimal totalAmount = invoiceCommission.getInvoice().getTotalAmount();
		BigDecimal amountApplied = payment.getAmountApplied();
		
		if (totalAmount != null && amountApplied != null) {
			return amountApplied.divide(totalAmount);
		}
		
		return BigDecimal.ZERO;
	}
	
	private <T> void notifyDataChange(DataEvent<T> dataEvent) {
		salesCommissionDataObservable.setChanged();
		salesCommissionDataObservable.notifyObservers(dataEvent);
	}
}
