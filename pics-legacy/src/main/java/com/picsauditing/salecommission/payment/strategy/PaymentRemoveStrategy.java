package com.picsauditing.salecommission.payment.strategy;

import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentCommission;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class PaymentRemoveStrategy extends AbstractPaymentCommissionStrategy {

    @Override
    protected boolean strategyAlreadyProcessed(PaymentApplied paymentApplied) {
        if (paymentApplied instanceof PaymentAppliedToInvoice) {
            return CollectionUtils.isEmpty(paymentCommissionDAO.findByPaymentIdInvoiceId(paymentApplied.getPayment().getId(), ((PaymentAppliedToInvoice) paymentApplied).getInvoice().getId()));
        }

        return false;
    }

    @Override
    protected void processPaymentCommissions(final PaymentApplied paymentApplied) {
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
            paymentCommission.setActivationPoints(calculateActivationPoints(invoiceCommission, paymentApplied.getPayment()).negate());
            paymentCommission.setPaymentAmount(calculateRevenueAmount(invoiceCommission, paymentApplied.getPayment()).negate());

            paymentCommissionDAO.save(paymentCommission);
        }
    }
}