package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.UserModel;
import com.picsauditing.employeeguard.models.AccountType;

public class UserModelFactory {

	public UserModel create(final int userId,
	                        final int accountId,
	                        final String name,
	                        final AccountType accountType) {

		UserModel userModel = new UserModel();

		userModel.setUserId(userId);
		userModel.setAccountId(accountId);
		userModel.setName(name);
		userModel.setType(accountType);

		return userModel;
	}

}
