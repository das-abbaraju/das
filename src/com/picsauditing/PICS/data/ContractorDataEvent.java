package com.picsauditing.PICS.data;

import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorDataEvent extends DataEvent<ContractorAccount> {

	public enum ContractorEventType {
		DEACTIVATION, DELETE
	}
	
	private ContractorEventType eventType;
	
	public ContractorDataEvent(ContractorAccount data, ContractorEventType eventType) {
		super(data);
		this.eventType = eventType;
	}
	
	public ContractorEventType getContractorEventType() {
		return eventType;
	}

}
