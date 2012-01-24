package com.picsauditing.report.fields;

import java.util.Date;

import com.ibm.icu.util.Calendar;
import com.picsauditing.PICS.DateBean;
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
		String valueWithoutPeriod = value.substring(0, value.length() - 1);
		Calendar cal = Calendar.getInstance();

		try {
			int amount = Integer.parseInt(valueWithoutPeriod);

			if (!plusTime) {
				amount *= -1;
			}
			cal.add(convertPeriod(period), amount);

		} catch (NumberFormatException nfe) {
			Date parsedDate = DateBean.parseDate(value);
			if (parsedDate != null) {
				cal.setTime(parsedDate);
			}
		}

		date = cal.getTime();
	}

	private int convertPeriod(char period) {
		if (period == 'y')
			return Calendar.YEAR;
		if (period == 'm')
			return Calendar.MONTH;
		if (period == 'd')
			return Calendar.DAY_OF_MONTH;
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
