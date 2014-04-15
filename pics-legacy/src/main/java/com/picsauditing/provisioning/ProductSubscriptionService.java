package com.picsauditing.provisioning;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;

public interface ProductSubscriptionService {
	
	public final static String CACHE_NAME = "product_subscription";


	void addEmployeeGUARD(int accountId);

	void removeEmployeeGUARD(int accountId);

	boolean isEmployeeGUARDEmployeeUser(int appUserId);
	boolean hasEmployeeGuard(Permissions permissions);

}
