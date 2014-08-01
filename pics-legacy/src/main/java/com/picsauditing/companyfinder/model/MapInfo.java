package com.picsauditing.companyfinder.model;

import com.picsauditing.companyfinder.model.builder.MapInfoBuilder;
import com.picsauditing.model.general.LatLong;

public class MapInfo {
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

    public static MapInfoBuilder builder() {
        return new MapInfoBuilder();
    }
}
