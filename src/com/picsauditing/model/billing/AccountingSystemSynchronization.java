package com.picsauditing.model.billing;

import java.util.Date;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.Transaction;

public class AccountingSystemSynchronization {

	public static void setToSynchronize(Transaction transaction) {
		transaction.setQbSync(true);
		if (transaction.getSapLastSync() != null) {
			transaction.setSapLastSync(DateBean.getStartOfPicsTime());
		}
	}

	public static void setNotToSyncrhonize(Transaction transaction) {
		transaction.setQbSync(false);
		transaction.setSapLastSync(new Date());
	}

}
