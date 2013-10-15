package com.picsauditing.salecommission.payment.strategy;

import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentCommission;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Built this strategy because the payment might have been applied to an Invoice
 * or a Refund, but based on the context when the payment was saved, we are
 * unsure.
 */
public class PaymentStrategy extends AbstractPaymentCommissionStrategy {

    /**
     * This will generate Invoice Commissions, if they do not exist
     */
    @Override
    protected final void processPaymentCommissions(PaymentApplied paymentApplied) {
        if (paymentApplied == null) {
            return;
        }
        int invoiceId = ((PaymentAppliedToInvoice) paymentApplied).getInvoice().getId();
        List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findByInvoiceId(invoiceId);

        for (InvoiceCommission invoiceCommission : invoiceCommissions) {
            PaymentCommission paymentCommission = new PaymentCommission();
            paymentCommission.setInvoiceCommission(invoiceCommission);
            paymentCommission.setPayment(paymentApplied.getPayment());
            paymentCommission.setAuditColumns(paymentApplied.getPayment().getUpdatedBy());
            paymentCommission.setActivationPoints(calculateActivationPoints(invoiceCommission, paymentApplied.getPayment()));
            paymentCommission.setPaymentAmount(calculateRevenueAmount(invoiceCommission, paymentApplied.getPayment()));

            paymentCommissionDAO.save(paymentCommission);
        }
    }

    @Override
    protected boolean strategyAlreadyProcessed(PaymentApplied paymentApplied) {
        if (paymentApplied instanceof PaymentAppliedToInvoice) {
            return CollectionUtils.isNotEmpty(paymentCommissionDAO.findByPaymentIdInvoiceId(paymentApplied.getPayment().getId(), ((PaymentAppliedToInvoice) paymentApplied).getInvoice().getId()));
        }

        return false;
    }
}
