package com.picsauditing.flagcalculator.service;

import com.picsauditing.flagcalculator.entities.FlagDataOverride;

import java.util.Date;

public class FlagService {
    public static boolean isInForce(FlagDataOverride flagDataOverride) {
        if (flagDataOverride.getForceEnd() == null)
            return false;
        return flagDataOverride.getForceEnd().after(new Date());
    }
}
