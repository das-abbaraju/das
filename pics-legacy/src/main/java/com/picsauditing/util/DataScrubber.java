package com.picsauditing.util;

/**
 * Purpose of this class is for all the helper methods used to cleanse/scrub data before it is persisted.
 */
public class DataScrubber {

	private static final String REGEX_FOR_MULTIPLE_SPACES = "\\s+";
	
	public static String cleanUKPostcode(String postcode) {
		return postcode.trim().replaceAll(REGEX_FOR_MULTIPLE_SPACES, " "); 
	}
	
}
