package com.picsauditing.report.fields;

public enum SqlFunction {

	Count(DisplayType.Number, FilterType.Number, true),
	CountDistinct(DisplayType.Number, FilterType.Number, true),
	Average(DisplayType.Number, FilterType.Number, true),
	Round(DisplayType.Number, FilterType.Number, false),
	Sum(DisplayType.Number, FilterType.Number, true),
	StdDev(DisplayType.Number, FilterType.Number, true),
	Length(DisplayType.Number, FilterType.Number, false),
	Year(DisplayType.Number, FilterType.Number, false),
	WeekDay(DisplayType.Number, FilterType.Number, false),
	Hour(DisplayType.Number, FilterType.Number, false),

    YearMonth(DisplayType.String, FilterType.Date, false),
	GroupConcat(DisplayType.String, FilterType.String, true),
	Month(DisplayType.String, FilterType.String, false),
	Date(DisplayType.String, FilterType.String, false),
    DaysFromNow(DisplayType.Number, FilterType.Number, false),

	// displayType = null indicates to use the DisplayType of the input value. Same with filterType = null.
	Max(null, null, true),
	Min(null, null, true),
	LowerCase(null, null, false),
	UpperCase(null, null, false);

    private DisplayType displayType;
	private FilterType filterType;
	private boolean aggregate;

	SqlFunction(DisplayType displayType, FilterType filterType, boolean aggregate) {
		this.displayType = displayType;
		this.filterType = filterType;
		this.aggregate = aggregate;
	}

	public DisplayType getDisplayType(Field field) {
		if (displayType == null) {
			return field.getType().getDisplayType();
		}
		return displayType;
	}

    public DisplayType getDisplayType() {
        return displayType;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public FilterType getFilterType(Field field) {
		if (filterType == null) {
			return field.getType().getFilterType();
		}
		return filterType;
	}

	public boolean isAggregate() {
		return aggregate;
	}
}
