package com.picsauditing.actions;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.TimeZoneDAO;
import com.picsauditing.jpa.entities.TimeZoneListByCountry;
import com.picsauditing.util.DateUtil;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class TimeZoneRetriever extends PicsActionSupport {

	private String countryCode;

	@Autowired
	private TimeZoneDAO timeZoneDAO;

	@SuppressWarnings("unchecked")
	@Anonymous
	public String execute() {
		String zoneName = "";

		json = new JSONObject();
		JSONArray result = new JSONArray();

		json.put("result", result);

		List<TimeZoneListByCountry> timeZonesByCountryCode = timeZoneDAO.findByCountryCode(countryCode);

		for (TimeZoneListByCountry timeZone: timeZonesByCountryCode) {
			zoneName = timeZone.getTimeZoneName();
			JSONObject timezoneData = new JSONObject();
			try {
				DateTimeZone zone = DateTimeZone.forID(zoneName);
				timezoneData.put("date", getCurrentDate(zone));
				timezoneData.put("offset", getCurrentOffset(zone));
				timezoneData.put("time", getCurrentTime(zone));
				timezoneData.put("id", zoneName);
				result.add(timezoneData);
			} catch (IllegalArgumentException e) {
				continue;
			}
		}

		return JSON;
	}

	private String getCurrentDate(DateTimeZone zone) {
		DateTime now = DateTime.now(zone);

		String currentDate =
				getText(DateUtil.getDayOfWeek(now.getDayOfWeek())) + ", " +
				getText(DateUtil.getMonthOfYear(now.getMonthOfYear())) + " " +
				now.getDayOfMonth();

		return currentDate;
	}


	public static String formatTimeToTwelveHour(int hourOfDay, int minuteOfHour) {
		String currentTime = "";
		String period = "AM";

		if (hourOfDay >= 12) {
			if (hourOfDay != 12) {
				hourOfDay -= 12;
			}
			period = "PM";
		}
		currentTime = hourOfDay + ":";

		if (minuteOfHour < 10) {
			currentTime += "0";
		}

		currentTime += minuteOfHour + " " + period;

		return currentTime;
	}

	public static String getCurrentTime(DateTimeZone zone) {
		DateTime now = DateTime.now(zone);

		String currentTime = formatTimeToTwelveHour(now.getHourOfDay(),now.getMinuteOfHour());

		return currentTime;
	}

	public static String getCurrentOffset(DateTimeZone zone) {
		double currentOffset = zone.getOffset(Instant.now());

        currentOffset =  (currentOffset / (double) (3600 * 1000));

        long iPart = (long) currentOffset;
        double fPart = currentOffset - iPart;

        String output = Strings.EMPTY_STRING;

        if (iPart > 0) {
        	output = "+" + iPart;
        } else {
        	output = String.valueOf(iPart);
        }

        //convert decimal to minutes
        double minutes = 0.0;


        if (fPart != 0.0) {
        	minutes = fPart * 60;
        	output += ":" + (int) Math.abs(minutes);
        } else {
        	output += ":00";
        }

        return output;
	}


	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}
