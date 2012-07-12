package com.picsauditing.report.fields;

public enum QueryFunction {
	Average(true),
	Count(true),
	CountDistinct(true),
	Date,
	LowerCase,
	Max(true),
	Min(true),
	Month, // TODO: Find a way to display the numeric instead of the date
	Round,
	Sum(true),
	UpperCase,
	Year,
	None;

	private boolean aggregate;

	private QueryFunction() {
	}

	private QueryFunction(boolean aggregate) {
		this.aggregate = aggregate;
	}

	public boolean isAggregate() {
		return aggregate;
	}
}
