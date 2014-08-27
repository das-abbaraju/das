package com.picsauditing.user.controller;

import com.google.gson.Gson;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.user.model.AccountType;
import com.picsauditing.user.model.UserInfo;


public class UserInfoController extends PicsRestActionSupport {

  	public String whoAmI() throws NoRightsException {

		UserInfo userInfo = whoAmI(permissions);

		jsonString = new Gson().toJson(userInfo);

		return JSON_STRING;
	}

    private UserInfo whoAmI(Permissions permissions) throws NoRightsException {

        AccountType accountType = AccountType.EMPLOYEE;
        if (permissions.isCorporate()) {
            accountType = AccountType.CORPORATE;
        }
        else if (permissions.isOperator()) {
            accountType = AccountType.OPERATOR;
        }
        else if(permissions.isContractor()){
            accountType = AccountType.CONTRACTOR;
        }

        UserInfo userInfo = new UserInfo(
                permissions.getAppUserID(),
                permissions.getUserId(),
                permissions.getAccountId(),
                permissions.getName(),
                accountType);

        return userInfo;
    }

}
