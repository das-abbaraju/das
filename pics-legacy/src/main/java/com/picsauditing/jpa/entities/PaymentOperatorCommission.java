package com.picsauditing.jpa.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;

@SuppressWarnings("serial")
@Entity
@Table(name="payment_operator_commission")
public class PaymentOperatorCommission extends BaseTable {

    private InvoiceOperatorCommission invoiceOperatorCommission;
    private Payment payment;
    private BigDecimal paymentAmount = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "invoiceOperatorCommissionID", nullable = false, updatable = false)
    public InvoiceOperatorCommission getInvoiceOperatorCommission() {
        return invoiceOperatorCommission;
    }

    public void setInvoiceOperatorCommission(InvoiceOperatorCommission invoiceOperatorCommission) {
        this.invoiceOperatorCommission = invoiceOperatorCommission;
    }

    @ManyToOne
    @JoinColumn(name = "paymentID", nullable = false)
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @ReportField(type = FieldType.Float, requiredPermissions = OpPerms.SalesCommission)
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
}
