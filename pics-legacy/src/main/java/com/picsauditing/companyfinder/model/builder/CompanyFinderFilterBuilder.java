package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.CompanyFinderFilter;
import com.picsauditing.companyfinder.model.SafetySensitive;
import com.picsauditing.companyfinder.model.ViewPort;

import java.util.List;

public class CompanyFinderFilterBuilder {
    private CompanyFinderFilter filter = new CompanyFinderFilter();

    public CompanyFinderFilterBuilder viewPort(ViewPort viewPort) {
        filter.setViewPort(viewPort);
        return this;
    }

    public CompanyFinderFilterBuilder safetySensitive(SafetySensitive safetySensitive) {
        filter.setSafetySensitive(safetySensitive);
        return this;
    }

    public CompanyFinderFilter build() {
        return filter;
    }

    public CompanyFinderFilterBuilder tradeIds(List<Integer> tradeIds) {
        filter.setTradeIds(tradeIds);
        return this;
    }
}
