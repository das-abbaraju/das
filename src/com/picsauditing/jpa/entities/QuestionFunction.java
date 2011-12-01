package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;



import com.picsauditing.util.AnswerMap;
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
					return "Audit.missingParameter";
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
				return "Audit.missingParameter";
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
				return "Audit.missingParameter";
			
			int manHours = Integer.parseInt(params.get("manHours"));
			int fatalities = Integer.parseInt(params.get("fatalities"));
			int lostWorkdayCases = Integer.parseInt(params.get("lostWorkdayCases"));
			int restrictedCases = Integer.parseInt(params.get("restrictedCases"));
			int injuries= Integer.parseInt(params.get("injuries"));
			int totalCases = fatalities + lostWorkdayCases + restrictedCases + injuries;
			
			return calculateOSHARate(totalCases, manHours);
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
				return "Audit.missingParameter";

			int manHours = Integer.parseInt(params.get("manHours"));
			int lostWorkdayCases = Integer.parseInt(params.get("lostWorkdayCases"));
			
			return calculateOSHARate(lostWorkdayCases, manHours);
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
				return "Audit.missingParameter";

			int manHours = Integer.parseInt(params.get("manHours"));
			int restrictedCases = Integer.parseInt(params.get("restrictedCases"));
			
			return calculateOSHARate(restrictedCases, manHours);	
		}
	},
	/**
	 * Annual Update DART
	 * LWCR  = (Lost Day Work Cases + Restricted Work Cases * 200,000) / Total Man Hours
	 */
	DART {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("lostWorkdayCases"))
					|| Strings.isEmpty(params.get("restrictedCases")))
				return "Audit.missingParameter";
			
			int manHours = Integer.parseInt(params.get("manHours"));			
			int lostWorkdayCases = Integer.parseInt(params.get("lostWorkdayCases"));
			int restrictedCases = Integer.parseInt(params.get("restrictedCases"));
			
			int totalCases = lostWorkdayCases + restrictedCases;
			
			return calculateOSHARate(totalCases, manHours);
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
				return "Audit.missingParameter";

			int manHours = Integer.parseInt(params.get("manHours"));
			int fatalities = Integer.parseInt(params.get("fatalities"));
			
			return calculateOSHARate(fatalities, manHours);	
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
				return "Audit.missingParameter";

			int manHours = Integer.parseInt(params.get("manHours"));
			int lostWorkdays = Integer.parseInt(params.get("lostWorkdays"));
			
			return calculateOSHARate(lostWorkdays, manHours);	
		}
	},
	/**
	 * US Annual Update Severity Rate
	 * PICS Severity Rate  = (Lost Workdays (K) + Restricted Days (L) / Total Man Hours) * 200,000
	 */
	PICS_SEVERITY_RATE {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("manHours"))
					|| Strings.isEmpty(params.get("lostWorkdays"))
					|| Strings.isEmpty(params.get("restrictedDays")))
				return "Audit.missingParameter";

			int manHours = Integer.parseInt(params.get("manHours"));
			int lostWorkdays = Integer.parseInt(params.get("lostWorkdays"));
			int restrictedDays = Integer.parseInt(params.get("restrictedDays"));
			int totalDays = lostWorkdays + restrictedDays;
			
			return calculateOSHARate(totalDays, manHours);	
		}
	},
	/**
	 * UK Annual Update Incidence Frequency Rate
	 * IFR = (fatalities + major injuries + non injuries / number of employees) X 100,000
	 */
	IFR {
		@Override
		public Object calculate(FunctionInput input) {
			Map<String, String> params = getParameterMap(input);

			if (Strings.isEmpty(params.get("employees"))
					|| Strings.isEmpty(params.get("nonMajorInjuries"))
					|| Strings.isEmpty(params.get("majorInjuries"))
					|| Strings.isEmpty(params.get("fatalities")))
				return "Audit.missingParameter";
			
			BigDecimal employees = new BigDecimal(params.get("employees")).setScale(7);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities")).setScale(7);
			BigDecimal majorInjuries = new BigDecimal(params.get("majorInjuries")).setScale(7);
			BigDecimal nonMajorInjuries = new BigDecimal(params.get("nonMajorInjuries")).setScale(7);
					
			BigDecimal totalIncidents = fatalities.add(majorInjuries.add(nonMajorInjuries));
			
			BigDecimal result;
			try {
				result = totalIncidents.divide(employees).multiply(UK_NORMALIZER).setScale(2);
			} catch (java.lang.ArithmeticException e) {
				return "Audit.missingParameter";
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
				return "Audit.missingParameter";
			
			BigDecimal totalHours = new BigDecimal(params.get("totalHours")).setScale(7);
			BigDecimal dangerousOccurences = new BigDecimal(params.get("dangerousOccurrences")).setScale(7);				
					
			BigDecimal result;
			try {
				result = dangerousOccurences.divide(totalHours).multiply(UK_NORMALIZER).setScale(2);
			} catch (java.lang.ArithmeticException e) {
				return "Audit.missingParameter";
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
				return "Audit.missingParameter";
			
			int overThreeDays = Integer.valueOf(params.get("overThreeDays"));
			int underThreeDays = Integer.valueOf(params.get("underThreeDays"));
			
			return overThreeDays + underThreeDays;
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
	};
	// US OSHA standard normalizer. Hours in a year * 100 employees
	private static final BigDecimal OSHA_NORMALIZER = new BigDecimal(2000 * 100);
	
	// UK HSE standard normalizer.
	private static final BigDecimal UK_NORMALIZER = new BigDecimal(100000);

	public abstract Object calculate(FunctionInput input);

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
	protected Object calculateOSHARate(int totalCases, int manHours) {
		
		BigDecimal cases = new BigDecimal(totalCases).setScale(7);
		BigDecimal hours = new BigDecimal(manHours).setScale(7);

		BigDecimal result;
		try {
			result = OSHA_NORMALIZER.multiply(
					cases.divide(hours,
							BigDecimal.ROUND_HALF_UP)).setScale(2);
		} catch (java.lang.ArithmeticException e) {
			return "Audit.missingParameter";
		}

		return result;
	}
}
