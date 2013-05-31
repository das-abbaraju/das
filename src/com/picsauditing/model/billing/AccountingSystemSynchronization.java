package com.picsauditing.model.billing;

import java.util.Date;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.Transaction;

public class AccountingSystemSynchronization {

	public static void setToSynchronize(Transaction transaction) {
		transaction.setQbSync(true);
        transaction.setSapSync(true);
	}

	public static void setNotToSynchronize(Transaction transaction) {
		transaction.setQbSync(false);
        transaction.setSapSync(false);
	}

}
