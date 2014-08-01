package com.picsauditing.companyfinder.model;

import com.picsauditing.companyfinder.model.builder.ViewportLocationBuilder;
import com.picsauditing.model.general.LatLong;

public class ViewportLocation {
    private String address;
    private LatLong coordinates;
    private ViewPort viewPort;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLong getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLong coordinates) {
        this.coordinates = coordinates;
    }

    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public static ViewportLocationBuilder builder() {
        return new ViewportLocationBuilder();
    }

}
