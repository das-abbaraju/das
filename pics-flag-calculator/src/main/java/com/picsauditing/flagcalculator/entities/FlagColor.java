package com.picsauditing.flagcalculator.entities;

public enum FlagColor {
    Green, Amber, Red, Clear;

    public static FlagColor getWorseColor(FlagColor color1, FlagColor color2) {
        if (color2 == null) {
            return color1;
        }

        if (color1 == null) {
            return color2;
        }

        if (color2.ordinal() > color1.ordinal()) {
            color1 = color2;
        }
        return color1;
    }

    public boolean isWorseThan(FlagColor flagColor2) {
        if (flagColor2 == null) {
            return true;
        }

        if (this.compareTo(flagColor2) < 0) {
            return true;
        }

        return false;
    }
}