package com.picsauditing.provisioning;

import com.picsauditing.jpa.entities.Account;

public interface ProductSubscriptionService {

	boolean hasEmployeeGUARD(Account account);

    boolean hasEmployeeGUARD(int accountId);

	void addEmployeeGUARD(int accountId);

	void removeEmployeeGUARD(int accountId);

	boolean isEmployeeGUARDEmployeeUser(int appUserId);
}
