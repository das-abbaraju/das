package com.picsauditing.companyfinder.model;

public enum SafetySensitive {
    IGNORE(-1),
    EXCLUDE(0),
    INCLUDE(1);

    private int safetySensitiveValue;

    SafetySensitive(int value){
        this.safetySensitiveValue = value;
    }

    public static SafetySensitive fromInteger(int x) {
        switch(x) {
            case -1:
                return IGNORE;
            case 0:
                return EXCLUDE;
            case 1:
                return INCLUDE;
        }
        return IGNORE;
    }

    public int getSafetySensitiveValue(){
        return safetySensitiveValue;
    }

    public boolean toBoolean() {
        return this == INCLUDE;
    }
}