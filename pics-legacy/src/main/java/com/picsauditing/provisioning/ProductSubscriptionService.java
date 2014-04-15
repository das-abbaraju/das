package com.picsauditing.provisioning;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;

public interface ProductSubscriptionService {

	public final static String CACHE_NAME = "product_subscription";

	void addEmployeeGUARD(int accountId);

	void removeEmployeeGUARD(int accountId);

	boolean isEmployeeGUARDEmployeeUser(int appUserId);

	boolean hasEmployeeGUARD(Permissions permissions);

	boolean hasEmployeeGUARD(int accountId);

	boolean hasLegacyEmployeeGUARD(ContractorAccount contractor);

}
