package com.picsauditing.salecommission.service;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent.PaymentEventType;
import com.picsauditing.dao.PaymentCommissionDAO;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentAppliedToRefund;
import com.picsauditing.jpa.entities.PaymentCommission;
import com.picsauditing.salecommission.service.strategy.PaymentCommissionStrategy;
import com.picsauditing.salecommission.service.strategy.PaymentRemoveStrategy;
import com.picsauditing.salecommission.service.strategy.PaymentStrategy;

public class PaymentObserver implements Observer {

//	@Autowired
//	private PaymentCommissionDAO paymentCommissionDAO;
//	@Autowired(required = true)
//	private SaleCommissionService saleCommissionService;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentObserver.class);
	
	@Override
	public void update(Observable o, Object arg) {
		if (!(arg instanceof PaymentDataEvent)) {
			return;
		}
		
		PaymentDataEvent event = (PaymentDataEvent) arg;
		logger.info("Got payment id = {}", event.getData().getId());
		
		PaymentCommissionStrategy<Payment> strategy = null;
		switch (event.getPaymentEventType()) {
			case PAYMENT:
				strategy = new PaymentStrategy();
				break;
				
			case VOID:
				strategy = new PaymentRemoveStrategy();
				break;
				
			case REFUND:
			case REMOVE:
				strategy = new PaymentRemoveStrategy();
				break;
				
			default:
				throw new IllegalArgumentException("Unhandled Payment Event Type.");
		}
		
		SaleCommissionService.processPaymentCommissionStrategy(strategy, event.getData());
		
//		logger.debug("Got payment: {}", arg);
//		if (!(arg instanceof PaymentDataEvent)) {
////			logger.info("ignore this event :{}", arg);
//			return;
//		}
		
		
		
//		PaymentDataEvent dataEvent = (PaymentDataEvent) arg;
//		Payment data = dataEvent.getData();
//		PaymentEventType eventType = dataEvent.getPaymentEventType();
//
//		if (eventType == PaymentEventType.REMOVE) {
//			// remove related rows
//			List<PaymentCommission> paymentCommissions = paymentCommissionDAO
//					.findByPaymentId(data.getId());
//			for (PaymentCommission pc : paymentCommissions) {
//				paymentCommissionDAO.remove(pc);
//			}
//			
//			return;
//		}

//		for (PaymentApplied pa : data.getApplied()) {
//			if (pa instanceof PaymentAppliedToInvoice) {
//				processPayment((PaymentAppliedToInvoice) pa);
//			} else if (pa instanceof PaymentAppliedToRefund) {
//				logger.info("Process refund");
//				processRefund((PaymentAppliedToRefund) pa);
//			} else {
//				logger.warn("Invalid Payment data event");
//			}
//		}
	}
}
