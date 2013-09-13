package com.picsauditing.util;


public class Geo {
	/**
	 * Calculate the distance (kilometers) between two points See http://www.movable-type.co.uk/scripts/latlong.html
	 * 
	 * @param lat1
	 * @param long1
	 * @param lat2
	 * @param long2
	 * @return kilometers between the two points
	 */
	static public double distance(float lat1, float long1, float lat2, float long2) {
		int radius = 6371; // km

		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(long2 - long1);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = radius * c;

		return d;
	}

	public static double distance(Location location1, Location location2) {
		return distance(location1.getLatitude(), location1.getLongitude(), location2.getLatitude(), location2.getLongitude());
	}

	public static Location middle(Location location1, Location location2) {
		return new Location((location1.getLatitude() - location2.getLatitude())/2, (location1.getLongitude() - location2.getLongitude())/2);
	}
}
