package com.picsauditing.salecommission.payment.strategy;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.picsauditing.jpa.entities.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.dao.PaymentCommissionDAO;
import com.picsauditing.util.Strings;

/**
 * Built this strategy because the payment might have been applied to an Invoice
 * or a Refund, but based on the context when the payment was saved, we are
 * unsure.
 */
public class PaymentStrategy extends AbstractPaymentCommissionStrategy {

	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;
	@Autowired
	private PaymentCommissionDAO paymentCommissionDAO;

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

		for (PaymentAppliedToInvoice paymentForInvoice : paymentsForInvoices) {
			Invoice invoice = paymentForInvoice.getInvoice();
			List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findByInvoiceId(invoice.getId());
			processPaymentCommissions(payment, invoiceCommissions);
		}
	}

	protected void processPaymentCommissions(final Payment payment, final List<InvoiceCommission> invoiceCommissions) {
		if (CollectionUtils.isEmpty(invoiceCommissions)) {
			return;
		}

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

	@Override
	protected boolean strategyAlreadyProcessed(List<Transaction> transactions) {
		Collection<Integer> ids = Utilities.getIdsBaseTableEntities(transactions);
		return CollectionUtils.isNotEmpty(paymentCommissionDAO.findWhere("t.payment.id IN (" + Strings.implode(ids)
				+ ")"));
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

		BigDecimal revenuePercent = calculatePaymentPercentOfInvoice(invoiceCommission, payment).multiply(
				invoiceCommission.getRevenuePercent());
		BigDecimal revenueAmount = invoiceCommission.getInvoice().getCommissionableAmount().multiply(revenuePercent);
		return revenueAmount;
	}

	private BigDecimal calculatePaymentPercentOfInvoice(InvoiceCommission invoiceCommission, Payment payment) {
		BigDecimal totalAmount = invoiceCommission.getInvoice().getTotalAmount();

        for (PaymentAppliedToInvoice invoicePayment : payment.getInvoices()) {
            if (invoiceCommission.getInvoice().getId() == invoicePayment.getInvoice().getId()) {
                BigDecimal amountApplied = invoicePayment.getAmount();

                if (totalAmount != null && amountApplied != null) {
                    return BigDecimal.valueOf(amountApplied.doubleValue() / totalAmount.doubleValue());
                }
            }
        }

		return BigDecimal.ZERO;
	}

}
