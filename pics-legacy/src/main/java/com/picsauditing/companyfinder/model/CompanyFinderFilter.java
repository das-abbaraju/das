package com.picsauditing.companyfinder.model;

import java.util.List;

public class CompanyFinderFilter {

    private ViewPort viewPort;
    private TriStateFlag safetySensitive = TriStateFlag.IGNORE;
    private TriStateFlag soleProprietor = TriStateFlag.IGNORE;
    private List<Integer> tradeIds;
    private List<Integer> contractorIds;

    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public TriStateFlag getSafetySensitive() {
        return safetySensitive;
    }

    public void setSafetySensitive(TriStateFlag safetySensitive) {
        this.safetySensitive = safetySensitive;
    }

    public List<Integer> getTradeIds() {
        return tradeIds;
    }

    public void setTradeIds(List<Integer> tradeIds) {
        this.tradeIds = tradeIds;
    }

    public TriStateFlag getSoleProprietor() {
        return soleProprietor;
    }

    public void setSoleProprietor(TriStateFlag soleProprietor) {
        this.soleProprietor = soleProprietor;
    }

    public void setContractorIds(List<Integer> contractorIds) {
        this.contractorIds = contractorIds;
    }

    public List<Integer> getContractorIds() {
        return contractorIds;
    }
}
