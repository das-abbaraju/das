package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.CorruptionPerceptionIndexMap;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

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
	/**
	 * Canadian WCB Surcharge
	 * 
	 * Net Premium Rate / Industry Rate
	 */
	WCB_SURCHARGE {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = input.getParams();
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
					|| Strings.isEmpty(params.get("injuries")))
				return MISSING_PARAMETER;
			
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
					|| Strings.isEmpty(params.get("lostWorkdayCases")))
				return MISSING_PARAMETER;

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
					|| Strings.isEmpty(params.get("restrictedCases")))
				return MISSING_PARAMETER;

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
					|| Strings.isEmpty(params.get("restrictedCases")))
				return MISSING_PARAMETER;
			
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
					|| Strings.isEmpty(params.get("fatalities")))
				return MISSING_PARAMETER;

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
					|| Strings.isEmpty(params.get("lostWorkdays")))
				return MISSING_PARAMETER;

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
					|| Strings.isEmpty(params.get("restrictedDays")))
				return MISSING_PARAMETER;

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
					|| Strings.isEmpty(params.get("fatalities")))
				return MISSING_PARAMETER;
			
			BigDecimal totalHours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal majorInjuries = new BigDecimal(params.get("majorInjuries").replace(",", "")).setScale(7);

			BigDecimal totalIncidents = fatalities.add(majorInjuries);
			
			BigDecimal result;
			try {
				result = totalIncidents.divide(totalHours, 7, RoundingMode.HALF_UP).multiply(IFR_FREQUENCY_RATE_NORMALIZER).setScale(2);
			} catch (java.lang.ArithmeticException e) {
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
					|| Strings.isEmpty(params.get("totalHours")))
				return MISSING_PARAMETER;
			
			BigDecimal injuries = new BigDecimal(params.get("lostTimeInjuries").replace(",", "")).setScale(7);
			BigDecimal hours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
			
			BigDecimal result = null;
			try {
				result = injuries.divide(hours, 7, RoundingMode.HALF_UP).multiply(IFR_FREQUENCY_RATE_NORMALIZER).setScale(2);
			} catch (java.lang.ArithmeticException e) {
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
					|| Strings.isEmpty(params.get("fatalities")))
				return MISSING_PARAMETER;
			
			BigDecimal injuries = new BigDecimal(params.get("lostTimeInjuries").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal hours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
			
			BigDecimal result = null;
			try {
				result = fatalities.add(injuries).divide(hours, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(1000000)).setScale(2);
			} catch (java.lang.ArithmeticException e) {
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
					|| Strings.isEmpty(params.get("fatalities")))
				return MISSING_PARAMETER;
			
			BigDecimal injuries = new BigDecimal(params.get("lostTimeInjuries").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal employees = new BigDecimal(params.get("employees").replace(",", "")).setScale(7);
			
			BigDecimal result = null;
			try {
				result = fatalities.add(injuries).divide(employees, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2);
			} catch (java.lang.ArithmeticException e) {
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
					|| Strings.isEmpty(params.get("fatalities")))
				return MISSING_PARAMETER;
			
			BigDecimal injuries = new BigDecimal(params.get("lostTimeInjuries").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal hours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
			
			BigDecimal result = null;
			try {
				result = fatalities.add(injuries).divide(hours, 7, RoundingMode.HALF_UP).multiply(new BigDecimal(1000000)).setScale(2);
			} catch (java.lang.ArithmeticException e) {
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
					|| Strings.isEmpty(params.get("fatalities")))
				return MISSING_PARAMETER;
			
			BigDecimal injuries = new BigDecimal(params.get("lostTimeInjuries").replace(",", "")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities").replace(",", "")).setScale(7);
			BigDecimal lostHours = new BigDecimal(params.get("lostHours").replace(",", "")).setScale(7);
			
			BigDecimal result = null;
			try {
				result = lostHours.divide(fatalities.add(injuries), 7, RoundingMode.HALF_UP).setScale(2,RoundingMode.HALF_UP);
			} catch (java.lang.ArithmeticException e) {
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
				result = fatalities.add(majorOver).add(majorUnder).add(minor).divide(employees, 7, RoundingMode.HALF_UP).setScale(2);
			} catch (java.lang.ArithmeticException e) {
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
					|| Strings.isEmpty(params.get("dangerousOccurrences")))
				return MISSING_PARAMETER;
			
			BigDecimal totalHours = new BigDecimal(params.get("totalHours").replace(",", "")).setScale(7);
			BigDecimal dangerousOccurences = new BigDecimal(params.get("dangerousOccurrences").replace(",", "")).setScale(7);				
					
			BigDecimal result;
			try {
				result = dangerousOccurences.divide(totalHours, 7, RoundingMode.HALF_UP).multiply(DOFR_NORMALIZER).setScale(2);
			} catch (java.lang.ArithmeticException e) {
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
					|| Strings.isEmpty(params.get("underThreeDays")))
				return MISSING_PARAMETER;
			
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
					|| Strings.isEmpty(params.get("lostTimeInjuries")))
				return MISSING_PARAMETER;

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
					|| Strings.isEmpty(params.get("lostWorkDays")))
				return MISSING_PARAMETER;

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
					|| Strings.isEmpty(params.get("lostTimeInjuries")))
				return MISSING_PARAMETER;

			int employees = convertToInt(params.get("employees").replace(",", ""));
			int lostTimeInjuries = convertToInt(params.get("lostTimeInjuries").replace(",", ""));
			
			return calculateRate(lostTimeInjuries, employees, FRANCE_NORMALIZER);	
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
				if (auditData != null && auditData.isScoreApplies()) {
					score += auditData.getScoreValue();
					total += question.getScoreWeight();
				}
			}
			if (total == 0)
				return 0;
			return Math.round((score / total) * 100);
		}
	},
	OGP_REMAP {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);
			String value = params.get("value");
			if ("Yes".equals(value))
				return "D";
			if ("No".equals(value))
				return "B";
			if ("NA".equals(value))
				return "E";

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
					CorruptionPerceptionIndexMap cpiMap = (CorruptionPerceptionIndexMap) SpringUtils.getBean("CorruptionPerceptionIndexMap");
					
					for (Double cpi: cpiMap.findCorruptionPerceptionIndices(unparsedJsonCountries)) {
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
    }
