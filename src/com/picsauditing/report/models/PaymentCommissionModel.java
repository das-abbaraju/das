package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.PaymentCommissionTable;

public class PaymentCommissionModel extends AbstractModel {
	public PaymentCommissionModel(Permissions permissions) {
		super(permissions, new PaymentCommissionTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "PaymentCommission");
		return spec;
	}
}