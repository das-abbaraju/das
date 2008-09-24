package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.EmailStatus;

public class EmailStatusConverter extends EnumConverter {
	public EmailStatusConverter() {
		enumClass = EmailStatus.class;
	}
}
