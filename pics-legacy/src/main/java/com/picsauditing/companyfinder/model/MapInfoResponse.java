package com.picsauditing.companyfinder.model;

import com.picsauditing.companyfinder.model.builder.MapInfoResponseBuilder;
import com.picsauditing.model.general.LatLong;

public class MapInfoResponse {
    private LatLong center;
    private ViewPort viewPort;

    public LatLong getCenter() {
        return center;
    }

    public void setCenter(LatLong center) {
        this.center = center;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
    }

    public static MapInfoResponseBuilder builder() {
        return new MapInfoResponseBuilder();
    }
}
