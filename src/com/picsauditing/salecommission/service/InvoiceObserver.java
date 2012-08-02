package com.picsauditing.salecommission.service;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.salecommission.service.strategy.ActivateInvoiceCommissionStrategy;
import com.picsauditing.salecommission.service.strategy.InvoiceCommissionStrategy;
import com.picsauditing.salecommission.service.strategy.InvoiceStrategy;
import com.picsauditing.salecommission.service.strategy.VoidInvoiceCommissionStrategy;

public class InvoiceObserver implements Observer {
	
//	@Autowired(required = true)
//	private SaleCommissionService saleCommissionService;
	
	private static final Logger logger = LoggerFactory.getLogger(InvoiceObserver.class);

	@Override
	public void update(Observable o, Object arg) {
		if (!(arg instanceof InvoiceDataEvent)) {
			return;
		}
		
		InvoiceDataEvent event = (InvoiceDataEvent) arg;
		logger.info("Got invoice id = {}", event.getData().getId());
		
		InvoiceCommissionStrategy<Invoice> strategy = null;
		switch (event.getEventType()) {
			case ACTIVATION:
				strategy = new ActivateInvoiceCommissionStrategy();
				break;
				
			case REACTIVATION:
			case UPGRADE:
			case RENEW:
			case NEW:
			case ADD_LINE_ITEM:
				strategy = new InvoiceStrategy();
				break;
				
			case VOID:
				strategy = new VoidInvoiceCommissionStrategy();
				break;
			
			default:
				throw new IllegalArgumentException("Unhandled Invoice Event Type.");
		}

//		processInvoice(event.getData());
		SaleCommissionService.processInvoiceCommissionStrategy(strategy, event.getData());		
	}

}