package com.picsauditing.report.fields;

public enum QueryFilterOperator {
	
	Equals("="),
	NotEquals("!="),
	GreaterThan(">"),
	LessThan("<"),
	GreaterThanOrEquals(">="),
	LessThanOrEquals("<="),
	In("IN"),
	NotIn("NOT IN"),
	BeginsWith("LIKE"),
	NotBeginsWith("NOT LIKE"),
	EndsWith("LIKE"),
	NotEndsWith("NOT LIKE"),
	Contains("LIKE"),
	NotContains("NOT LIKE"),
	Empty("IS NULL"),
	NotEmpty("NOT IS NULL");
	
	private String operand;

	private QueryFilterOperator(String operand) {
		this.operand = operand;
	}

	public String getOperand() {
		return operand;
	}
}
