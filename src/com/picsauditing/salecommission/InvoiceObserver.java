package com.picsauditing.salecommission;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.salecommission.strategy.invoice.ActivateInvoiceCommissionStrategy;
import com.picsauditing.salecommission.strategy.invoice.InvoiceCommissionStrategy;
import com.picsauditing.salecommission.strategy.invoice.InvoiceStrategy;
import com.picsauditing.salecommission.strategy.invoice.VoidInvoiceCommissionStrategy;
import com.picsauditing.toggle.FeatureToggle;

public class InvoiceObserver implements Observer {

	@Autowired
	private InvoiceStrategy invoiceStrategy;
	@Autowired
	private VoidInvoiceCommissionStrategy voidInvoiceCommissionStrategy;
	@Autowired
	private FeatureToggle featureToggle;
	
	private static final Logger logger = LoggerFactory.getLogger(InvoiceObserver.class);

	@Override
	public void update(Observable o, Object arg) {
		if (!(arg instanceof InvoiceDataEvent) || !featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_INVOICE_COMMISSION)) {
			return;
		}
		
		InvoiceDataEvent event = (InvoiceDataEvent) arg;
		logger.info("Got invoice id = {}", event.getData().getId());
		
		InvoiceCommissionStrategy<Invoice> strategy = null;
		switch (event.getEventType()) {
			case ACTIVATION:
			case REACTIVATION:
			case UPGRADE:
			case RENEW:
			case NEW:
			case ADD_LINE_ITEM:
				strategy = invoiceStrategy;
				break;
				
			case VOID:
				strategy = voidInvoiceCommissionStrategy;
				break;
			
			default:
				throw new IllegalArgumentException("Unhandled Invoice Event Type.");
		}

		strategy.processInvoiceCommission(event.getData());
	}

}