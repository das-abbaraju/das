package com.picsauditing.model.events;

import java.util.List;

import org.springframework.context.ApplicationEvent;

public class ContractorOperatorWaitingOnChangedEvent extends ApplicationEvent {

	public ContractorOperatorWaitingOnChangedEvent(Object source) {
		super(source);
	}

}
