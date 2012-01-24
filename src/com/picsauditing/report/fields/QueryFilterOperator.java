package com.picsauditing.report.fields;

public enum QueryFilterOperator {
	Equals("="),
	GreaterThan(">"),
	LessThan("<"),
	GreaterThanOrEquals(">="),
	LessThanOrEquals("<="),
	In("IN"),
	InReport("IN"),
	BeginsWith("LIKE"),
	EndsWith("LIKE"),
	Contains("LIKE"),
	Empty("IS NULL");
	private String operand;

	private QueryFilterOperator(String operand) {
		this.operand = operand;
	}

	public String getOperand() {
		return operand;
	}
}
