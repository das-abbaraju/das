package com.picsauditing.salecommission;

import java.util.*;

import com.picsauditing.salecommission.payment.strategy.PaymentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.toggle.FeatureToggle;

public class PaymentObserver implements Observer {

	@Autowired
	private PaymentStrategy paymentStrategy;
	@Autowired
	private FeatureToggle featureToggle;

    private static final Set<PaymentDataEvent.PaymentEventType> PAYMENT_EVENT_TYPES = new HashSet<>(Arrays.asList(PaymentDataEvent.PaymentEventType.values()));

	private static final Logger logger = LoggerFactory.getLogger(PaymentObserver.class);

	@Override
	public void update(Observable o, Object arg) {
		if (!(arg instanceof PaymentDataEvent)
				|| !featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_INVOICE_COMMISSION)) {
			return;
		}

        try {
			PaymentDataEvent event = (PaymentDataEvent) arg;
			logger.info("Got payment id = {}", event.getData().getId());

            if (!PAYMENT_EVENT_TYPES.contains(event.getPaymentEventType())){
				throw new IllegalArgumentException("Unhandled Payment Event Type.");
			}

            paymentStrategy.processPaymentCommission(event.getData(), event.getPaymentEventType());
		} catch (Exception e) {
			logger.error("An error occured during processing in PaymentObserver", e);
		}
	}
}