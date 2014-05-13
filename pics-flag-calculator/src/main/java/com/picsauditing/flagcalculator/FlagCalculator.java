package com.picsauditing.flagcalculator;

import java.util.List;

public interface FlagCalculator {
    List<FlagData> calculate();
    boolean saveFlagData(List<FlagData> flagDatas);
    void setShouldPublishChanges(boolean shouldPublishChanges);
    void setWorksForOperator(boolean worksForOperator);
}
