package com.picsauditing.companyfinder.model.builder;

import com.itextpdf.text.pdf.CFFFont;
import com.picsauditing.companyfinder.model.CompanyFinderFilter;
import com.picsauditing.companyfinder.model.SafetySensitive;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.jpa.entities.Trade;

public class CompanyFinderFilterBuilder {
    private CompanyFinderFilter filter = new CompanyFinderFilter();

    public CompanyFinderFilterBuilder viewPort(ViewPort viewPort) {
        filter.setViewPort(viewPort);
        return this;
    }

    public CompanyFinderFilterBuilder trade(Trade trade) {
        filter.setTrade(trade);
        return this;
    }

    public CompanyFinderFilterBuilder safetySensitive(SafetySensitive safetySensitive) {
        filter.setSafetySensitive(safetySensitive);
        return this;
    }

    public CompanyFinderFilter build() {
        return filter;
    }
}
