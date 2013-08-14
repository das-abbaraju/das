package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class AccountNotesModel extends AbstractModel {

	public AccountNotesModel(Permissions permissions) {
		super(permissions, new AccountTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec account = new ModelSpec(null, "Account");

        ModelSpec note = account.join(AccountTable.Note);
        note.join(NoteTable.User);

		return account;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
        super.getWhereClause(filters);

		String where = permissionQueryBuilder.buildWhereClause();

        if (!where.isEmpty()) {
            where += " AND ";
        }

        where += "(AccountNote.viewableBy = " + permissions.getAccountId() + " OR AccountNote.viewableBy = 1)";

		return where;
	}
}