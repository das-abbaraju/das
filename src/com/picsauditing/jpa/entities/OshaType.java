package com.picsauditing.jpa.entities;

/**
 * @TODO We need a better name for the collective of all of the various health and safety organization types than "OSHA type" because OSHA implies US-only.
 * Possible better names: HSEType  SSRRS  (safety statistics reporting/record-keeping by regional standards) 
 * 
 * OSHA = Occupation Safety and Health Admin
 * MSHA = Mining Safety and Health Admin
 * COHS = Canada Occupational Health and Safety
 * UK_HSE = UK's Heath Safety and Environment (??? is this the official name?)
 */
public enum OshaType implements Translatable {
	OSHA(new OshaRateType[]{OshaRateType.TrirAbsolute,OshaRateType.LwcrAbsolute,OshaRateType.Fatalities,OshaRateType.Hours}),
	MSHA(new OshaRateType[]{}),
	COHS(new OshaRateType[]{}),
	UK_HSE(new OshaRateType[]{OshaRateType.IFR, OshaRateType.DOFR});
	
	public OshaRateType[] rates;
	
	private OshaType(OshaRateType[] rates) {
		this.rates = rates;
	}
	private OshaType() {
		rates = null;
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
