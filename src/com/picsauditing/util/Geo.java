package com.picsauditing.util;


public class Geo {
	/**
	 * Calculate the distance (kilometers) between two points
	 * See http://www.movable-type.co.uk/scripts/latlong.html
	 * @param latitude1
	 * @param longitude1
	 * @param latitude2
	 * @param longitude2
	 * @return kilometers between the two points
	 */
	static public double distance(float latitude1, float longitude1, float latitude2, float longitude2) {
		int radius = 6371; // km
		
		double dLat = Math.toRadians(latitude2-latitude1);
		double dLon = Math.toRadians(longitude2-longitude1);
		
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) * 
		        Math.sin(dLon/2) * Math.sin(dLon/2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = radius * c;

		return d;
	}
}
