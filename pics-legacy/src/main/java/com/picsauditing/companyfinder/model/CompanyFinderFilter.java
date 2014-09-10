package com.picsauditing.companyfinder.model;

import java.util.List;

public class CompanyFinderFilter {

    private ViewPort viewPort;
    private SafetySensitive safetySensitive = SafetySensitive.IGNORE;
    private List<Integer> tradeIds;

    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public SafetySensitive getSafetySensitive() {
        return safetySensitive;
    }

    public void setSafetySensitive(SafetySensitive safetySensitive) {
        this.safetySensitive = safetySensitive;
    }

    public List<Integer> getTradeIds() {
        return tradeIds;
    }

    public void setTradeIds(List<Integer> tradeIds) {
        this.tradeIds = tradeIds;
    }
}
