package com.picsauditing.mail;

import java.util.Calendar;
import java.util.Date;

public enum SubscriptionTimePeriod {
	None, Daily, Weekly, Monthly, Quarterly;
	
	public Date getCompaisonDate() {
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
}
