package com.picsauditing.report.fields;

public enum QueryMethod {
	// None,
	Count(true, ExtFieldType.Int),
	CountDistinct(true, ExtFieldType.Int),
	GroupConcat(true, ExtFieldType.String), 
	Max(true),
	Min(true),
	
	Average(true), 
	Round(false, ExtFieldType.Int), // Parameter means decimal place
	Sum(true),
	
	Left, // Parameter means number of characters
	Length,
	LowerCase,
	UpperCase,
	
	Month(false, ExtFieldType.String), // January TODO translate the 1 into January
	Year(false, ExtFieldType.String), // 2012
	YearMonth(false, ExtFieldType.String), // 2012-01 or we can use 2012-Jan IF we can solve the sorting problem
	WeekDay(false, ExtFieldType.String), // Monday TODO translate the 1
	Hour(false, ExtFieldType.String), // 23 
	Date // 2012-01-31
	;

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
	
	public boolean isNeedsParameter() {
		if (this == Left) {
			return true;
		}
		if (this == Round) {
			return true;
		}
		return false;
	}
}
