package com.picsauditing.salecommission;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.salecommission.payment.strategy.PaymentCommissionStrategy;
import com.picsauditing.salecommission.payment.strategy.PaymentRemoveStrategy;
import com.picsauditing.salecommission.payment.strategy.PaymentStrategy;
import com.picsauditing.toggle.FeatureToggle;

public class PaymentObserver implements Observer {

	@Autowired
	private PaymentStrategy paymentStrategy;
	@Autowired
	private PaymentRemoveStrategy paymentRemoveStrategy;
	@Autowired
	private FeatureToggle featureToggle;

	private static final Logger logger = LoggerFactory.getLogger(PaymentObserver.class);

	@Override
	public void update(Observable o, Object arg) {
		if (!(arg instanceof PaymentDataEvent)) {
			return;
		}

		PaymentDataEvent event = (PaymentDataEvent) arg;
		if (event != null && !event.isFromApiForForceReload()
				&& !featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_INVOICE_COMMISSION)) {
			return;
		}

		try {
			// PaymentDataEvent event = (PaymentDataEvent) arg;
			logger.info("Got payment id = {}", event.getData().getId());

			PaymentCommissionStrategy<Payment> strategy = null;
			switch (event.getPaymentEventType()) {
			case PAYMENT:
			case SAVE:
				strategy = paymentStrategy;
				break;

			case REFUND:
			case REMOVE:
				strategy = paymentRemoveStrategy;
				break;

			default:
				throw new IllegalArgumentException("Unhandled Payment Event Type.");
			}

			strategy.processPaymentCommission(event.getData());
		} catch (Exception e) {
			logger.error("An error occured during processing in PaymentObserver", e);
		}
	}
}