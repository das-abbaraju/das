package com.picsauditing.salecommission;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.salecommission.invoice.strategy.InvoiceCommissionStrategy;
import com.picsauditing.salecommission.invoice.strategy.InvoiceStrategy;
import com.picsauditing.salecommission.invoice.strategy.UpdateInvoiceStrategy;
import com.picsauditing.salecommission.invoice.strategy.VoidInvoiceCommissionStrategy;
import com.picsauditing.toggle.FeatureToggle;

public class InvoiceObserver implements Observer {

	@Autowired
	private InvoiceStrategy invoiceStrategy;
	@Autowired
	private VoidInvoiceCommissionStrategy voidInvoiceCommissionStrategy;
	@Autowired
	private UpdateInvoiceStrategy updateInvoiceStrategy;
	@Autowired
	private FeatureToggle featureToggle;

	private static final Logger logger = LoggerFactory.getLogger(InvoiceObserver.class);

	@Override
	public void update(Observable o, Object arg) {
		if (!(arg instanceof InvoiceDataEvent)
				|| !featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_INVOICE_COMMISSION)) {
			return;
		}

		try {
			InvoiceDataEvent event = (InvoiceDataEvent) arg;
			logger.info("Got invoice id = {}", event.getData().getId());

			InvoiceCommissionStrategy<Invoice> strategy = null;
			switch (event.getEventType()) {
			case NEW:
				strategy = invoiceStrategy;
				break;

			case VOID:
				strategy = voidInvoiceCommissionStrategy;
				break;

			case UPDATE:
				strategy = updateInvoiceStrategy;
				break;

			default:
				throw new IllegalArgumentException("Unhandled Invoice Event Type.");
			}

			strategy.processInvoiceCommission(event.getData());

		} catch (Exception e) {
			logger.error("An error occured during processing in InvoiceObserver", e);
		}
	}

}