package com.picsauditing.salecommission.service.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.dao.PaymentCommissionDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.PaymentCommission;
import com.picsauditing.jpa.entities.User;

public class ContractorDeactivateCommissionStrategy implements ContractorCommissionStrategy<ContractorAccount> {

	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDao;
	@Autowired
	private PaymentCommissionDAO paymentCommissionDao;
	
	/**
	 * Determine when a Contractor was "Deactivated" and if the AccountUser should keep their points
	 * or lose them (if they have paid at least one invoice, they get to keep the points) 
	 */
	@Override
	public ContractorCommissionResults calculateCommission(ContractorAccount contractor) {
		ContractorCommissionResults result = new ContractorCommissionResults();
		List<InvoiceCommission> invoiceCommissions = new ArrayList<InvoiceCommission>();
		List<PaymentCommission> paymentCommissions = new ArrayList<PaymentCommission>();
		
		if (!hasOnePaidInvoice(contractor)) {
			invoiceCommissions.addAll(zeroOutInvoiceCommission(contractor));
		}
		
		paymentCommissions.addAll(zeroOutUnpaidInvoices(contractor));
		
		result.setInvoiceCommissions(invoiceCommissions);
		result.setPaymentCommissions(paymentCommissions);
		return result;
	}
	
	private boolean hasOnePaidInvoice(ContractorAccount contractor) {
		if (CollectionUtils.isEmpty(contractor.getInvoices())) {
			return false;
		}		
		
		for (Invoice invoice : contractor.getInvoices()) {
			if (invoice.getStatus().isPaid()) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<InvoiceCommission> zeroOutInvoiceCommission(ContractorAccount contractor) {
		if (CollectionUtils.isEmpty(contractor.getInvoices())) {
			return Collections.emptyList();
		}
		
		List<InvoiceCommission> invoiceCommissions = new ArrayList<InvoiceCommission>();
		for (Invoice invoice : contractor.getInvoices()) {
			if (invoice.getStatus().isUnpaid()) {
				invoiceCommissions.addAll(invoiceCommissionDao.findByInvoiceId(invoice.getId()));
			}
		}
		
		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
			invoiceCommission.setUpdatedBy(new User(User.SYSTEM));
			invoiceCommission.setPoints(0);
			invoiceCommission.setRevenuePercent(0);
		}
		
		return invoiceCommissions;
	}
	
	private List<PaymentCommission> zeroOutUnpaidInvoices(ContractorAccount contractor) {
		if (CollectionUtils.isEmpty(contractor.getInvoices())) {
			return Collections.emptyList();
		}
		
		List<InvoiceCommission> invoiceCommissions = new ArrayList<InvoiceCommission>();
		for (Invoice invoice : contractor.getInvoices()) {
			if (invoice.getStatus().isUnpaid()) {
				invoiceCommissions.addAll(invoiceCommissionDao.findByInvoiceId(invoice.getId()));
			}
		}
		
		List<PaymentCommission> paymentCommissions = new ArrayList<PaymentCommission>();
		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
			paymentCommissions.addAll(paymentCommissionDao.findByInvoiceCommissionId(invoiceCommission.getId()));
		}
		
//		PaymentCommission.insertUpdateDeleteExplicit(unLinkedList, changes, dao)
		
		return paymentCommissions;
	}	

}
