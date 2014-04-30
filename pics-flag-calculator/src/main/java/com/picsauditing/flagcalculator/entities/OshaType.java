package com.picsauditing.flagcalculator.entities;

/**
 * @TODO We need a better name for the collective of all of the various health and safety organization types than
 *       "OSHA type" because OSHA implies US-only. Possible better names: HSEType SSRRS (safety statistics
 *       reporting/record-keeping by regional standards)
 * 
 *       OSHA = Occupation Safety and Health Admin MSHA = Mining Safety and Health Admin COHS = Canada Occupational
 *       Health and Safety UK_HSE = UK's Heath Safety and Environment (??? is this the official name?)
 */
public enum OshaType {

	OSHA(OshaAudit.CAT_ID_OSHA, AuditQuestion.OSHA_KEPT_ID, true),
	MSHA(OshaAudit.CAT_ID_MSHA, 0, false),
	COHS(OshaAudit.CAT_ID_COHS, AuditQuestion.COHS_KEPT_ID, true),
	UK_HSE(OshaAudit.CAT_ID_UK_HSE, AuditQuestion.UK_HSE_KEPT_ID, true),
    FRANCE_NRIS(OshaAudit.CAT_ID_FRANCE_NRIS, 0, false),
	MEXICO(OshaAudit.CAT_ID_MEXICO, AuditQuestion.MEXICO_KEPT_ID, true),
    AUSTRALIA(OshaAudit.CAT_ID_AUSTRALIA, AuditQuestion.AUSTRALIA_KEPT_ID, true),
	IRELAND(OshaAudit.CAT_ID_IRElAND, AuditQuestion.IRELAND_KEPT_ID, true),
	SOUTH_AFRICA(OshaAudit.CAT_ID_SOUTH_AFRICA, AuditQuestion.SOUTH_AFRICA_KEPT_ID, true),
	SINGAPORE_MOM(OshaAudit.CAT_ID_SINGAPORE_MOM, AuditQuestion.SINGAPORE_MOM_KEPT_ID, true),
	TURKEY(OshaAudit.CAT_ID_TURKEY, AuditQuestion.TURKEY_KEPT_ID, true),
	SWITZERLAND(OshaAudit.CAT_ID_SWITZERLAND, AuditQuestion.SWITZERLAND_KEPT_ID, true),
	SPAIN(OshaAudit.CAT_ID_SPAIN, AuditQuestion.SPAIN_KEPT_ID, true),
	POLAND(OshaAudit.CAT_ID_POLAND, AuditQuestion.POLAND_KEPT_ID, true),
	AUSTRIA(OshaAudit.CAT_ID_AUSTRIA, AuditQuestion.AUSTRIA_KEPT_ID, true),
	ITALY(OshaAudit.CAT_ID_ITALY, AuditQuestion.ITALY_KEPT_ID, true),
	PORTUGAL(OshaAudit.CAT_ID_PORTUGAL, AuditQuestion.PORTUGAL_KEPT_ID, true),
	DENMARK(OshaAudit.CAT_ID_DENMARK, AuditQuestion.DENMARK_KEPT_ID, true),
	CZECH(OshaAudit.CAT_ID_CZECH, AuditQuestion.CZECH_KEPT_ID, true),
	HUNGARY(OshaAudit.CAT_ID_HUNGARY, AuditQuestion.HUNGARY_KEPT_ID, true),
	GREECE(OshaAudit.CAT_ID_GREECE, AuditQuestion.GREECE_KEPT_ID, true),
	EMR(OshaAudit.CAT_ID_EMR, AuditQuestion.EMR_KEPT_ID, true);

	public final int categoryId;
	public final int shaKeptQuestionId;
	public final boolean displayStats;

	private OshaType(int categoryId, int shaKeptQuestionId, boolean displayStats) {
		this.categoryId = categoryId;
		this.displayStats = displayStats;
		this.shaKeptQuestionId = shaKeptQuestionId;
	}
}
