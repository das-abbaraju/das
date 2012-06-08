package com.picsauditing.util;


/**
 * Latitude/Longitude/Elevation object used for geo calculations
 * 
 * @author Trevor
 * 
 */
public class Location {
	private float latitude = 0;
	private float longitude = 0;
	private float elevation = 0;

	public Location(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Location(float latitude, float longitude, float elevation) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public float getElevation() {
		return elevation;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		try {
			Location other = (Location) obj;
			return (other.getElevation() == elevation && other.getLatitude() == latitude && other.getLongitude() == longitude);
		} catch (Exception e) {
			System.out.println("Error comparing BaseTable objects: " + e.getMessage());
			return false;
		}
	}
}
