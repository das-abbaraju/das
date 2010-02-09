package com.picsauditing.cron;

public enum CronReportingPeriod {
	TenMinutes ( 10 * 60 * 1000L ), 
	ThirtyMinutes ( 30 * 60 * 1000L ), 
	OneHour ( 1 * 60 * 60 * 1000L ),
	TwoHours ( 2 * 60 * 60 * 1000L ), 
	ThreeHours ( 3 * 60 * 60 * 1000L ), 
	SixHours ( 6 * 60 * 60 * 1000L ), 
	TwelveHours ( 12 * 60 * 60 * 1000L ), 
	TwentyFourHours ( 24 * 60 * 60 * 1000L );

	private long period = 0L;
	
	CronReportingPeriod( long period ) {
		this.period = period;
	}

	public long getPeriod() {
		return period;
	}
	
}
