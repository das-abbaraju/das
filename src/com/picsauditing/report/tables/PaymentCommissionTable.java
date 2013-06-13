package com.picsauditing.report.tables;


public class PaymentCommissionTable extends AbstractTable {

	public static final String Commission = "Commission";
	public static final String Payment = "Payment";
	
	public PaymentCommissionTable() {
		super("payment_commission");
		addFields(com.picsauditing.jpa.entities.PaymentCommission.class);
	}

	public void addJoins() {
		ReportForeignKey commissionKey = new ReportForeignKey(Commission, new InvoiceCommissionTable(), new ReportOnClause("commissionID"));
		commissionKey.setCategory(FieldCategory.Commission);
		addRequiredKey(commissionKey);
		
		ReportForeignKey paymentKey = new ReportForeignKey(Payment, new PaymentTable(), new ReportOnClause("paymentID"));
		paymentKey.setCategory(FieldCategory.Commission);
        paymentKey.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(paymentKey);
	}
}