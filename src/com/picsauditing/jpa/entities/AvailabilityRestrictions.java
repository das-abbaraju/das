package com.picsauditing.jpa.entities;

import java.io.Serializable;

import com.picsauditing.util.Location;

public class AvailabilityRestrictions implements Serializable {
	private static final long serialVersionUID = -8383954239800310895L;
	
	private float latitude = 0;
	private float longitude = 0;
	private int maxDistance = 30; // km

	private boolean onsiteOnly = false;
	private boolean webOnly = false;
	private String[] onlyInStates = null;

	public Location getLocation() {
		return new Location(latitude, longitude);
	}

	public void setLocation(Location location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}

	public boolean isOnsiteOnly() {
		return onsiteOnly;
	}

	public void setOnsiteOnly(boolean onsiteOnly) {
		this.onsiteOnly = onsiteOnly;
	}

	public boolean isWebOnly() {
		return webOnly;
	}

	public void setWebOnly(boolean webOnly) {
		this.webOnly = webOnly;
	}

	public String[] getOnlyInStates() {
		return onlyInStates;
	}

	public void setOnlyInStates(String[] onlyInStates) {
		this.onlyInStates = onlyInStates;
	}

	public int getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}

}
