package com.picsauditing.provisioning;

import com.picsauditing.jpa.entities.Account;

public interface ProductSubscriptionService {

	boolean hasEmployeeGUARD(final Account account);

    boolean hasEmployeeGUARD(final int accountId);

	void addEmployeeGUARD(final int accountId);

	void removeEmployeeGUARD(final int accountId);
}
