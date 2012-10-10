package com.picsauditing.search;

import java.util.ArrayList;
import java.util.List;

public class SelectCase {
	private String caseValue = null;
	private List<Condition> conditions = new ArrayList<Condition>();
	private String elseCondition = null;

	public SelectCase() {
	}

	public SelectCase(String caseValue) {
		this.caseValue = caseValue;
	}

	@Override
	public String toString() {
		String sql = "CASE";
		if (caseValue != null)
			sql += " " + caseValue;

		for (Condition condition : conditions) {
			sql += " WHEN " + condition.when + " THEN " + condition.then;
		}
		if (elseCondition != null)
			sql += " ELSE " + elseCondition;

		return sql + " END";
	}

	public void addCondition(String when, String then) {
		conditions.add(new Condition(when, then));
	}

	public void setElse(String elseCondition) {
		this.elseCondition = elseCondition;
	}

	private class Condition {
		String when;
		String then;

		public Condition(String when, String then) {
			this.when = when;
			this.then = then;
		}
	}
}
