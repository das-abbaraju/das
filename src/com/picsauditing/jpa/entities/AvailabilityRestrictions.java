package com.picsauditing.jpa.entities;

import java.io.Serializable;

public class AvailabilityRestrictions implements Serializable {
	private static final long serialVersionUID = -5298244671418832612L;

	private float nearLatitude = 0;
	private float nearLongitude = 0;
	private boolean webOnly = false;
	private String[] onlyInStates = null;
	public float getNearLatitude() {
		return nearLatitude;
	}
	public void setNearLatitude(float nearLatitude) {
		this.nearLatitude = nearLatitude;
	}
	public float getNearLongitude() {
		return nearLongitude;
	}
	public void setNearLongitude(float nearLongitude) {
		this.nearLongitude = nearLongitude;
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
