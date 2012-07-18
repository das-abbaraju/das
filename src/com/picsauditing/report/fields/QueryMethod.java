package com.picsauditing.report.fields;

public enum QueryMethod {
	Average(true),
	Count(true, ExtFieldType.Int),
	CountDistinct(true, ExtFieldType.Int),
	// TODO this may need to be a string depending on how we use date
	Date,
	LowerCase,
	Max(true),
	Min(true),
	Month(false, ExtFieldType.String),
	Round(false, ExtFieldType.Int),
	Sum(true),
	UpperCase,
	Year(false, ExtFieldType.String),
	None;

	private boolean aggregate;
	private ExtFieldType type = ExtFieldType.Auto;

	private QueryMethod() {
	}

	private QueryMethod(boolean aggregate) {
		this.aggregate = aggregate;
	}

	private QueryMethod(boolean aggregate, ExtFieldType type) {
		this.aggregate = aggregate;
		this.type = type;
	}

	public ExtFieldType getType() {
		return type;
	}

	public boolean isAggregate() {
		return aggregate;
	}
}
