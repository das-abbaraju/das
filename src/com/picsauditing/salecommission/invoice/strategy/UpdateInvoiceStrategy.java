package com.picsauditing.salecommission.invoice.strategy;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.jpa.entities.Invoice;

public class UpdateInvoiceStrategy implements InvoiceCommissionStrategy<Invoice> {

	@Autowired
	private InvoiceStrategy invoiceStrategy;
	@Autowired
	private InvoiceCommissionDAO invoiceCommissionDAO;
		
	@Override
	public void processInvoiceCommission(Invoice invoice) {
		invoiceCommissionDAO.deleteData("t.invoice.id = " + invoice.getId());
		invoiceStrategy.processInvoiceCommission(invoice);
	}

}