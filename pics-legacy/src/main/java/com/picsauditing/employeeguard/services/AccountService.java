package com.picsauditing.employeeguard.services;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.services.models.AccountType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This is a stand-in for a "remote" service call
public class AccountService {

    @Autowired
    private AccountDAO accountDAO;

    public AccountModel getAccountById(int accountId) {
        Account account = accountDAO.find(accountId);
        return mapAccountToAccountModel(account);
    }

    public List<AccountModel> getAccountsByIds(List<Integer> accountIds) {
        List<Account> accounts = accountDAO.findByIds(accountIds);
        return  mapAccountsToAccountModels(accounts);
    }

	public static boolean isEmployeeGUARDEnabled(int accountId) {
		// FIXME lookup actual values
		return accountId == 1100 || accountId == 54578;
	}

    private List<AccountModel> mapAccountsToAccountModels(List<Account> accounts) {
        if (CollectionUtils.isEmpty(accounts)) {
            return Collections.emptyList();
        }

        List<AccountModel> accountModels = new ArrayList<>(accounts.size());
        for (Account account : accounts) {
            accountModels.add(mapAccountToAccountModel(account));
        }

        return accountModels;
    }

    private AccountModel mapAccountToAccountModel(Account account) {
        return new AccountModel.Builder().accountType(getAccountTypeForAccount(account)).id(account.getId())
                .name(account.getName()).build();
    }

    private AccountType getAccountTypeForAccount(Account account) {
        switch (account.getType()) {
            case "Admin":
                return AccountType.ADMIN_ACCOUNT;

            case "Assessment":
                return AccountType.ASSESSMENT;

            case "Contractor":
                return AccountType.CONTRACTOR;

            case "Corporate":
                return AccountType.CORPORATE;

            case "Operator":
                return AccountType.OPERATOR;

            default:
                throw new IllegalArgumentException("Invalid account type " + account.getType());
        }
    }

}
