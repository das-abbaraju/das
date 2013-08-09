package com.picsauditing.report.fields;


public enum DisplayType {
	Boolean, Flag, Number, String;

    public boolean isNumber() {
        return this == Number;
    }
}
