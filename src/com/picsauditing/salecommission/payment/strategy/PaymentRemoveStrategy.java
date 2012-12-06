package com.picsauditing.salecommission.payment.strategy;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentCommission;
import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.util.Strings;

public class PaymentRemoveStrategy extends AbstractPaymentCommissionStrategy {
	
	@Override
	protected boolean strategyAlreadyProcessed(List<Transaction> transactions) {
		if (CollectionUtils.isEmpty(transactions)) {
			return true;
		}
		
		Collection<Integer> ids = Utilities.getIdsBaseTableEntities(transactions);		
		return CollectionUtils.isEmpty(paymentCommissionDAO.findWhere("t.invoiceCommission.invoice.id IN (" + Strings.implode(ids) + ")"));
	}

	@Override
	protected void processPaymentCommissions(final Payment payment) {
		List<PaymentCommission> paymentCommissions = paymentCommissionDAO.findByPaymentId(payment.getId());
		if (CollectionUtils.isEmpty(paymentCommissions)) {
			return;
		}
		
		paymentCommissionDAO.deleteData("t.id IN (" + Strings.implode(Utilities.getIdsBaseTableEntities(paymentCommissions)) + ")");
	}
}