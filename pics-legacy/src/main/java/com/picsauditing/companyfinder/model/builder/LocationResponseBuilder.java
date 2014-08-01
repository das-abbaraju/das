package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.ViewportLocation;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.model.general.LatLong;

public class LocationResponseBuilder {
    private ViewportLocation locationResponse = new ViewportLocation();

    public LocationResponseBuilder address(String address) {
        locationResponse.setAddress(address);
        return this;
    }

    public LocationResponseBuilder coordinates(LatLong coordinates) {
        locationResponse.setCoordinates(coordinates);
        return this;
    }

    public ViewportLocation build() {
        return locationResponse;
    }

    public LocationResponseBuilder viewPort(ViewPort viewPort) {
        locationResponse.setViewPort(viewPort);
        return this;
    }
}
