package com.picsauditing.salecommission.invoice.strategy;

import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.InvoiceOperatorCommission;
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
		invoiceCommissionDAO.deleteData(InvoiceCommission.class, "t.invoice.id = " + invoice.getId());
        invoiceCommissionDAO.deleteData(InvoiceOperatorCommission.class, "t.invoice.id = " + invoice.getId());
		invoiceStrategy.processInvoiceCommission(invoice);
	}

}