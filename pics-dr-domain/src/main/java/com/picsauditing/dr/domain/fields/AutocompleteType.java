package com.picsauditing.dr.domain.fields;

public abstract class AutocompleteType implements FieldType {

    public DisplayType getDisplayType() {
        return DisplayType.String;
    }

    public FilterType getFilterType() {
        return FilterType.Autocomplete;
    }

}
