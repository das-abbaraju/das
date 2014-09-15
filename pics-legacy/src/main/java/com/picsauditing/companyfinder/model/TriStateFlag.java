package com.picsauditing.companyfinder.model;

public enum TriStateFlag {
    IGNORE(-1),
    EXCLUDE(0),
    INCLUDE(1);

    private int value;

    TriStateFlag(int value){
        this.value = value;
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

    public int getValue(){
        return value;
    }

    public boolean toBoolean() {
        return this == INCLUDE;
    }
}