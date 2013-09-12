package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagData;

public class FlagDataBuilder {
    private FlagData flagData = new FlagData();

    public FlagDataBuilder criteria(FlagCriteria criteria) {
        flagData.setCriteria(criteria);
        return this;
    }

    public FlagDataBuilder flag(FlagColor color) {
        flagData.setFlag(color);
        return this;
    }

    public FlagData build() {
        return flagData;
    }
}
