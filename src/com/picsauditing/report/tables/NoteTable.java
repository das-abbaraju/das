package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.UserAccountRole;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class NoteTable extends AbstractTable {
	public static final String User = "User";

    public NoteTable() {
		super("note");
		addPrimaryKey();
		addFields(Note.class);
	}

	protected void addJoins() {
		addOptionalKey(new ReportForeignKey(User, new UserTable(), new ReportOnClause("createdBy", "id")));
    }
}