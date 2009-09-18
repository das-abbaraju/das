package com.picsauditing.jpa.entities;

import java.io.Serializable;

import com.picsauditing.util.Location;

public class AvailabilityRestrictions implements Serializable {
	private static final long serialVersionUID = -5298244671418832612L;

	private Location location = null;

	private boolean webOnly = false;
	private String[] onlyInStates = null;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
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

}
