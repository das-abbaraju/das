package com.picsauditing.report.fields;

public enum QueryMethod {
	Average(true),
	Count(true),
	CountDistinct(true),
	Date,
	LowerCase,
	Max(true),
	Min(true),
	Month,
	Round,
	Sum(true),
	UpperCase,
	Year,
	None;

	private boolean aggregate;

	private QueryMethod() {
	}

	private QueryMethod(boolean aggregate) {
		this.aggregate = aggregate;
	}

	public boolean isAggregate() {
		return aggregate;
	}
}
