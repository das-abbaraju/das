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
			
			BigDecimal manHours = new BigDecimal(params.get("manHours"))
					.setScale(2);
			BigDecimal fatalities = new BigDecimal(params.get("fatalities"))
					.setScale(2);
			BigDecimal lostWorkdayCases = new BigDecimal(params
					.get("lostWorkdayCases")).setScale(2);
			BigDecimal restrictedCases = new BigDecimal(params
					.get("restrictedCases")).setScale(2);
			BigDecimal injuries = new BigDecimal(params.get("injuries"))
					.setScale(2);
			BigDecimal totalIncidents = fatalities.add(lostWorkdayCases).add(
					restrictedCases).add(injuries);

			BigDecimal result;
			try {
				result = (totalIncidents.multiply(OSHA_NORMALIZER)).divide(
						manHours, BigDecimal.ROUND_HALF_UP);
			} catch (java.lang.ArithmeticException e) {
				return "Audit.missingParameter";
			}
			return result;
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

			BigDecimal lostWorkdayCases = new BigDecimal(params
					.get("lostWorkdayCases")).setScale(7);
			BigDecimal manHours = new BigDecimal(params.get("manHours"))
					.setScale(7);

			BigDecimal result;
			try {
				result = OSHA_NORMALIZER.multiply(
						lostWorkdayCases.divide(manHours,
								BigDecimal.ROUND_HALF_UP)).setScale(2);
			} catch (java.lang.ArithmeticException e) {
				return "Audit.missingParameter";
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
	// OSHA standard normalizer. Hours in a year * 100 employees
	private static final BigDecimal OSHA_NORMALIZER = new BigDecimal(2000 * 100);

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

}
