package com.picsauditing.report.tables;


import com.picsauditing.jpa.entities.PaymentOperatorCommission;

public class PaymentOperatorCommissionTable extends AbstractTable {

	public static final String Commission = "Commission";
	public static final String Payment = "Payment";
    public static final String Recipient = "Account";

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

        addOptionalKey(new ReportForeignKey(Recipient, new AccountUserTable(), new ReportOnClause(null, null, ReportOnClause.ThirdAlias + ".opID = " +
                ReportOnClause.ToAlias + ".accountID AND " + ReportOnClause.ToAlias + ".startDate < " +
                ReportOnClause.FromAlias + ".creationDate AND " + ReportOnClause.ToAlias + ".endDate >= " + ReportOnClause.FromAlias + ".creationDate")));

    }
}