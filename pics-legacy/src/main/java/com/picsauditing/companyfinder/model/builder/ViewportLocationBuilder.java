package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.ViewportLocation;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.model.general.LatLong;

public class ViewportLocationBuilder {
    private ViewportLocation locationResponse = new ViewportLocation();

    public ViewportLocationBuilder address(String address) {
        locationResponse.setAddress(address);
        return this;
    }

    public ViewportLocationBuilder coordinates(LatLong coordinates) {
        locationResponse.setCoordinates(coordinates);
        return this;
    }

    public ViewportLocation build() {
        return locationResponse;
    }

    public ViewportLocationBuilder viewPort(ViewPort viewPort) {
        locationResponse.setViewPort(viewPort);
        return this;
    }
}
