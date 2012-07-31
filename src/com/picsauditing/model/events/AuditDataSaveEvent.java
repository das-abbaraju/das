package com.picsauditing.model.events;

import org.springframework.context.ApplicationEvent;

public class AuditDataSaveEvent extends ApplicationEvent {
	private static final long serialVersionUID = -8507700425644279053L;

	public AuditDataSaveEvent(Object source) {
		super(source);
	}

	public String toString() {
		return "AuditDataSaveEvent";
	}
}
