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

	OSHA(OshaAudit.CAT_ID_OSHA, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Dart, OshaRateType.SeverityRate, 
			OshaRateType.Fatalities, OshaRateType.Hours, OshaRateType.EMR }),
	MSHA(OshaAudit.CAT_ID_MSHA, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Fatalities,
			OshaRateType.Hours }),
	COHS(OshaAudit.CAT_ID_COHS, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Fatalities,
			OshaRateType.Hours }),
	UK_HSE(OshaAudit.CAT_ID_UK_HSE, new OshaRateType[] { OshaRateType.IFR, OshaRateType.DOFR, OshaRateType.LTIFR, OshaRateType.Fatalities }),
	FRANCE_NRIS(OshaAudit.CAT_ID_FRANCE_NRIS, null);

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
