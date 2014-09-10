package com.picsauditing.util;

public enum TriState {
    TRUE,
    FALSE,
    UNKNOWN;

    public boolean toBoolean() {
        if (this == TRUE) {
            return true;
        }
        if (this == FALSE) {
            return false;
        }
        throw new IllegalStateException("Cannot convert TriState.UNKNOWN to a boolean.");
    }
}
