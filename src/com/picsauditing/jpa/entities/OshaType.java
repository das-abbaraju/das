package com.picsauditing.jpa.entities;

/**
 * @TODO We need a better name for the collective of all of the various health and safety organization types than
 *       "OSHA type" because OSHA implies US-only. Possible better names: HSEType SSRRS (safety statistics
 *       reporting/record-keeping by regional standards)
 * 
 *       OSHA = Occupation Safety and Health Admin MSHA = Mining Safety and Health Admin COHS = Canada Occupational
 *       Health and Safety UK_HSE = UK's Heath Safety and Environment (??? is this the official name?)
 */
public enum OshaType implements Translatable {

	OSHA(2033, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Dart,
			OshaRateType.Fatalities, OshaRateType.Hours, OshaRateType.EMR }),
	MSHA(2256, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Fatalities,
			OshaRateType.Hours }),
	COHS(2086, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Fatalities,
			OshaRateType.Hours }),
	UK_HSE(2092, new OshaRateType[] { OshaRateType.IFR, OshaRateType.DOFR }),
	FRANCE_NRIS(1691, null);

	public static final int CAT_ID_OSHA = 2033; // U.S.
	public static final int CAT_ID_COHS = 2086; // Canada
	public static final int CAT_ID_UK_HSE = 2092; // U.K.
	public static final int CAT_ID_FRANCE_NRIS = 1691; // France

	public final OshaRateType[] rates;
	public final int categoryId;

	private OshaType(int categoryId, OshaRateType[] rates) {
		this.categoryId = categoryId;
		this.rates = rates;
	}

	@Override
	public String getI18nKey() {
		return this.toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
