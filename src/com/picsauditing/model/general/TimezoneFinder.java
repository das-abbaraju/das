package com.picsauditing.model.general;

import java.util.TimeZone;

public interface TimezoneFinder {
	TimeZone timezoneIdFromAddress(String address);
}
