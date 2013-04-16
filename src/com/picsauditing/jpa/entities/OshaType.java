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
			OshaRateType.Fatalities, OshaRateType.Hours }),
	MSHA(OshaAudit.CAT_ID_MSHA, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Fatalities,
			OshaRateType.Hours }),
	COHS(OshaAudit.CAT_ID_COHS, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Fatalities,
			OshaRateType.Hours }),
	UK_HSE(OshaAudit.CAT_ID_UK_HSE, new OshaRateType[] { OshaRateType.IFR, OshaRateType.DOFR, OshaRateType.LTIFR, OshaRateType.Fatalities }),
	FRANCE_NRIS(OshaAudit.CAT_ID_FRANCE_NRIS, null),
	MEXICO(OshaAudit.CAT_ID_MEXICO, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, 
			OshaRateType.Fatalities, OshaRateType.Hours }),
    AUSTRALIA(OshaAudit.CAT_ID_AUSTRALIA, new OshaRateType[] { OshaRateType.LTIFR, OshaRateType.IR, OshaRateType.FR, OshaRateType.ATLR,
            OshaRateType.Fatalities, OshaRateType.Hours }),
	IRELAND(OshaAudit.CAT_ID_IRElAND, new OshaRateType[] { OshaRateType.IFR, OshaRateType.IR,
			OshaRateType.Fatalities, OshaRateType.Hours }),
	SOUTH_AFRICA(OshaAudit.CAT_ID_SOUTH_AFRICA, new OshaRateType[] { OshaRateType.IFR, OshaRateType.IR,
			OshaRateType.Fatalities, OshaRateType.Hours }),
	EMR(OshaAudit.CAT_ID_EMR, new OshaRateType[] { OshaRateType.EMR });
	

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
