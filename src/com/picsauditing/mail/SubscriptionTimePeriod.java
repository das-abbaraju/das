package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.picsauditing.jpa.entities.Translatable;

public enum SubscriptionTimePeriod implements Translatable {
	None, Daily, Weekly, Monthly, Quarterly, Event;

	public Date getComparisonDate() {
		Calendar calendar = Calendar.getInstance();

		switch (this) {
		case Daily:
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			break;
		case Weekly:
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
			break;
		case Monthly:
			calendar.add(Calendar.MONTH, -1);
			break;
		case Quarterly:
			calendar.add(Calendar.MONTH, -3);
			break;
		}

		return calendar.getTime();
	}
	
	public Date getNearestComparisonDate() {
		Calendar calendar = Calendar.getInstance();

		switch (this) {
		case Daily:
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			break;
		case Weekly:
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
			break;
		case Monthly:
			calendar.add(Calendar.MONTH, -1);
			setCalendarToNearestSunday(calendar);
			break;
		case Quarterly:
			calendar.add(Calendar.MONTH, -3);
			setCalendarToNearestSunday(calendar);
			break;
		}

		return calendar.getTime();
	}

	private void setCalendarToNearestSunday(Calendar calendar) {
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			return;

		if (calendar.get(Calendar.DAY_OF_WEEK) >= Calendar.THURSDAY) {
			calendar.add(Calendar.WEEK_OF_MONTH, 1);
			// sets day to nearest Sunday, searching backward.
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		} else {
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		}
	}

	public static ArrayList<SubscriptionTimePeriod> getValuesWithDefault(Subscription subscription) {
		ArrayList<SubscriptionTimePeriod> values = new ArrayList<SubscriptionTimePeriod>();
		SubscriptionTimePeriod[] sTimePeriods = subscription.getSupportedTimePeriods();
		if (sTimePeriods.length > 2) {
			for (SubscriptionTimePeriod sPeriod : sTimePeriods) {
				if (sPeriod.compareTo(SubscriptionTimePeriod.None) > 0)
					values.add(sPeriod);
			}
		}
		return values;
	}

	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
