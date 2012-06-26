package com.picsauditing.report.fields;

import java.util.Calendar;
import java.util.Date;

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

		char periodChar = value.charAt(value.length() - 1);
		String valueWithoutPeriod = value.substring(0, value.length() - 1);
		Calendar cal = Calendar.getInstance();

		try {
			int amount = Integer.parseInt(valueWithoutPeriod);

			if (!plusTime) {
				amount *= -1;
			}
			int period = convertPeriod(periodChar);
			cal.add(period, amount);

			date = cal.getTime();
		} catch (NumberFormatException nfe) {
			Date parsedDate = DateBean.parseDate(value);
			if (parsedDate != null) {
				cal.setTime(parsedDate);
				date = cal.getTime();
			}
			else
			{
				date = null;
			}
		}

	}

	private int convertPeriod(char period) {
		period = Character.toUpperCase(period);
		switch (period) {
		case 'Y':
			return Calendar.YEAR;
		case 'M':
			return Calendar.MONTH;
		case 'D':
			return Calendar.DAY_OF_MONTH;
		case 'W':
			return Calendar.WEEK_OF_YEAR;
		case 'H':
			return Calendar.HOUR_OF_DAY;
		default:
			return Calendar.DAY_OF_YEAR;
		}
	}

	public Date getTime() {
		return date;
	}
}
