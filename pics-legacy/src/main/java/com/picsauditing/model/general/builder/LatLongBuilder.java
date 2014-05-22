package com.picsauditing.model.general.builder;

import com.picsauditing.model.general.LatLong;

public class LatLongBuilder {
    private LatLong latLong = new LatLong();

    public LatLongBuilder lat(double lat) {
        latLong.setLatitude(lat);
        return this;
    }

    public LatLongBuilder lng(double lng) {
        latLong.setLongitude(lng);
        return this;
    }

    public LatLong build() {
        return latLong;
    }
}
