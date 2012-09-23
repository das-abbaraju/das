package com.picsauditing.report.tables;

public class PaymentCommissionTable extends AbstractTable {

	public PaymentCommissionTable() {
		super("payment_commission");
		addFields(com.picsauditing.jpa.entities.PaymentCommission.class);
	}

	public void addJoins() {
		// PaymentTable payment = new PaymentTable(prefix + "Payment", alias +
		// ".paymentID");
		// payment.includeRequiredAndAverageColumns();
		// payment.setOverrideCategory(FieldCategory.Commission);
		// addLeftJoin(payment);
	}
}