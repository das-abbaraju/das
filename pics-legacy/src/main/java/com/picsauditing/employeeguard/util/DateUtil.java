package com.picsauditing.employeeguard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

	public static final Date END_OF_TIME = new Date(64060617600000l);

	public static Date explodedToDate(final int year, final int month, final int day) {
		if (year > 0 && month > 0 && day > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month - 1, day);
			return calendar.getTime();
		}

		return null;
	}

	public static int[] dateToExploded(final Date date) {
		if (date != null) {
			int[] exploded = new int[3];
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			exploded[0] = calendar.get(Calendar.YEAR);
			exploded[1] = calendar.get(Calendar.MONTH) + 1;
			exploded[2] = calendar.get(Calendar.DAY_OF_MONTH);

			return exploded;
		}

		return new int[]{};
	}

	public static boolean doesNotExpire(final Date date) {
		if (date == null) {
			return false;
		}

		if (END_OF_TIME.equals(date)) {
			return true;
		}

		Calendar dayBeforeEndOfTime = Calendar.getInstance();
		dayBeforeEndOfTime.setTime(END_OF_TIME);
		dayBeforeEndOfTime.add(Calendar.DAY_OF_YEAR, -1);

		return date.after(dayBeforeEndOfTime.getTime());
	}
}
