package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public enum SubscriptionTimePeriod {
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
	
	public static ArrayList<SubscriptionTimePeriod> getValuesWithDefault(Subscription subscription) {
		ArrayList<SubscriptionTimePeriod> values = new ArrayList<SubscriptionTimePeriod>();
		SubscriptionTimePeriod[] sTimePeriods = subscription.getSupportedTimePeriods();
		if(sTimePeriods.length > 2) {
			for(SubscriptionTimePeriod sPeriod : sTimePeriods) {
				if(sPeriod.compareTo(SubscriptionTimePeriod.None) > 0)
					values.add(sPeriod);
			}
		}
		return values;
	}
}
