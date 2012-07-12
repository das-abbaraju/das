package com.picsauditing.report.tables;

public class PaymentCommissionTable extends AbstractTable {

	public PaymentCommissionTable(String parentPrefix, String parentAlias) {
		super("payment_commission", "paymentCommission", "paycom", parentAlias + ".id = paycom.commissionID");
		this.parentPrefix = parentPrefix;
		this.parentAlias = parentAlias;
	}

	public void addFields() {
		addFields(com.picsauditing.jpa.entities.PaymentCommission.class);
	}

	public void addJoins() {
		addLeftJoin(new PaymentTable(prefix + "Payment", alias + ".paymentID"));
	}
}