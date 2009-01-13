package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.CaoStatus;

public class CaoStatusConverter extends EnumConverter {
	public CaoStatusConverter() {
		enumClass = CaoStatus.class;
	}
}
