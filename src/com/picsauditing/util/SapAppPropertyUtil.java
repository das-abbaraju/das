package com.picsauditing.util;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Transaction;

public class SapAppPropertyUtil extends AppPropertyUtil {
	protected static SapAppPropertyUtil instance;

	private SapAppPropertyUtil() {
		super();
	}

	public static SapAppPropertyUtil factory() {
		if (instance == null) {
			instance = new SapAppPropertyUtil();
		}
		return instance;
	}

	public boolean isSAPBusinessUnitEnabled(int needle) {
		return isInCSV(SAP_BIZ_UNITS_ENABLED, needle);
	}

	public boolean isSAPBusinessUnitEnabledForObject(Account account) {
		CountryUtil.hydrateAccountBusinessUnitIfNecessary(account);
		return isSAPBusinessUnitEnabled(account.getCountry().getBusinessUnit().getId());
	}

	public boolean isSAPBusinessUnitEnabledForObject(Transaction transaction) {
		return isSAPBusinessUnitEnabledForObject(transaction.getAccount());
	}

	public boolean isSAPBusinessUnitSetSyncTrueEnabled(int needle) {
		return isInCSV(SAP_BIZ_UNITS_SET_SYNC_TRUE_ENABLED, needle);
	}

	public boolean isSAPBusinessUnitSetSyncTrueEnabledForObject(Account account) {
		CountryUtil.hydrateAccountBusinessUnitIfNecessary(account);
		return isSAPBusinessUnitSetSyncTrueEnabled(account.getCountry().getBusinessUnit().getId());
	}

	public boolean isSAPBusinessUnitSetSyncTrueEnabledForObject(Transaction transaction) {
		return isSAPBusinessUnitSetSyncTrueEnabledForObject(transaction.getAccount());
	}
}
