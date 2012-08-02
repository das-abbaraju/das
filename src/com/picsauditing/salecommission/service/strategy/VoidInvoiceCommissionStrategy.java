package com.picsauditing.salecommission.service.strategy;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.dao.PaymentCommissionDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.PaymentCommission;
import com.picsauditing.jpa.entities.User;

public class VoidInvoiceCommissionStrategy extends AbstractInvoiceCommissionStrategy {

	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;
	@Autowired
	private PaymentCommissionDAO paymentCommissionDAO;
	
	@Override
	public List<InvoiceCommission> calculateInvoiceCommission(Invoice invoice) {
		List<InvoiceCommission> invoiceCommissions = invoiceCommissionDAO.findByInvoiceId(invoice.getId());
		return zeroOutInvoiceCommission(invoiceCommissions);
	}
	
	private List<InvoiceCommission> zeroOutInvoiceCommission(List<InvoiceCommission> invoiceCommissions) {
		if (CollectionUtils.isEmpty(invoiceCommissions)) {
			return Collections.emptyList();
		}
				
		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
			invoiceCommission.setUpdateDate(new Date());
			invoiceCommission.setUpdatedBy(new User(User.SYSTEM));
			invoiceCommission.setPoints(0);
			invoiceCommission.setRevenuePercent(0);
			zeroOutPaymentCommissions(invoiceCommission.getPaymentCommissions());
		}
		
		return invoiceCommissions;
	}
	
	/**
	 * TODO: Determine if this is needed, or remove if it is not necessary.
	 * 
	 * @param paymentCommissions
	 */
	private void zeroOutPaymentCommissions(List<PaymentCommission> paymentCommissions) {
		if (CollectionUtils.isEmpty(paymentCommissions)) {
			return;
		}
		
		for (PaymentCommission paymentCommission : paymentCommissions) {
			paymentCommission.setUpdateDate(new Date());
			paymentCommission.setUpdatedBy(new User(User.SYSTEM));
			paymentCommission.setActivationPoints(0);
			paymentCommission.setPaymentAmount(BigDecimal.ZERO);
			paymentCommissionDAO.save(paymentCommission);
		}
	}

}
