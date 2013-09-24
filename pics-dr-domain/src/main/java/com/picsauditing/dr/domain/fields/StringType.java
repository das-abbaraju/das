package com.picsauditing.dr.domain.fields;

public class StringType implements FieldType {
    public DisplayType getDisplayType() {
        return DisplayType.String;
    }

    public FilterType getFilterType() {
        return FilterType.String;
    }
}
