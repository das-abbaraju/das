package com.picsauditing.salecommission.payment.strategy;

import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class contains all the logic for the processing of PaymentCommissions
 */
public class PaymentStrategy implements PaymentCommissionStrategy<PaymentApplied> {

    protected PaymentDataEvent.PaymentEventType eventType = null;

    @Autowired
    protected InvoiceCommissionDAO invoiceCommissionDAO;

    @Override
    public void processPaymentCommission(PaymentApplied paymentApplied, PaymentDataEvent.PaymentEventType eventType) {
        this.eventType = eventType;
        processPaymentCommissions(paymentApplied);
    }

    protected void processPaymentCommissions(final PaymentApplied paymentApplied) {
        if (paymentApplied == null) {
            return;
        }

        PaymentAppliedToInvoice paymentAppliedToInvoice = (PaymentAppliedToInvoice) paymentApplied;
        int invoiceId = paymentAppliedToInvoice.getInvoice().getId();
        buildPaymentCommissions(paymentApplied, paymentAppliedToInvoice, invoiceId);
        buildPaymentOperatorCommissions(paymentApplied, paymentAppliedToInvoice, invoiceId);
    }

    protected void buildPaymentOperatorCommissions(PaymentApplied paymentApplied, PaymentAppliedToInvoice paymentAppliedToInvoice, int invoiceId) {
        List<InvoiceOperatorCommission> invoiceOperatorCommissions = invoiceCommissionDAO.findInvoiceOperatorCommissionsByInvoiceId(invoiceId);

        for (InvoiceOperatorCommission invoiceOperatorCommission : invoiceOperatorCommissions) {
            PaymentOperatorCommission paymentOperatorCommission = new PaymentOperatorCommission();
            paymentOperatorCommission.setInvoiceOperatorCommission(invoiceOperatorCommission);
            paymentOperatorCommission.setPayment(paymentApplied.getPayment());
            paymentOperatorCommission.setAuditColumns(paymentApplied.getPayment().getUpdatedBy());
            paymentOperatorCommission.setPaymentAmount(calculateRevenueAmount(invoiceOperatorCommission.getInvoice(), invoiceOperatorCommission.getRevenuePercent(), paymentAppliedToInvoice));

            invoiceCommissionDAO.save(paymentOperatorCommission);
        }
    }

    protected void buildPaymentCommissions(PaymentApplied paymentApplied, PaymentAppliedToInvoice paymentAppliedToInvoice, int invoiceId) {
        List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findInvoiceCommissionsByInvoiceId(invoiceId);

        for (InvoiceCommission invoiceCommission : invoiceCommissions) {
            PaymentCommission paymentCommission = new PaymentCommission();
            paymentCommission.setInvoiceCommission(invoiceCommission);
            paymentCommission.setPayment(paymentApplied.getPayment());
            paymentCommission.setAuditColumns(paymentApplied.getPayment().getUpdatedBy());
            paymentCommission.setActivationPoints(calculateActivationPoints(invoiceCommission, paymentAppliedToInvoice));
            paymentCommission.setPaymentAmount(calculateRevenueAmount(invoiceCommission.getInvoice(), invoiceCommission.getRevenuePercent(), paymentAppliedToInvoice));

            invoiceCommissionDAO.save(paymentCommission);
        }
    }

    private BigDecimal calculateActivationPoints(InvoiceCommission invoiceCommission, PaymentAppliedToInvoice paymentApplied) {
        if (invoiceCommission.getPoints().equals(BigDecimal.ZERO))
            return BigDecimal.ZERO;

        BigDecimal activationPoints = calculatePaymentPercentOfInvoice(invoiceCommission.getInvoice(), paymentApplied).multiply(invoiceCommission.getPoints());

        return negateIfRemovingPayment(activationPoints);
    }

    private BigDecimal calculateRevenueAmount(Invoice invoice, BigDecimal revenuePercent, PaymentAppliedToInvoice paymentApplied) {
        if (revenuePercent.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        BigDecimal paymentPercent = calculatePaymentPercentOfInvoice(invoice, paymentApplied).multiply(revenuePercent);
        BigDecimal revenueAmount = invoice.getCommissionableAmount().multiply(paymentPercent);

        return negateIfRemovingPayment(revenueAmount);
    }

    private BigDecimal calculatePaymentPercentOfInvoice(Invoice invoice, PaymentAppliedToInvoice paymentApplied) {
        BigDecimal totalAmount = invoice.getTotalAmount();

        if (invoice.getId() == paymentApplied.getInvoice().getId()) {
            BigDecimal amountApplied = paymentApplied.getAmount();

            if (totalAmount != null && amountApplied != null) {
                return BigDecimal.valueOf(amountApplied.doubleValue() / totalAmount.doubleValue());
            }
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal negateIfRemovingPayment(BigDecimal value) {
        switch (eventType) {
            case REFUND:
            case REMOVE:
                return value.negate();
        }

        return value;
    }

}