package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.NoteStatus;

public class NoteStatusConverter extends EnumConverter {
	public NoteStatusConverter() {
		enumClass = NoteStatus.class;
	}
}
