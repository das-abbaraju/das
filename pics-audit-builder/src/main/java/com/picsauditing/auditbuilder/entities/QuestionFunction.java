package com.picsauditing.auditbuilder.entities;

import com.picsauditing.auditbuilder.service.AuditService;
import com.picsauditing.auditbuilder.util.AnswerMap;
import com.picsauditing.auditbuilder.util.CorruptionPerceptionIndexMap;
import com.picsauditing.auditbuilder.util.Strings;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
	CUSTOM {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = input.getParams();
			Object result;
			ScriptEngineManager mgr = new ScriptEngineManager();
			Map<String, String> modifiableParams = new HashMap<String, String>(params);
			try {
				if (Strings.isEmpty(modifiableParams.get("expression"))) {
					return MISSING_PARAMETER;
				}

				String expression = modifiableParams.get("expression");
				modifiableParams.remove("expression");
				for (Map.Entry<String, String> entry : modifiableParams.entrySet()) {
					expression = expression.replaceAll("{" + entry.getKey() + "}", entry.getValue());
				}

				ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
				result = jsEngine.eval(expression);
			} catch (ScriptException ex) {
				result = ex.getMessage();
			}
			return result;
		}
	},
    SIMPLE_MATH {
        @Override
        public Object calculate(FunctionInput input) {
            return evaluateSimpleMath(input);
        }
    },
	/**
	 * Canadian WCB Surcharge
	 *
	 * Net Premium Rate / Industry Rate
	 */
	WCB_SURCHARGE {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);
			if (Strings.isEmpty(params.get("netPremiumRate")) || Strings.isEmpty(params.get("industryRate"))) {
				return MISSING_PARAMETER;
			}
			BigDecimal netPremiumRate = new BigDecimal(params.get("netPremiumRate")).setScale(3);
			BigDecimal industryRate = new BigDecimal(params.get("industryRate")).setScale(3);
			return netPremiumRate.divide(industryRate, BigDecimal.ROUND_HALF_UP);
		}
	},
	/**
	 * Annual Update TRIR
	 * Formula: TRIR = (Total Recordable Incidents * 200,000) / Total Man Hours
	 */
	TRIR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("fatalities"))
					|| Strings.isEmpty(params.get("lostWorkdayCases"))
					|| Strings.isEmpty(params.get("restrictedCases"))
					|| Strings.isEmpty(params.get("injuries"))) {
				return MISSING_PARAMETER;
			}

			int manHours = convertToInt(params.get("manHours").replace(",", ""));
			//convertToInt(params.get("manHours").replace(",", ""));
			int fatalities = convertToInt(params.get("fatalities").replace(",", ""));
			int lostWorkdayCases = convertToInt(params.get("lostWorkdayCases").replace(",", ""));
			int restrictedCases = convertToInt(params.get("restrictedCases").replace(",", ""));
			int injuries= convertToInt(params.get("injuries").replace(",", ""));
			int totalCases = fatalities + lostWorkdayCases + restrictedCases + injuries;

			return calculateRate(totalCases, manHours, OSHA_NORMALIZER);
		}
	},
	/**
	 * Annual Update LWCR
	 * LWCR  = (Total Lost Workday Cases / Total Man Hours) * 200,000
	 */
	LWCR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("lostWorkdayCases"))) {
				return MISSING_PARAMETER;
			}

			int manHours = convertToInt(params.get("manHours").replace(",", ""));
			int lostWorkdayCases = convertToInt(params.get("lostWorkdayCases").replace(",", ""));

			return calculateRate(lostWorkdayCases, manHours, OSHA_NORMALIZER);
		}
	},
	/**
	 * Annual Update RWCR
	 * RWCR  = (Number of Restricted Work Day Cases (I) / Total Man Hours) * 200,000
	 */
	RWCR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("restrictedCases"))) {
				return MISSING_PARAMETER;
			}

			int manHours = convertToInt(params.get("manHours").replace(",", ""));
			int restrictedCases = convertToInt(params.get("restrictedCases").replace(",", ""));

			return calculateRate(restrictedCases, manHours, OSHA_NORMALIZER);
		}
	},
	/**
	 * Annual Update DART
	 * DART  = (Lost Day Work Cases + Restricted Work Cases * 200,000) / Total Man Hours
	 */
	DART {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("lostWorkdayCases"))
					|| Strings.isEmpty(params.get("restrictedCases"))) {
				return MISSING_PARAMETER;
			}

			int manHours = convertToInt(params.get("manHours").replace(",", ""));
			int lostWorkdayCases = convertToInt(params.get("lostWorkdayCases").replace(",", ""));
			int restrictedCases = convertToInt(params.get("restrictedCases").replace(",", ""));

			int dartCases = lostWorkdayCases + restrictedCases;

			return calculateRate(dartCases, manHours, OSHA_NORMALIZER);
		}
	},
	/**
	 * US Annual Update Fatality Incident Rate
	 * FIR  = (Fatalities (G) / Total Man Hours) * 200,000
	 */
	FIR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("fatalities"))) {
				return MISSING_PARAMETER;
			}

			int manHours = convertToInt(params.get("manHours").replace(",", ""));
			int fatalities = convertToInt(params.get("fatalities").replace(",", ""));

			return calculateRate(fatalities, manHours, OSHA_NORMALIZER);
		}
	},
	/**
	 * US Annual Update Severity Rate
	 * Severity Rate  = (Lost Workdays (K) / Total Man Hours) * 200,000
	 */
	SEVERITY_RATE {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("lostWorkdays"))) {
				return MISSING_PARAMETER;
			}

			int manHours = convertToInt(params.get("manHours").replace(",", ""));
			int lostWorkdays = convertToInt(params.get("lostWorkdays").replace(",", ""));

			return calculateRate(lostWorkdays, manHours, OSHA_NORMALIZER);
		}
	},
	/**
	 * US Annual Update Severity Rate
	 * PICS Severity Rate  = ((Lost Workdays (K) + Restricted Days (L)) / Total Man Hours) * 200,000
	 */
	PICS_SEVERITY_RATE {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("lostWorkdays"))
					|| Strings.isEmpty(params.get("restrictedDays"))) {
				return MISSING_PARAMETER;
			}

			int manHours = convertToInt(params.get("manHours").replace(",", ""));
			int lostWorkdays = convertToInt(params.get("lostWorkdays").replace(",", ""));
			int restrictedDays = convertToInt(params.get("restrictedDays").replace(",", ""));
			int totalDays = lostWorkdays + restrictedDays;

			return calculateRate(totalDays, manHours, OSHA_NORMALIZER);
		}
	},
	/**
	 * UK Annual Update Incidence Frequency Rate
	 * IFR = ((fatalities + major injuries + non injuries) / total number of hours worked) X 1,000,000
	 * Also known as AFR
	 */
	IFR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("totalHours"))
					|| Strings.isEmpty(params.get("majorInjuries"))
					|| Strings.isEmpty(params.get("fatalities"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal totalHours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal majorInjuries = new BigDecimal(params.get("majorInjuries").replace(",", "")).setScale(7);

			BigDecimal totalIncidents = fatalities.add(majorInjuries);


			BigDecimal result;
			try {
				result = totalIncidents.divide(totalHours, 7, RoundingMode.HALF_UP).multiply(IFR_FREQUENCY_RATE_NORMALIZER).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
    IFR2 {
        @Override
        public Object calculate(FunctionInput input) {
            Map<String, String> params = getParameterMap(input);

            if (Strings.isEmpty(params.get("totalHours"))
                    || Strings.isEmpty(params.get("majorUnder"))
                    || Strings.isEmpty(params.get("majorUnder"))
                    || Strings.isEmpty(params.get("nonMajorInjuries"))
                    || Strings.isEmpty(params.get("fatalities"))) {
                return MISSING_PARAMETER;
            }

            BigDecimal totalHours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
            BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
            BigDecimal majorInjuries = new BigDecimal(params.get("majorInjuries").replace(",", "")).setScale(7);
            BigDecimal nonMajorInjuries = new BigDecimal(params.get("nonMajorInjuries").replace(",", "")).setScale(7);
            BigDecimal majorUnder = new BigDecimal(params.get("majorUnder").replace(",", "")).setScale(7);

            BigDecimal totalIncidents = fatalities.add(majorInjuries).add(majorUnder).add(nonMajorInjuries);


            BigDecimal result;
            try {
                result = totalIncidents.divide(totalHours, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(1_000_000)).setScale(2);
            } catch (ArithmeticException e) {
                return MISSING_PARAMETER;
            }

            return result;
        }
    },
    AIR {
        @Override
        public Object calculate(FunctionInput input) {
            Map<String, String> params = getParameterMap(input);

            if (Strings.isEmpty(params.get("employees"))
                    || Strings.isEmpty(params.get("majorInjuries"))
                    || Strings.isEmpty(params.get("fatalities"))) {
                return MISSING_PARAMETER;
            }

            BigDecimal employees = new BigDecimal(params.get("employees").replace(",", "")).setScale(7);
            BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
            BigDecimal majorInjuries = new BigDecimal(params.get("majorInjuries").replace(",", "")).setScale(7);

            BigDecimal totalIncidents = fatalities.add(majorInjuries);


            BigDecimal result;
            try {
                result = totalIncidents.divide(employees, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(100_000)).setScale(2);
            } catch (ArithmeticException e) {
                return MISSING_PARAMETER;
            }

            return result;
        }
    },
	/**
	 * UK Total Lost Time Injuries
	 * LTIFR = ((Total Lost Time Injuries X 1,000,000) / Total Hours Worked)
	 */
	LTIFR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("lostTimeInjuries"))
					|| Strings.isEmpty(params.get("totalHours"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal injuries = new BigDecimal(params.get("lostTimeInjuries").replace(",", "")).setScale(7);
			BigDecimal hours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);

			BigDecimal result = null;
			try {
				result = injuries.divide(hours, 7, RoundingMode.HALF_UP).multiply(IFR_FREQUENCY_RATE_NORMALIZER).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
	/**
	 * AUS Total Lost Time Injuries Frequency Rate
	 * LTIFR = ((Fatalities + Injuries) X 1,000,000) / Total Hours Worked)
	 */
	LTIFR_AUS {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("lostTimeInjuries"))
					|| Strings.isEmpty(params.get("totalHours"))
					|| Strings.isEmpty(params.get("fatalities"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal injuries = new BigDecimal(params.get("lostTimeInjuries").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal hours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);

			BigDecimal result = null;
			try {
				result = fatalities.add(injuries).divide(hours, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(1000000)).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
	/**
	 * AUS Incident Rate
	 * IR = ((Fatalities + Lost Time Cases + Non-Lost Time Cases) X 100) / Employees)
	 */
	IR_AUS {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("lostTimeInjuries"))
					|| Strings.isEmpty(params.get("employees"))
					|| Strings.isEmpty(params.get("fatalities"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal injuries = new BigDecimal(params.get("lostTimeInjuries").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal employees = new BigDecimal(params.get("employees").replace(",", "")).setScale(7);

			BigDecimal result = null;
			try {
				result = fatalities.add(injuries).divide(employees, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
	/**
	 * AUS Frequency Rate
	 * IR = ((Fatalities + Lost Time Cases + Non-Lost Time Cases) X 1000000) / hours)
	 */
	FR_AUS {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("lostTimeInjuries"))
					|| Strings.isEmpty(params.get("totalHours"))
					|| Strings.isEmpty(params.get("fatalities"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal injuries = new BigDecimal(params.get("lostTimeInjuries").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal hours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);

			BigDecimal result = null;
			try {
				result = fatalities.add(injuries).divide(hours, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(1000000)).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
	/**
	 * AUS Average Loss Time Rate
	 * ATLR = Hours Lost / (Fatalities + Lost Time Cases + Non-Lost Time Cases)
	 */
	ATLR_AUS {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("lostTimeInjuries"))
					|| Strings.isEmpty(params.get("lostHours"))
					|| Strings.isEmpty(params.get("fatalities"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal injuries = new BigDecimal(params.get("lostTimeInjuries").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal lostHours = new BigDecimal(params.get("lostHours").replace(",", "")).setScale(7);

			BigDecimal result = null;
			try {
				result = lostHours.divide(fatalities.add(injuries), 7, RoundingMode.HALF_UP).setScale(2,RoundingMode.HALF_UP);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
	/**
	 * Ireland Incident Rate
	 * IR = ((Fatalities + Lost Time Cases + Non-Lost Time Cases) X 100) / Employees)
	 */
	IR_IRELAND {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("majorOver"))
					|| Strings.isEmpty(params.get("majorUnder"))
					|| Strings.isEmpty(params.get("minor"))
					|| Strings.isEmpty(params.get("employees"))
					|| Strings.isEmpty(params.get("fatalities")))
				return MISSING_PARAMETER;

			BigDecimal majorOver = new BigDecimal(params.get("majorOver").replace(",", "")).setScale(7);
			BigDecimal majorUnder = new BigDecimal(params.get("majorUnder").replace(",", "")).setScale(7);
			BigDecimal minor = new BigDecimal(params.get("minor").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal employees = new BigDecimal(params.get("employees").replace(",", "")).setScale(7);

			BigDecimal result = null;
			try {
				result = fatalities.add(majorOver).add(majorUnder).add(minor).divide(employees, 7, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
	/**
	 * UK Annual Update Dangerous Occurrences Frequency Rate
	 * DOFR = (dangerous occurrences / total hours worked) x 100,000
	 */
	DOFR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("totalHours"))
					|| Strings.isEmpty(params.get("dangerousOccurrences"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal totalHours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
			BigDecimal dangerousOccurences = new BigDecimal(params.get("dangerousOccurrences").replace(",", "")).setScale(7);

			BigDecimal result;
			try {
				result = dangerousOccurences.divide(totalHours, 7, RoundingMode.HALF_UP).multiply(DOFR_NORMALIZER).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
	/**
	 *  UK Annual Update
	 */
	UK_INJURIES{
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("overThreeDays"))
					|| Strings.isEmpty(params.get("underThreeDays"))) {
				return MISSING_PARAMETER;
			}

			int overThreeDays = Integer.valueOf(params.get("overThreeDays").replace(",", ""));
			int underThreeDays = Integer.valueOf(params.get("underThreeDays").replace(",", ""));

			return overThreeDays + underThreeDays;
		}

	},
	/**
	 * France Annual Update
	 * AFR = ((Deaths + Lost Time Injuries) x  1,000,000 / Total Hours)
	 */
	FRANCE_AFR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("deaths"))
					|| Strings.isEmpty(params.get("lostTimeInjuries"))) {
				return MISSING_PARAMETER;
			}

			int manHours = convertToInt(params.get("manHours").replace(",", ""));
			int totalCases = convertToInt(params.get("deaths").replace(",", "")) + convertToInt(params.get("lostTimeInjuries").replace(",", ""));

			return calculateRate(totalCases, manHours, IFR_FREQUENCY_RATE_NORMALIZER);
		}
	},
	/**
	 * France Annual Update
	 * AFR = ((Lost Work Days) x  1,000 / Total Hours)
	 */
	FRANCE_LWR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("lostWorkDays"))) {
				return MISSING_PARAMETER;
			}

			int manHours = convertToInt(params.get("manHours").replace(",", ""));
			int lostWorkDays = convertToInt(params.get("lostWorkDays").replace(",", ""));

			return calculateRate(lostWorkDays, manHours, FRANCE_NORMALIZER);
		}
	},
	/**
	 * France Annual Update
	 * Frequency Index = (Lost Time Injuries) x  1,000 / Employees)
	 */
	FRANCE_FI {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("employees"))
					|| Strings.isEmpty(params.get("lostTimeInjuries"))) {
				return MISSING_PARAMETER;
			}

			int employees = convertToInt(params.get("employees").replace(",", ""));
			int lostTimeInjuries = convertToInt(params.get("lostTimeInjuries").replace(",", ""));

			return calculateRate(lostTimeInjuries, employees, FRANCE_NORMALIZER);
		}
	},
	DIIR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("totalHours"))
					|| Strings.isEmpty(params.get("majorInjuries"))
					|| Strings.isEmpty(params.get("minorInjuries"))
					|| Strings.isEmpty(params.get("fatalities"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal totalHours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal majorInjuries = new BigDecimal(params.get("majorInjuries").replace(",", "")).setScale(7);
			BigDecimal minorInjuries = new BigDecimal(params.get("minorInjuries").replace(",", "")).setScale(7);

			BigDecimal totalIncidents = fatalities.add(majorInjuries).add(minorInjuries);


			BigDecimal result;
			try {
				result = totalIncidents.divide(totalHours, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(200_000)).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}

	},
	SR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("totalHours"))
					|| Strings.isEmpty(params.get("daysRestricted"))
					|| Strings.isEmpty(params.get("daysAway"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal totalHours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
			BigDecimal daysAway = new BigDecimal(params.get("daysAway").replace(",", "")).setScale(7);
			BigDecimal daysRestricted = new BigDecimal(params.get("daysRestricted").replace(",", "")).setScale(7);

			BigDecimal totalIncidents = daysAway.add(daysRestricted);


			BigDecimal result;
			try {
				result = totalIncidents.divide(totalHours, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(200_000)).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}

	},
	WIR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("employees"))
					|| Strings.isEmpty(params.get("fatalities"))
					|| Strings.isEmpty(params.get("medicalOver"))
					|| Strings.isEmpty(params.get("medicalUnder"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal employees = new BigDecimal(params.get("employees").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal medicalOver = new BigDecimal(params.get("medicalOver").replace(",", "")).setScale(7);
			BigDecimal medicalUnder = new BigDecimal(params.get("medicalUnder").replace(",", "")).setScale(7);

			BigDecimal totalIncidents = fatalities.add(medicalOver).add(medicalUnder);


			BigDecimal result;
			try {
				result = totalIncidents.divide(employees, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(100_000)).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}

	},
	SINGAPORE_AFR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("totalHours"))
					|| Strings.isEmpty(params.get("fatalities"))
					|| Strings.isEmpty(params.get("medicalOver"))
					|| Strings.isEmpty(params.get("medicalUnder"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal totalHours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal medicalOver = new BigDecimal(params.get("medicalOver").replace(",", "")).setScale(7);
			BigDecimal medicalUnder = new BigDecimal(params.get("medicalUnder").replace(",", "")).setScale(7);

			BigDecimal totalIncidents = fatalities.add(medicalOver).add(medicalUnder);

			BigDecimal result;
			try {
				result = totalIncidents.divide(totalHours, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(1_000_000)).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}

	},
	ODI {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("employees"))
					|| Strings.isEmpty(params.get("diseases"))) {
				return MISSING_PARAMETER;
			}

			BigDecimal employees = new BigDecimal(params.get("employees").replace(",", "")).setScale(7);
			BigDecimal diseases = new BigDecimal(params.get("diseases").replace(",", "")).setScale(7);

			BigDecimal result;
			try {
				result = diseases.divide(employees, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(100_000)).setScale(2);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}

	},
	SCORE {
		@Override
		public Object calculate(FunctionInput input) {
			float score = 0f;
			float total = 0f;
			for (AuditQuestionFunctionWatcher watcher : input.getWatchers()) {
				AuditQuestion question = watcher.getQuestion();
				AuditData auditData = input.getAnswerMap().get(question.getId());
				if (auditData != null && AuditService.isScoreApplies(auditData)) {
					score += AuditService.getScoreValue(auditData);
					total += question.getScoreWeight();
				}
			}
			if (total == 0) {
				return 0;
			}
			return Math.round((score / total) * 100);
		}
	},
	OGP_REMAP {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);
			String value = params.get("value");
			if ("Yes".equals(value)) {
				return "D";
			}
			if ("No".equals(value)) {
				return "B";
			}
			if ("NA".equals(value)) {
				return "E";
			}

			return "E";
		}
	},
	/**
	 * Suncor Audit Corruption Audit.
	 *
	 */
	CPI_CHECK {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			String unparsedJsonCountries = params.get("countries");
			String actingAsAgent = params.get("actingAsAgent");

			double lowestCpi = NO_CPI_FOR_COUNTRIES_LISTED;

			if ("Yes".equals(actingAsAgent)) {
					for (Double cpi: input.getCpiMap().findCorruptionPerceptionIndices(unparsedJsonCountries)) {
						if (cpi != null && cpi < lowestCpi) {
							lowestCpi = cpi;
						}
					}

			}
			return lowestCpi;
		}
	},
    AUDIT_SCORE {
        @Override
        public Object calculate(FunctionInput input) {
            return null;
        }
    },
	LWDR {
		@Override
		public  Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);
			if (Strings.isEmpty(params.get("didWork"))
					|| Strings.isEmpty(params.get("lostWorkCases"))
					|| Strings.isEmpty(params.get("hours"))) {
				return MISSING_PARAMETER;
			}

			if ("No".equals(params.get("didWork")))
				return MISSING_PARAMETER;

			BigDecimal lostWorkCases = new BigDecimal(params.get("lostWorkCases").replace(",", "")).setScale(7);
			BigDecimal hours = new BigDecimal(params.get("hours").replace(",", "")).setScale(7);

			BigDecimal result;
			try {
				result = lostWorkCases.multiply(new BigDecimal(200000).setScale(7)).divide(hours, 7, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
    LWDR_SUMMARY {
        @Override
        public  Object calculate(FunctionInput input) {
            Map<String, String> params = getParameterMap(input);
            if (Strings.isEmpty(params.get("lostWorkCases"))
                    || Strings.isEmpty(params.get("hours"))) {
                return MISSING_PARAMETER;
            }

            BigDecimal lostWorkCases = new BigDecimal(params.get("lostWorkCases").replace(",", "")).setScale(7);
            BigDecimal hours = new BigDecimal(params.get("hours").replace(",", "")).setScale(7);

            BigDecimal result;
            try {
                result = lostWorkCases.multiply(new BigDecimal(200000).setScale(7)).divide(hours, 7, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
            } catch (ArithmeticException e) {
                return MISSING_PARAMETER;
            }

            return result;
        }
    },
	SPMVI {
		@Override
		public  Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);
			if (Strings.isEmpty(params.get("didDrive"))
					|| Strings.isEmpty(params.get("incidents"))
						|| Strings.isEmpty(params.get("miles"))) {
				return MISSING_PARAMETER;
			}

			if ("No".equals(params.get("didDrive")))
				return MISSING_PARAMETER;

			BigDecimal incidents = new BigDecimal(params.get("incidents").replace(",", "")).setScale(7);
			BigDecimal miles = new BigDecimal(params.get("miles").replace(",", "")).setScale(7);

			BigDecimal result;
			try {
				result = incidents.multiply(new BigDecimal(1000000).setScale(7)).divide(miles, 7, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
    SPMVI_SUMMARY {
        @Override
        public  Object calculate(FunctionInput input) {
            Map<String, String> params = getParameterMap(input);
            if (Strings.isEmpty(params.get("incidents"))
                    || Strings.isEmpty(params.get("miles"))) {
                return MISSING_PARAMETER;
            }

            BigDecimal incidents = new BigDecimal(params.get("incidents").replace(",", "")).setScale(7);
            BigDecimal miles = new BigDecimal(params.get("miles").replace(",", "")).setScale(7);

            BigDecimal result;
            try {
                result = incidents.multiply(new BigDecimal(1000000).setScale(7)).divide(miles, 7, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
            } catch (ArithmeticException e) {
                return MISSING_PARAMETER;
            }

            return result;
        }
    },
	QUARTERLY {
		@Override
		public  Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);
			int count = 0;
			BigDecimal sum = new BigDecimal(0);

			BigDecimal result;
			try {
				if (!isEmptyOrMissingParameter(params.get("month1"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("month1").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("month2"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("month2").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("month3"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("month3").replace(",", ""))));
				}

				if (count == 0)
					return MISSING_PARAMETER;
				else
					result = sum.divide((new BigDecimal(count)).setScale(7), 7, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
	YEARLY {
		@Override
		public  Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);
			int count = 0;
			BigDecimal sum = new BigDecimal(0);

			BigDecimal result;
			try {
				if (!isEmptyOrMissingParameter(params.get("jan"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("jan").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("feb"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("feb").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("mar"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("mar").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("apr"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("apr").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("may"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("may").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("jun"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("jun").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("jul"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("jul").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("aug"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("aug").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("sep"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("sep").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("oct"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("oct").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("nov"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("nov").replace(",", ""))));
				}
				if (!isEmptyOrMissingParameter(params.get("dec"))) {
					count++;
					sum = sum.add(new BigDecimal(Float.parseFloat(params.get("dec").replace(",", ""))));
				}

				if (count == 0)
					return MISSING_PARAMETER;
				else
					result = sum.divide(new BigDecimal(count), 7, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			} catch (ArithmeticException e) {
				return MISSING_PARAMETER;
			}

			return result;
		}
	},
    ROLLUP {
        @Override
        public  Object calculate(FunctionInput input) {
           // placeholder so a function can be designated, actual function is done in code
           return MISSING_PARAMETER;
        }
    },
    CLEAR_ON_NO {
        @Override
        public  Object calculate(FunctionInput input) {
            Map<String, String> params = getParameterMap(input);
            if (Strings.isEmpty(params.get("yesNo"))) {
                return MISSING_PARAMETER;
            }

            if ("No".equals(params.get("yesNo")))
                return "";

            return input.getCurrentAnswer();
        }
    },
    ZERO_ON_NO {
        @Override
        public  Object calculate(FunctionInput input) {
            Map<String, String> params = getParameterMap(input);
            if (Strings.isEmpty(params.get("yesNo"))) {
                return MISSING_PARAMETER;
            }

            if ("No".equals(params.get("yesNo")))
                return "0";

            return input.getCurrentAnswer();
        }
    },
	DOUBLE {
		@Override
		public  Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);
			if (params.size() == 0) {
				return MISSING_PARAMETER;
			}

			String answer = params.values().iterator().next().replace(",", "");

			BigDecimal number = new BigDecimal(answer);
			number = number.multiply(new BigDecimal(2)).setScale(0);
			return number;
		}
	}

;
	private static final int NO_CPI_FOR_COUNTRIES_LISTED = 11000;

	// US OSHA standard normalizer. Hours in a year * 100 employees
	private static final BigDecimal OSHA_NORMALIZER = new BigDecimal(2000 * 100);

	// UK HSE standard normalizer.
	private static final BigDecimal DOFR_NORMALIZER = new BigDecimal(100000);
	private static final BigDecimal IFR_FREQUENCY_RATE_NORMALIZER = new BigDecimal(1000000);

	// France NRIS standard normalizer.
	private static final BigDecimal FRANCE_NORMALIZER = new BigDecimal(1000);

	public static final String MISSING_PARAMETER = "Audit.missingParameter";

	public abstract Object calculate(FunctionInput input)  throws NumberFormatException;;

	public static class FunctionInput {
		private final Map<String, String> params;
		private final AnswerMap answerMap;
		private final Collection<AuditQuestionFunctionWatcher> watchers;
        private String currentAnswer = null;
        private String expression = null;
        private CorruptionPerceptionIndexMap cpiMap = null;

        String operatorPrecedence = "+-  */()";
        StringBuilder constant = new StringBuilder();
        StringBuilder identifier = new StringBuilder();
        boolean processingConstant = false;
        boolean processingIdentifier = false;
        Stack<BigDecimal> operandStack = new Stack<>();
        Stack<Character> operatorStack = new Stack();

        private FunctionInput(Builder builder) {
			this.params = builder.params;
			this.answerMap = builder.answerMap;
			this.watchers = builder.watchers;
            this.cpiMap = builder.cpiMap;
		}

        public String getCurrentAnswer() {
            return currentAnswer;
        }

        public void setCurrentAnswer(String currentAnswer) {
            this.currentAnswer = currentAnswer;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public CorruptionPerceptionIndexMap getCpiMap() {
            return cpiMap;
        }

        public void setCpiMap(CorruptionPerceptionIndexMap cpiMap) {
            this.cpiMap = cpiMap;
        }

        public static class Builder {
			private Map<String, String> params;
			private AnswerMap answerMap;
			private Collection<AuditQuestionFunctionWatcher> watchers = Collections.emptyList();
            private CorruptionPerceptionIndexMap cpiMap;

            public Builder params(Map<String, String> params) {
				this.params = params;
				return this;
			}

			public Builder answerMap(AnswerMap answerMap) {
				this.answerMap = answerMap;
				return this;
			}

			public Builder watchers(Collection<AuditQuestionFunctionWatcher> watchers) {
				this.watchers = Collections.unmodifiableCollection(watchers);
				return this;
			}

            public Builder cpiMap(CorruptionPerceptionIndexMap cpiMap) {
                this.cpiMap = cpiMap;
                return this;
            }

			public FunctionInput build() {
				return new FunctionInput(this);
			}
		}

		public Map<String, String> getParams() {
			return params;
		}

		public AnswerMap getAnswerMap() {
			return answerMap;
		}

		public Collection<AuditQuestionFunctionWatcher> getWatchers() {
			return watchers;
		}
	}

	protected int convertToInt(String value) {
		return Float.valueOf(value).intValue();
	}

	protected boolean isEmptyOrMissingParameter(String answer) {
		if (Strings.isEmpty(answer))
			return true;
		if (MISSING_PARAMETER.equals(answer))
			return true;
		return false;
	}

	protected Map<String, String> getParameterMap(FunctionInput input) {
		Map<String, String> params = new HashMap<>();

		for (AuditQuestionFunctionWatcher watcher : input.watchers) {
			AuditData auditData = input.answerMap.get(watcher.getQuestion().getId());
			String answer = "";
			if (auditData != null) {
				answer = auditData.getAnswer();
			}
			params.put(watcher.getUniqueCode(), answer);
		}

		return params;
	}

    protected Object evaluateSimpleMath(FunctionInput input) {
        if (Strings.isEmpty(input.getExpression()))
            return MISSING_PARAMETER;
        String answer = null;
        Map<String, String> params = getParameterMap(input);

        try {
            for (char c : input.getExpression().toCharArray()) {
                if (Character.isWhitespace(c)) {
                    processIdentifierOrConstant(input, params);
                } else if ((Character.isDigit(c) || c == '.') && input.processingIdentifier) {
                    addToIdentifier(input, c);
                } else if (Character.isDigit(c) || c == '.') {
                    addToNumber(input, c);
                } else if (Character.isLetter(c)) {
                    addToIdentifier(input, c);
                } else if (c != ' ' && input.operatorPrecedence.indexOf(c) >= 0) { // operator
                    processIdentifierOrConstant(input, params);
                    processOperator(input, c);
                } else { // end of constant or identifier
                    throw new Exception("Unknown character '" + c + "'");
                }
            }
            processIdentifierOrConstant(input, params);
            while (input.operatorStack.size() > 0) {
                evaluate(input);
            }

            if (input.operandStack.size() != 1) {
                throw new Exception("No or too many operands.");
            }

            answer = input.operandStack.pop().setScale(2, RoundingMode.HALF_UP).toString();
        } catch (Exception e) {
            return MISSING_PARAMETER;
        }

        return answer;
    }

    private void processOperator(FunctionInput input, char op) throws Exception{
        if (op == '(') {
            return;
        } else if (op == ')') {
            evaluate(input);
        } else {
            if (input.operatorStack.size() == 0 || greaterPrecedence(input, op)) {
                input.operatorStack.push(op);
            } else {
                evaluate(input);
                input.operatorStack.push(op);
            }
        }
    }

    private boolean greaterPrecedence(FunctionInput input, char op) {
        int opPrec = input.operatorPrecedence.indexOf(op);
        int stackPrec = input.operatorPrecedence.indexOf(input.operatorStack.peek().charValue());

        if (opPrec > stackPrec + 2)
            return true;

        return false;
    }

    private void evaluate(FunctionInput input) throws Exception {
        if (input.operatorStack.size() == 0) {
            throw new Exception("Evaluate called with no operators.");
        }

        if (input.operandStack.size() < 2) {
            throw new Exception("Evaluate called with not enough operands.");
        }

        BigDecimal operand2 = input.operandStack.pop();
        BigDecimal operand1 = input.operandStack.pop();

        char operator = input.operatorStack.pop().charValue();

        switch(operator) {
            case '+':
                input.operandStack.push(operand1.add(operand2));
                break;
            case '-':
                input.operandStack.push(operand1.subtract(operand2));
                break;
            case '*':
                input.operandStack.push(operand1.multiply(operand2));
                break;
            case '/':
                input.operandStack.push(operand1.divide(operand2, 7, BigDecimal.ROUND_HALF_UP));
                break;
            default:
                throw new Exception("Unknown operator '" + operator + "'.");
        }
    }

    private void addToNumber(FunctionInput input, char c) {
        input.constant.append(c);
        input.processingConstant = true;
    }

    private void addToIdentifier(FunctionInput input, char c) {
        input.identifier.append(c);
        input.processingIdentifier = true;
    }

    private void processIdentifierOrConstant(FunctionInput input, Map<String, String> params) throws Exception {
        if (input.processingIdentifier) {
            processIdentifier(input, params);
        } else if (input.processingConstant) {
            processConstant(input);
        }
    }

    private void processConstant(FunctionInput input) throws Exception {
        BigDecimal value = new BigDecimal(input.constant.toString());
        value.setScale(7, BigDecimal.ROUND_HALF_UP);

        input.operandStack.push(value);
        input.processingConstant = false;
        input.constant = new StringBuilder();
    }

    private void processIdentifier(FunctionInput input, Map<String, String> params) throws Exception {
        String var = params.get(input.identifier.toString());
        if (var == null) {
            throw new Exception("unknown identifier");
        }

        var = var.trim().replace(",", "");

        BigDecimal value = new BigDecimal(var);
        value.setScale(7, BigDecimal.ROUND_HALF_UP);

        input.operandStack.push(value);
        input.identifier = new StringBuilder();
        input.processingIdentifier = false;
    }

    protected Object calculateRate(int totalCases, int manHours, BigDecimal normalizer) {
		BigDecimal cases = new BigDecimal(totalCases).setScale(7);
		BigDecimal hours = new BigDecimal(manHours).setScale(7);

		BigDecimal result;
		try {
			result = cases.multiply(normalizer).divide(hours, 2, BigDecimal.ROUND_HALF_UP);
		} catch (ArithmeticException e) {
			return MISSING_PARAMETER;
		}

		return result;
	}
}
