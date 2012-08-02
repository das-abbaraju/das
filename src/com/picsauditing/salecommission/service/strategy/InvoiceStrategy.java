package com.picsauditing.salecommission.service.strategy;

import java.util.Collections;
import java.util.List;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;

public class InvoiceStrategy extends AbstractInvoiceCommissionStrategy {

	@Override
	public List<InvoiceCommission> calculateInvoiceCommission(Invoice invoice) {
		
		
		return Collections.emptyList();
	}

}
