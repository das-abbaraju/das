package com.picsauditing.rules;

import java.util.HashMap;

public class RulesRow {
	private HashMap<String, RulesQuestion> questions = new HashMap<String, RulesQuestion>();
	private Object value;
	
	public HashMap<String, RulesQuestion> getQuestions() {
		return questions;
	}
	public void addQuestion(String column, RulesQuestion question) {
		this.questions.put(column, question);
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

	public boolean equals(HashMap<String, Object> parameters) {
		for(String key : questions.keySet()) {
			// if any question in this row returns a false, then fail 
			if (!questions.get(key).equals(parameters.get(key)))
				return false;
		}
		return true;
	}
}
