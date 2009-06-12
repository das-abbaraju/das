package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.TransactionStatus;

public class TransactionStatusConverter extends EnumConverter {
	public TransactionStatusConverter() {
		enumClass = TransactionStatus.class;
	}
}
