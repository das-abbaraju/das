package com.picsauditing.rules;

import java.util.HashMap;

public class ResultSetRow {
	private HashMap<String, ResultSetQuestion> questions = new HashMap<String, ResultSetQuestion>();
	private Object value;
	
	public HashMap<String, ResultSetQuestion> getQuestions() {
		return questions;
	}
	public void addQuestion(String column, ResultSetQuestion question) {
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
