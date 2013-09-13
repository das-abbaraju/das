package com.picsauditing.model.billing;

import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.util.SapAppPropertyUtil;

public class AccountingSystemSynchronization {

	private static SapAppPropertyUtil sapAppPropertyUtil;

	public static void setToSynchronize(Transaction transaction) {
        if (transaction.getAccount().isDemo()) return;
		transaction.setQbSync(true);
		if (sapAppPropertyUtil == null) sapAppPropertyUtil = SapAppPropertyUtil.factory();
		if (sapAppPropertyUtil.isSAPBusinessUnitSetSyncTrueEnabledForObject(transaction)) {
        	transaction.setSapSync(true);
		}
	}

	public static void setNotToSynchronize(Transaction transaction) {
        if (transaction.getAccount().isDemo()) return;
		transaction.setQbSync(false);
        transaction.setSapSync(false);
	}

	public static SapAppPropertyUtil getSapAppPropertyUtil() {
		return sapAppPropertyUtil;
	}

	public static void setSapAppPropertyUtil(SapAppPropertyUtil sapAppPropertyUtil) {
		AccountingSystemSynchronization.sapAppPropertyUtil = sapAppPropertyUtil;
	}
}
