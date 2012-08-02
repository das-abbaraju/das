package com.picsauditing.salecommission.service.strategy;


import java.util.List;

import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentCommission;

public class PaymentRefundStrategy implements PaymentCommissionStrategy<Payment> {

	@Override
	public List<PaymentCommission> calculatePaymentCommission(Payment payment) {
		PaymentRemoveStrategy strategy = new PaymentRemoveStrategy();
		return strategy.calculatePaymentCommission(payment);
	}

}
