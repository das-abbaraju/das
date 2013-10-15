package com.picsauditing.salecommission.payment.strategy;

import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.dao.PaymentCommissionDAO;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * This class contains all the logic for the processing of PaymentCommissions
 */
public abstract class AbstractPaymentCommissionStrategy implements PaymentCommissionStrategy<PaymentApplied> {

    @Autowired
    protected InvoiceCommissionDAO invoiceCommissionDAO;
    @Autowired
    protected PaymentCommissionDAO paymentCommissionDAO;

    @Override
    public final void processPaymentCommission(PaymentApplied paymentApplied) {
        if (strategyAlreadyProcessed(paymentApplied)) {
            return;
        }

        processPaymentCommissions(paymentApplied);
    }

    protected abstract boolean strategyAlreadyProcessed(final PaymentApplied paymentApplied);

    protected abstract void processPaymentCommissions(final PaymentApplied paymentApplied);

    protected BigDecimal calculateActivationPoints(InvoiceCommission invoiceCommission, Payment payment) {
        if (invoiceCommission.getPoints().equals(BigDecimal.ZERO))
            return BigDecimal.ZERO;

        return calculatePaymentPercentOfInvoice(invoiceCommission, payment).multiply(invoiceCommission.getPoints());
    }

    protected BigDecimal calculateRevenueAmount(InvoiceCommission invoiceCommission, Payment payment) {
        if (invoiceCommission.getRevenuePercent().equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        BigDecimal revenuePercent = calculatePaymentPercentOfInvoice(invoiceCommission, payment).multiply(
                invoiceCommission.getRevenuePercent());
        BigDecimal revenueAmount = invoiceCommission.getInvoice().getCommissionableAmount().multiply(revenuePercent);
        return revenueAmount;
    }

    protected BigDecimal calculatePaymentPercentOfInvoice(InvoiceCommission invoiceCommission, Payment payment) {
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