;
	// This a special value if none of the countries listed have a CPI value.
	// The CPI scale goes from 0 - 10, with 0 being most corrupt. We don't want
	// to flag a contractor if they work in countries without a defined CPI.
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

		private FunctionInput(Builder builder) {
			this.params = builder.params;
			this.answerMap = builder.answerMap;
			this.watchers = builder.watchers;
		}

		public static class Builder {
			private Map<String, String> params;
			private AnswerMap answerMap;
			private Collection<AuditQuestionFunctionWatcher> watchers = Collections.emptyList();

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
	
	protected Map<String, String> getParameterMap(FunctionInput input) {
		Map<String, String> params = new HashMap<String, String>();

		for (AuditQuestionFunctionWatcher watcher : input.watchers) {
			AuditData auditData = input.answerMap.get(watcher.getQuestion().getId());
			String answer = "";
			if (auditData != null)
				answer = auditData.getAnswer();
			params.put(watcher.getUniqueCode(), answer);
		}

		return params;
	}
	protected Object calculateRate(int totalCases, int manHours, BigDecimal normalizer) {
		BigDecimal cases = new BigDecimal(totalCases).setScale(7);
		BigDecimal hours = new BigDecimal(manHours).setScale(7);

		BigDecimal result;
		try {
			result = cases.multiply(normalizer).divide(hours, 2, BigDecimal.ROUND_HALF_UP);
		} catch (java.lang.ArithmeticException e) {
			return MISSING_PARAMETER;
		}

		return result;
	}
}
