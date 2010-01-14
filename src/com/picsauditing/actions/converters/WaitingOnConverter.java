package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.WaitingOn;

public class WaitingOnConverter extends EnumConverter {

	public WaitingOnConverter() {
		enumClass = WaitingOn.class;
	}
}
