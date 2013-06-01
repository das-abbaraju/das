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

	OSHA(OshaAudit.CAT_ID_OSHA, AuditQuestion.OSHA_KEPT_ID, true, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Dart, OshaRateType.SeverityRate,
			OshaRateType.Fatalities, OshaRateType.Hours }),
	MSHA(OshaAudit.CAT_ID_MSHA, 0, false, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Fatalities,
			OshaRateType.Hours }),
	COHS(OshaAudit.CAT_ID_COHS, AuditQuestion.COHS_KEPT_ID, true, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute, OshaRateType.Fatalities,
			OshaRateType.Hours }),
	UK_HSE(OshaAudit.CAT_ID_UK_HSE, AuditQuestion.UK_HSE_KEPT_ID, true, new OshaRateType[] { OshaRateType.IFR, OshaRateType.DOFR, OshaRateType.LTIFR, OshaRateType.Fatalities }),
	FRANCE_NRIS(OshaAudit.CAT_ID_FRANCE_NRIS, 0, false, null),
	MEXICO(OshaAudit.CAT_ID_MEXICO, AuditQuestion.MEXICO_KEPT_ID, true, new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute,
			OshaRateType.Fatalities, OshaRateType.Hours }),
    AUSTRALIA(OshaAudit.CAT_ID_AUSTRALIA, AuditQuestion.AUSTRALIA_KEPT_ID, true, new OshaRateType[] { OshaRateType.LTIFR, OshaRateType.IR, OshaRateType.FR, OshaRateType.ATLR,
            OshaRateType.Fatalities, OshaRateType.Hours }),
	IRELAND(OshaAudit.CAT_ID_IRElAND, AuditQuestion.IRELAND_KEPT_ID, true, new OshaRateType[] { OshaRateType.IFR, OshaRateType.IR,
			OshaRateType.Fatalities, OshaRateType.Hours }),
	SOUTH_AFRICA(OshaAudit.CAT_ID_SOUTH_AFRICA, AuditQuestion.SOUTH_AFRICA_KEPT_ID, true, new OshaRateType[] { OshaRateType.DIIR, OshaRateType.SR,
			OshaRateType.Fatalities, OshaRateType.Hours }),
	EMR(OshaAudit.CAT_ID_EMR, AuditQuestion.EMR_KEPT_ID, true, new OshaRateType[] { OshaRateType.EMR });
	

	public final OshaRateType[] rates;
	public final int categoryId;
	public final int shaKeptQuestionId;
	public final boolean displayStats;

	private OshaType(int categoryId, int shaKeptQuestionId, boolean displayStats, OshaRateType[] rates) {
		this.categoryId = categoryId;
		this.rates = rates;
		this.displayStats = displayStats;
		this.shaKeptQuestionId = shaKeptQuestionId;
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
