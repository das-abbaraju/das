package com.picsauditing.decorators;

public class SapAppPropertyDecorator extends AppPropertyDecorator{

	private static SapAppPropertyDecorator instance;

	private static SapAppPropertyDecorator factory() {
		if (instance == null) {
			instance = new SapAppPropertyDecorator();
		}
		return instance;
	}

	public static boolean isSAPBusinessUnitEnabled(int needle) {
		SapAppPropertyDecorator instance = factory();
		return instance.isInCSV(SAP_BIZ_UNITS_ENABLED,needle);
	}

	public static boolean isSAPBusinessUnitSetSyncTrueEnabled(int needle) {
		SapAppPropertyDecorator instance = factory();
		return instance.isInCSV(SAP_BIZ_UNITS_SET_SYNC_TRUE_ENABLED,needle);
	}
}
