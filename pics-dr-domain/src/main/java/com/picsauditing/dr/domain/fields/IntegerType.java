package com.picsauditing.dr.domain.fields;

public class IntegerType implements FieldType {
    public DisplayType getDisplayType() {
        return DisplayType.Number;
    }

    public FilterType getFilterType() {
        return FilterType.Number;
    }
}
