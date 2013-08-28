package com.picsauditing.util;

public class SapAppPropertyUtil extends AppPropertyUtil {

	protected static SapAppPropertyUtil instance;


	public static SapAppPropertyUtil factory() {
		if (instance == null) {
			instance = new SapAppPropertyUtil();
		}
		return instance;
	}

	public boolean isSAPBusinessUnitEnabled(int needle) {
		SapAppPropertyUtil instance = factory();
		return instance.isInCSV(SAP_BIZ_UNITS_ENABLED,needle);
	}

	public boolean isSAPBusinessUnitSetSyncTrueEnabled(int needle) {
		SapAppPropertyUtil instance = factory();
		return instance.isInCSV(SAP_BIZ_UNITS_SET_SYNC_TRUE_ENABLED,needle);
	}
}
