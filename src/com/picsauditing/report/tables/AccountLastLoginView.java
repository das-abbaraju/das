package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AccountLastLoginView extends AbstractTable {

	public AccountLastLoginView() {
		super("(SELECT MAX(lastLogin) lastLogin, accountID FROM users GROUP BY accountID)");

        Field lastLogin = new Field("LastLogin","lastLogin", FieldType.DateTime);
        lastLogin.setCategory(FieldCategory.AccountInformation);
        addField(lastLogin);
	}
	
	@Override
	protected void addJoins() {

	}

}
