package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.MapInfoResponse;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.model.general.LatLong;

public class MapInfoResponseBuilder {
    private MapInfoResponse mapInfoResponse = new MapInfoResponse();

    public MapInfoResponseBuilder center(LatLong center) {
        mapInfoResponse.setCenter(center);
        return this;
    }

    public MapInfoResponseBuilder viewPort(ViewPort viewPort) {
        mapInfoResponse.setViewPort(viewPort);
        return this;
    }

    public MapInfoResponse build() {
        return mapInfoResponse;
    }
}
