package com.picsauditing.report;

import java.util.Date;

import com.ibm.icu.util.Calendar;
import com.picsauditing.util.Strings;

public class QueryDateParameter {
	private Date date = new Date();

	public QueryDateParameter(String value) {
		if (Strings.isEmpty(value))
			return;

		value = value.trim();

		if (value.matches("[0-9]+")) {
			long unixtime = Long.parseLong(value);
			date = new Date(unixtime * 1000);
			return;
		}

		boolean plusTime = true;
		if ('-' == value.charAt(0)) {
			plusTime = false;
			value = value.substring(1);
		}

		char period = value.charAt(value.length() - 1);
		value = value.substring(0, value.length() - 1);
		int amount = Integer.parseInt(value);

		if (!plusTime) {
			amount *= -1;
		}

		Calendar cal = Calendar.getInstance();
		cal.add(convertPeriod(period), amount);
		date = cal.getTime();
	}

	private int convertPeriod(char period) {
		if (period == 'y')
			return Calendar.YEAR;
		if (period == 'm')
			return Calendar.MONTH;
		if (period == 'w')
			return Calendar.WEEK_OF_YEAR;
		if (period == 'h')
			return Calendar.HOUR_OF_DAY;
		return Calendar.DAY_OF_YEAR;
	}

	public Date getTime() {
		return date;
	}
}
