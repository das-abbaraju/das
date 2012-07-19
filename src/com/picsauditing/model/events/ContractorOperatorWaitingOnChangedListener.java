package com.picsauditing.model.events;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.WaitingOn;

public class ContractorOperatorWaitingOnChangedListener implements ApplicationListener<ContractorOperatorWaitingOnChangedEvent> {
	private final Logger logger = LoggerFactory.getLogger(ContractorOperatorWaitingOnChangedListener.class);
	
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	
	/*
	 * The business logic behind this is:
	 * 		When gc.waitingOn is set to WaitingOn.Contractor, the clock starts.
	 * 		When gc.waitingOn is set to <> WaitingOn.Contractor, the clock stops.
	 * It doesn't matter what the previous value was. Since this is a change event, we know
	 * that it changed to WaitingOn.Contractor or to something not WaitingOn.Contractor and
	 * in either case, we need to update the lastStepToGreenDate.
	 * 
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ContractorOperatorWaitingOnChangedEvent event) {
		Object source = event.getSource();
		if (source instanceof ContractorOperator) {
			ContractorOperator co = (ContractorOperator)source;
			WaitingOn waitingOn = co.getWaitingOn();
			if (waitingOn == WaitingOn.Contractor) {
				co.setLastStepToGreenDate(new Date());
			} else {
				co.setLastStepToGreenDate(null);
			}
			contractorOperatorDAO.save(co);
		}
	}

}
