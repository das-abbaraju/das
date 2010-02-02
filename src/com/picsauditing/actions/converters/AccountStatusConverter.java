package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.AccountStatus;

public class AccountStatusConverter extends EnumConverter {
	public AccountStatusConverter() {
		enumClass = AccountStatus.class;
	}
}
