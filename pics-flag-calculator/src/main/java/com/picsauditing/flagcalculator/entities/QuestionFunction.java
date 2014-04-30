package com.picsauditing.flagcalculator.entities;

/**
 * The function that a specific {@link AuditQuestion} performs. This is used for calculated values.
 *
 * @author kpartridge
 *
 */

public enum QuestionFunction {

	/**
	 * Used for custom {@link AuditQuestionFunction}s. This requires an expression be placed on the params map.
	 *
	 * Uses the unique code to create a string that will be executed as javascript
	 *
	 * Ex: {totalIncidents} / {manHours}
	 */
	CUSTOM,
    SIMPLE_MATH,
	/**
	 * Canadian WCB Surcharge
	 *
	 * Net Premium Rate / Industry Rate
	 */
	WCB_SURCHARGE,
	/**
	 * Annual Update TRIR
	 * Formula: TRIR = (Total Recordable Incidents * 200,000) / Total Man Hours
	 */
	TRIR,
	/**
	 * Annual Update LWCR
	 * LWCR  = (Total Lost Workday Cases / Total Man Hours) * 200,000
	 */
	LWCR,
	/**
	 * Annual Update RWCR
	 * RWCR  = (Number of Restricted Work Day Cases (I) / Total Man Hours) * 200,000
	 */
	RWCR,
	/**
	 * Annual Update DART
	 * DART  = (Lost Day Work Cases + Restricted Work Cases * 200,000) / Total Man Hours
	 */
	DART,
	/**
	 * US Annual Update Fatality Incident Rate
	 * FIR  = (Fatalities (G) / Total Man Hours) * 200,000
	 */
	FIR,
	/**
	 * US Annual Update Severity Rate
	 * Severity Rate  = (Lost Workdays (K) / Total Man Hours) * 200,000
	 */
	SEVERITY_RATE,
	/**
	 * US Annual Update Severity Rate
	 * PICS Severity Rate  = ((Lost Workdays (K) + Restricted Days (L)) / Total Man Hours) * 200,000
	 */
	PICS_SEVERITY_RATE,
	/**
	 * UK Annual Update Incidence Frequency Rate
	 * IFR = ((fatalities + major injuries + non injuries) / total number of hours worked) X 1,000,000
	 * Also known as AFR
	 */
	IFR,
    IFR2,
    AIR,
	/**
	 * UK Total Lost Time Injuries
	 * LTIFR = ((Total Lost Time Injuries X 1,000,000) / Total Hours Worked)
	 */
	LTIFR,
	/**
	 * AUS Total Lost Time Injuries Frequency Rate
	 * LTIFR = ((Fatalities + Injuries) X 1,000,000) / Total Hours Worked)
	 */
	LTIFR_AUS,
	/**
	 * AUS Incident Rate
	 * IR = ((Fatalities + Lost Time Cases + Non-Lost Time Cases) X 100) / Employees)
	 */
	IR_AUS,
	/**
	 * AUS Frequency Rate
	 * IR = ((Fatalities + Lost Time Cases + Non-Lost Time Cases) X 1000000) / hours)
	 */
	FR_AUS,
	/**
	 * AUS Average Loss Time Rate
	 * ATLR = Hours Lost / (Fatalities + Lost Time Cases + Non-Lost Time Cases)
	 */
	ATLR_AUS,
	/**
	 * Ireland Incident Rate
	 * IR = ((Fatalities + Lost Time Cases + Non-Lost Time Cases) X 100) / Employees)
	 */
	IR_IRELAND,
	/**
	 * UK Annual Update Dangerous Occurrences Frequency Rate
	 * DOFR = (dangerous occurrences / total hours worked) x 100,000
	 */
	DOFR,
	/**
	 *  UK Annual Update
	 */
	UK_INJURIES,
	/**
	 * France Annual Update
	 * AFR = ((Deaths + Lost Time Injuries) x  1,000,000 / Total Hours)
	 */
	FRANCE_AFR,
	/**
	 * France Annual Update
	 * AFR = ((Lost Work Days) x  1,000 / Total Hours)
	 */
	FRANCE_LWR,
	/**
	 * France Annual Update
	 * Frequency Index = (Lost Time Injuries) x  1,000 / Employees)
	 */
	FRANCE_FI,
	DIIR,
	SR,
	WIR,
	SINGAPORE_AFR,
	ODI,
	SCORE,
	OGP_REMAP,
	/**
	 * Suncor Audit Corruption Audit.
	 *
	 */
	CPI_CHECK,
    AUDIT_SCORE,
	LWDR,
    LWDR_SUMMARY,
	SPMVI,
    SPMVI_SUMMARY,
	QUARTERLY,
	YEARLY,
    ROLLUP,
    CLEAR_ON_NO,
    ZERO_ON_NO,
	DOUBLE;

    public static final String MISSING_PARAMETER = "Audit.missingParameter";
}