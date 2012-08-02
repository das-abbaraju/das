package com.picsauditing.salecommission.service;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.data.ContractorDataEvent;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.salecommission.service.strategy.ContractorDeactivateCommissionStrategy;
import com.picsauditing.salecommission.service.strategy.ContractorCommissionStrategy;

public class ContractorObserver implements Observer {

//	@Autowired(required = true)
//	private SaleCommissionService saleCommissionService;
	
	private static final Logger logger = LoggerFactory.getLogger(ContractorObserver.class);

	@Override
	public void update(Observable o, Object arg) {
		if (!(arg instanceof ContractorDataEvent)) {
			return;
		}
		
		ContractorDataEvent event = (ContractorDataEvent) arg;
		logger.info("Got contractor account id = {}", event.getData().getId());
		
		ContractorCommissionStrategy<ContractorAccount> strategy = null;
		switch (event.getContractorEventType()) {
			case DEACTIVATION:
			case DELETE:
				strategy = new ContractorDeactivateCommissionStrategy();
				break;
				
			default:
				throw new IllegalArgumentException("Unhandled Contractor Event Type.");
		}

		SaleCommissionService.processContractorStrategy(strategy, event.getData());		
	}	
}