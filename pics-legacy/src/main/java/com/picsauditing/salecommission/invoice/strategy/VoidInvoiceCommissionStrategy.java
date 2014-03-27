package com.picsauditing.salecommission.invoice.strategy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.PaymentCommission;
import com.picsauditing.jpa.entities.User;

public class VoidInvoiceCommissionStrategy extends AbstractInvoiceCommissionStrategy {
	@Override
	protected boolean hasStrategyAlreadyProcessed(Invoice invoice) {
		List<InvoiceCommission> invoiceCommissionNotVoid = invoiceCommissionDAO.findInvoiceCommissionNotVoid(invoice
				.getId());
		return CollectionUtils.isEmpty(invoiceCommissionNotVoid);
	}

	@Override
	protected void buildInvoiceCommissions(Invoice invoice) {
		List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findInvoiceCommissionsByInvoiceId(invoice.getId());
		zeroOutInvoiceCommission(invoiceCommissions);
	}

	private void zeroOutInvoiceCommission(List<InvoiceCommission> invoiceCommissions) {
		if (CollectionUtils.isEmpty(invoiceCommissions)) {
			return;
		}

		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
			invoiceCommission.setUpdateDate(new Date());
			invoiceCommission.setUpdatedBy(new User(User.SYSTEM));
			invoiceCommission.setPoints(BigDecimal.ZERO);
			invoiceCommission.setRevenuePercent(BigDecimal.ZERO);
			zeroOutPaymentCommissions(invoiceCommission.getPaymentCommissions());
			invoiceCommissionDAO.save(invoiceCommission);
		}
	}

	private void zeroOutPaymentCommissions(List<PaymentCommission> paymentCommissions) {
		if (CollectionUtils.isEmpty(paymentCommissions)) {
			return;
		}

		for (PaymentCommission paymentCommission : paymentCommissions) {
			paymentCommission.setUpdateDate(new Date());
			paymentCommission.setUpdatedBy(new User(User.SYSTEM));
			paymentCommission.setActivationPoints(BigDecimal.ZERO);
			paymentCommission.setPaymentAmount(BigDecimal.ZERO);
            invoiceCommissionDAO.save(paymentCommission);
		}
	}

}