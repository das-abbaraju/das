package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.model.general.LatLong;

public class ViewPortBuilder {

    private ViewPort viewPort = new ViewPort();

    public ViewPortBuilder northEast(LatLong northEast) {
        viewPort.setNorthEast(northEast);
        return this;
    }

    public ViewPortBuilder southWest(LatLong southWest) {
        viewPort.setSouthWest(southWest);
        return this;
    }

    public ViewPort build() {
        return viewPort;
    }
}
