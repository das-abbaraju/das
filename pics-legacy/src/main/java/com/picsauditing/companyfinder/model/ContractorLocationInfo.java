package com.picsauditing.companyfinder.model;


import com.picsauditing.companyfinder.model.builder.ContractorLocationInfoBuilder;
import com.picsauditing.model.general.LatLong;

public class ContractorLocationInfo {

    private int id;
    private String name;
    private String address;
    private LatLong coordinates;
    private String trade;
    private String link;

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

    public LatLong getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLong coordinates) {
        this.coordinates = coordinates;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
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
}


