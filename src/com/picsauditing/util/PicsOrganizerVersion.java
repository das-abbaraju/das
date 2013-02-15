package com.picsauditing.util;

public class PicsOrganizerVersion {
	final static public int major = 6;
	final static public int minor = 37;
	final static public int patch = 0;

	@SuppressWarnings("unused")
	public static final String getVersion() {
		if (patch > 0) {
			return major + "." + minor + "." + patch;
		}
		return major + "." + minor;
	}

	public static boolean greaterThan(int maj, int min) {
		return (major > maj) || (major == maj && minor > min);
	}
}
