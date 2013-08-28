package com.picsauditing.model.billing;

import com.picsauditing.decorators.SapAppPropertyDecorator;
import com.picsauditing.jpa.entities.Transaction;

public class AccountingSystemSynchronization {

	public static void setToSynchronize(Transaction transaction) {
        if (transaction.getAccount().isDemo()) return;
		transaction.setQbSync(true);
		if (SapAppPropertyDecorator.isSAPBusinessUnitSetSyncTrueEnabled(transaction.getAccount().getCountry().getBusinessUnit().getId())) {
        	transaction.setSapSync(true);
		}
	}

	public static void setNotToSynchronize(Transaction transaction) {
        if (transaction.getAccount().isDemo()) return;
		transaction.setQbSync(false);
        transaction.setSapSync(false);
	}

}
