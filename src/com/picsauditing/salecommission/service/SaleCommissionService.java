package com.picsauditing.salecommission.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


import com.picsauditing.PICS.data.DataObservable;

import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.dao.PaymentCommissionDAO;

import com.picsauditing.jpa.entities.InvoiceCommission;

import com.picsauditing.jpa.entities.PaymentCommission;
import com.picsauditing.salecommission.service.strategy.ContractorCommissionResults;
import com.picsauditing.salecommission.service.strategy.ContractorCommissionStrategy;
import com.picsauditing.salecommission.service.strategy.InvoiceCommissionStrategy;
import com.picsauditing.salecommission.service.strategy.PaymentCommissionStrategy;
import com.picsauditing.util.SpringUtils;

public class SaleCommissionService {

	@Autowired
	private DataObservable saleCommissionDataObservable;

	private static final InvoiceCommissionDAO invoiceCommissionDAO = SpringUtils.getBean("InvoiceCommissionDAO");
	private static final PaymentCommissionDAO paymentCommissionDAO = SpringUtils.getBean("PaymentCommissionDAO");

	public SaleCommissionService() { }

	public void initService() {
		saleCommissionDataObservable.addObserver(new InvoiceObserver());
		saleCommissionDataObservable.addObserver(new PaymentObserver());
		saleCommissionDataObservable.addObserver(new ContractorObserver());
	}

	public static <T> void processInvoiceCommissionStrategy(InvoiceCommissionStrategy<T> strategy, T data) {
		saveInvoiceCommissions(strategy.calculateInvoiceCommission(data));
	}
	
	public static <T> void processPaymentCommissionStrategy(PaymentCommissionStrategy<T> strategy, T data) {
		savePaymentCommissions(strategy.calculatePaymentCommission(data));
	}
	
	public static <T> void processContractorStrategy(ContractorCommissionStrategy<T> strategy, T data) {
		ContractorCommissionResults result = strategy.calculateCommission(data);
		saveInvoiceCommissions(result.getInvoiceCommissions());
		savePaymentCommissions(result.getPaymentCommissions());
	}
	
	private static void savePaymentCommissions(List<PaymentCommission> paymentCommissions) {
		for (PaymentCommission paymentCommission : paymentCommissions) {
			paymentCommissionDAO.save(paymentCommission);
		}
	}
	
	private static void saveInvoiceCommissions(List<InvoiceCommission> invoiceCommissions) {
		for (InvoiceCommission invoiceCommission : invoiceCommissions) {
			invoiceCommissionDAO.save(invoiceCommission);
		}
	}
	
}
