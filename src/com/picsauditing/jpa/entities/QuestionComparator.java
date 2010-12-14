package com.picsauditing.jpa.entities;

public enum QuestionComparator {
	Equals("is equal to"),
	NotEquals("is NOT equal to"),
	NotEmpty("is answered"),
	Empty("is NOT answered"),
	Verified("is verified"),
	StartsWith("startsWith"),
	LessThan("less than"),
	LessThanEqual("less than or equal to"),
	GreaterThan("greater than"),
	GreaterThanEqual("greater than or equal to");

	private String description;

	private QuestionComparator(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
