package com.picsauditing.companyfinder.model;

import com.picsauditing.jpa.entities.Trade;

public class CompanyFinderFilter {

    private ViewPort viewPort;
    private Trade trade;
    private SafetySensitive safetySensitive = SafetySensitive.IGNORE;
    //private int preFlagStatus = ;

    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public SafetySensitive getSafetySensitive() {
        return safetySensitive;
    }

    public void setSafetySensitive(SafetySensitive safetySensitive) {
        this.safetySensitive = safetySensitive;
    }
}
