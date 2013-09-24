package com.picsauditing.dr.domain.fields;

public class BooleanType implements FieldType {
    public DisplayType getDisplayType() {
        return DisplayType.Boolean;
    }

    public FilterType getFilterType() {
        return FilterType.Boolean;
    }
}
