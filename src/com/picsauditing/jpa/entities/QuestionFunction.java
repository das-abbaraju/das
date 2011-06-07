package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jboss.util.Strings;

public enum QuestionFunction {
	Custom {
		@Override
		public Object calculate(Map<String, String> params) {
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
	WCBSurcharge {
		@Override
		public Object calculate(Map<String, String> params) {
			if (Strings.isEmpty(params.get("netPremiumRate"))
					|| Strings.isEmpty(params.get("industryRate"))) {
				return "Audit.missingParameter";
			}
			BigDecimal netPremiumRate = new BigDecimal(params.get("netPremiumRate")).setScale(3);
			BigDecimal industryRate = new BigDecimal(params.get("industryRate")).setScale(3);
			return netPremiumRate.divide(industryRate, BigDecimal.ROUND_HALF_UP);
		}
	},
	TRIR {
		@Override
		public Object calculate(Map<String, String> params) {
			return "global.MissingTranslation";
		}
	};

	public abstract Object calculate(Map<String, String> params);
}
