package com.picsauditing.companyfinder.model;

public enum TriStateFlag {
    IGNORE(-1),
    EXCLUDE(0),
    INCLUDE(1);

    private int safetySensitiveValue;

    TriStateFlag(int value){
        this.safetySensitiveValue = value;
    }

    public static TriStateFlag fromInteger(int x) {
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