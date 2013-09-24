package com.picsauditing.dr.domain.fields;

public class DateType implements FieldType {
    public DisplayType getDisplayType() {
        return DisplayType.String;
    }

    public FilterType getFilterType() {
        return FilterType.Date;
    }
}
