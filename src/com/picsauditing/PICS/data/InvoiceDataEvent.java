package com.picsauditing.PICS.data;


import com.picsauditing.jpa.entities.Invoice;

public class InvoiceDataEvent extends DataEvent<Invoice> {
	
	public enum InvoiceEventType {
		ACTIVATION, REACTIVATION, UPGRADE, RENEW, UNKNOWN, ADD_LINE_ITEM, VOID, NEW
	}
	
	private InvoiceEventType eventType;
	
	public InvoiceDataEvent(Invoice data, InvoiceEventType eventType) {
		super(data);
		this.eventType = eventType;
	}
	
	public InvoiceEventType getEventType() {
		return eventType;
	}
	
}
