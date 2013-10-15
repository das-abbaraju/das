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
        processPaymentCommissions(paymentApplied);
    }

    protected abstract void processPaymentCommissions(final PaymentApplied paymentApplied);

    protected BigDecimal calculateActivationPoints(InvoiceCommission invoiceCommission, PaymentAppliedToInvoice paymentApplied) {
        if (invoiceCommission.getPoints().equals(BigDecimal.ZERO))
            return BigDecimal.ZERO;

        return calculatePaymentPercentOfInvoice(invoiceCommission, paymentApplied).multiply(invoiceCommission.getPoints());
    }

    protected BigDecimal calculateRevenueAmount(InvoiceCommission invoiceCommission, PaymentAppliedToInvoice paymentApplied) {
        if (invoiceCommission.getRevenuePercent().equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        BigDecimal revenuePercent = calculatePaymentPercentOfInvoice(invoiceCommission, paymentApplied).multiply(
                invoiceCommission.getRevenuePercent());
        BigDecimal revenueAmount = invoiceCommission.getInvoice().getCommissionableAmount().multiply(revenuePercent);
        return revenueAmount;
    }

    protected BigDecimal calculatePaymentPercentOfInvoice(InvoiceCommission invoiceCommission, PaymentAppliedToInvoice paymentApplied) {
        BigDecimal totalAmount = invoiceCommission.getInvoice().getTotalAmount();

        if (invoiceCommission.getInvoice().getId() == paymentApplied.getInvoice().getId()) {
            BigDecimal amountApplied = paymentApplied.getAmount();

            if (totalAmount != null && amountApplied != null) {
                return BigDecimal.valueOf(amountApplied.doubleValue() / totalAmount.doubleValue());
            }
        }

        return BigDecimal.ZERO;
    }

}