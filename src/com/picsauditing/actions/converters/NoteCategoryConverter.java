package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.NoteCategory;

public class NoteCategoryConverter extends EnumConverter {
	public NoteCategoryConverter() {
		enumClass = NoteCategory.class;
	}
}
