package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.CompanyFinderFilter;
import com.picsauditing.companyfinder.model.TriStateFlag;
import com.picsauditing.companyfinder.model.ViewPort;

import java.util.List;

public class CompanyFinderFilterBuilder {
    private CompanyFinderFilter filter = new CompanyFinderFilter();

    public CompanyFinderFilterBuilder viewPort(ViewPort viewPort) {
        filter.setViewPort(viewPort);
        return this;
    }

    public CompanyFinderFilterBuilder safetySensitive(TriStateFlag safetySensitive) {
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

    public CompanyFinderFilterBuilder soleProprietor(TriStateFlag soleProprietor) {
        filter.setSoleProprietor(soleProprietor);
        return this;
    }

    public CompanyFinderFilterBuilder contractorIds(List<Integer> contractorIds) {
        filter.setContractorIds(contractorIds);
        return this;
    }
}
