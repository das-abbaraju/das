package com.picsauditing.companyfinder.model;


import com.picsauditing.companyfinder.model.builder.ContractorLocationInfoBuilder;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.model.general.LatLong;

import java.util.List;

public class ContractorLocationInfo {

    private int id;
    private String name;
    private String address;
    private String formattedAddressBlock;
    private LatLong coordinates;
    private String primaryTrade;
    private List<String> trades;
    private String link;
    private boolean worksForOperator;
    private FlagColor flagColor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setFormattedAddressBlock(String formattedAddressBlock) {
        this.formattedAddressBlock = formattedAddressBlock;
    }

    public String getFormattedAddressBlock() {
        return formattedAddressBlock;
    }

    public LatLong getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLong coordinates) {
        this.coordinates = coordinates;
    }

    public String getPrimaryTrade() {
        return primaryTrade;
    }

    public void setPrimaryTrade(String trade) {
        this.primaryTrade = trade;
    }

    public void setTrades(List<String> trades) {
        this.trades = trades;
    }

    public List<String> getTrades() {
        return trades;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

   public static ContractorLocationInfoBuilder builder() {
        return new ContractorLocationInfoBuilder();
    }

    public boolean isWorksForOperator() {
        return worksForOperator;
    }

    public void setWorksForOperator(boolean isWorksForOperator) {
        this.worksForOperator = isWorksForOperator;
    }

    public void setFlagColor(FlagColor flagColor) {
        this.flagColor = flagColor;
    }
}


