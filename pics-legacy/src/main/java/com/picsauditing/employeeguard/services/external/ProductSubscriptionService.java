package com.picsauditing.employeeguard.services.external;

import com.picsauditing.jpa.entities.Account;

public interface ProductSubscriptionService {

	boolean hasEmployeeGUARD(final Account account);

	void addEmployeeGUARD(final int accountId);

	void removeEmployeeGUARD(final int accountId);
}
