package com.picsauditing.util;


import org.json.simple.JSONObject;

import java.io.*;

public class Geo {
	private static final Object COUNTRY_CODE = "country_code";
	private URLUtils urlUtils = SpringUtils.getBean(SpringUtils.URL_UTILS);

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

	public String getInformationFromRequestIP() throws IOException {
		String ipAddress = urlUtils.getRequestIPAddress();
		return urlUtils.getResponseFrom(buildHostIpLookupUrl(ipAddress));
	}

	public String getCountryCodeFromRequestIP() throws IOException {
		JSONObject json = JSONUtilities.parseJsonFromInput(
				new BufferedReader(
						new InputStreamReader(
								new ByteArrayInputStream(
										getInformationFromRequestIP().getBytes()
								)
						)
				)
		);
		String countryCode = (String) json.get(COUNTRY_CODE);
		return countryCode;
	}

	public String buildHostIpLookupUrl(String ip) {
		return "http://api.hostip.info/get_json.php?ip=" + ip;
	}
}
