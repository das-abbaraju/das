package com.picsauditing.flagcalculator;

import java.util.List;

public interface FlagCalculator {
    List<FlagData> calculate();
    void saveFlagData(List<FlagData> flagDatas);
}
