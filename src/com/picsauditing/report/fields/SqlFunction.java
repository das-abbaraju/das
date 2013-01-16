package com.picsauditing.report.fields;

// todo: Add in all the missing SqlFunctionReturnTypes!
public enum SqlFunction {
	// None,
	Count(true, DisplayType.Number, SqlFunctionReturnType.Integer),
	CountDistinct(true, DisplayType.Number),
	GroupConcat(true, DisplayType.String),
	Max(true),
	Min(true),
	
	Average(true, DisplayType.Number),
	Round(false, DisplayType.Number), // Parameter means decimal place
	Sum(true),
	StdDev(true, DisplayType.Number),
	
	Left, // Parameter means number of characters
	Length,
	LowerCase,
	UpperCase,
	
	Month(false, DisplayType.String), // January TODO translate the 1 into January
	Year(false, DisplayType.Number, SqlFunctionReturnType.Year), // 2012
	YearMonth(false, DisplayType.String), // 2012-01 or we can use 2012-Jan IF we can solve the sorting problem
	WeekDay(false, DisplayType.Number), // Monday TODO translate the 1
	Hour(false, DisplayType.Number), // 23 
	Date // 2012-01-31
	;

	private boolean aggregate;
	private DisplayType displayType = null;
	private SqlFunctionReturnType sqlFunctionReturnType;

	private SqlFunction() {
	}

	private SqlFunction(boolean aggregate) {
		this.aggregate = aggregate;
	}

	private SqlFunction(boolean aggregate, DisplayType type) {
		this.aggregate = aggregate;
		this.displayType = type;
	}



	SqlFunction(boolean aggregate, DisplayType displayType, SqlFunctionReturnType returnType) {
		this.aggregate = aggregate;
		this.displayType = displayType;
		this.sqlFunctionReturnType = returnType;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}

	public boolean isAggregate() {
		return aggregate;
	}

	public SqlFunctionReturnType getReturnType() {
		return sqlFunctionReturnType;
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
