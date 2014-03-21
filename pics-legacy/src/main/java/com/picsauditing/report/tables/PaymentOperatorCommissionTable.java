package com.picsauditing.report.tables;


import com.picsauditing.jpa.entities.PaymentOperatorCommission;

public class PaymentOperatorCommissionTable extends AbstractTable {

	public static final String Commission = "Commission";
	public static final String Payment = "Payment";

	public PaymentOperatorCommissionTable() {
		super("payment_operator_commission");
		addFields(PaymentOperatorCommission.class);
	}

	public void addJoins() {
		ReportForeignKey commissionKey = new ReportForeignKey(Commission, new InvoiceOperatorCommissionTable(), new ReportOnClause("invoiceOperatorCommissionID"));
        commissionKey.setMinimumImportance(FieldImportance.Average);
		addRequiredKey(commissionKey);
		
		ReportForeignKey paymentKey = new ReportForeignKey(Payment, new PaymentTable(), new ReportOnClause("paymentID"));
        paymentKey.setMinimumImportance(FieldImportance.Average);
        addRequiredKey(paymentKey);
	}
}