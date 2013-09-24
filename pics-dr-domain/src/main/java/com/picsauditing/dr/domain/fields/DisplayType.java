package com.picsauditing.dr.domain.fields;


public enum DisplayType {
	Boolean, Flag, Number, String;

    public boolean isNumber() {
        return this == Number;
    }
}
