package com.picsauditing.provisioning;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;

public interface ProductSubscriptionService {

	public final static String CACHE_NAME = "product_subscription";

	boolean hasEmployeeGUARD(Account account);

	boolean hasEmployeeGUARD(int accountId);

	void addEmployeeGUARD(int accountId);

	void removeEmployeeGUARD(int accountId);

	boolean isEmployeeGUARDEmployeeUser(int appUserId);

	boolean hasEmployeeGuardLegacy(Permissions permissions);

	boolean hasLegacyEmployeeGUARD(ContractorAccount contractor);

	void employeeGuardAcquiredLegacy(int accountId);

	void employeeGuardRemovedLegacy(int accountId);
}
