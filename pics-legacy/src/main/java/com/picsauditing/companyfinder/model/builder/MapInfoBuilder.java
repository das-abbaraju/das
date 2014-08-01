package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.MapInfo;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.model.general.LatLong;

public class MapInfoBuilder {
    private MapInfo mapInfo = new MapInfo();

    public MapInfoBuilder center(LatLong center) {
        mapInfo.setCenter(center);
        return this;
    }

    public MapInfoBuilder viewPort(ViewPort viewPort) {
        mapInfo.setViewPort(viewPort);
        return this;
    }

    public MapInfo build() {
        return mapInfo;
    }
}
