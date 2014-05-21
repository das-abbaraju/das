package com.picsauditing.companyfinder.model;

import com.picsauditing.companyfinder.model.builder.ViewPortBuilder;
import com.picsauditing.model.general.LatLong;

public class ViewPort {
    private LatLong northEast;
    private LatLong southWest;

    public LatLong getNorthEast() {
        return northEast;
    }

    public void setNorthEast(LatLong northEast) {
        this.northEast = northEast;
    }

    public LatLong getSouthWest() {
        return southWest;
    }

    public void setSouthWest(LatLong southWest) {
        this.southWest = southWest;
    }

    public static ViewPortBuilder builder() {
        return new ViewPortBuilder();
    }
}
