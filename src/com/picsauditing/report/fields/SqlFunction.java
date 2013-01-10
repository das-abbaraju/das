package com.picsauditing.report.fields;

public enum SqlFunction {
	// None,
	Count(true, DisplayType.RightAlign),
	CountDistinct(true, DisplayType.RightAlign),
	GroupConcat(true, DisplayType.String),
	Max(true),
	Min(true),
	
	Average(true, DisplayType.RightAlign),
	Round(false, DisplayType.RightAlign), // Parameter means decimal place
	Sum(true),
	StdDev(true, DisplayType.RightAlign),
	
	Left, // Parameter means number of characters
	Length,
	LowerCase,
	UpperCase,
	
	Month(false, DisplayType.String), // January TODO translate the 1 into January
	Year(false, DisplayType.RightAlign), // 2012
	YearMonth(false, DisplayType.String), // 2012-01 or we can use 2012-Jan IF we can solve the sorting problem
	WeekDay(false, DisplayType.RightAlign), // Monday TODO translate the 1
	Hour(false, DisplayType.RightAlign), // 23 
	Date // 2012-01-31
	;

	private boolean aggregate;
	private DisplayType displayType = null;

	private SqlFunction() {
	}

	private SqlFunction(boolean aggregate) {
		this.aggregate = aggregate;
	}

	private SqlFunction(boolean aggregate, DisplayType type) {
		this.aggregate = aggregate;
		this.displayType = type;
	}

	public DisplayType getDisplayType() {
		return displayType;
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
