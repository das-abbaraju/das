package com.picsauditing.actions.chart;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.util.chart.Category;
import com.picsauditing.util.comparators.CalendarMonthAsStringComparator;


public class OperatorFlagYearHistory extends OperatorFlagHistory {
	private static final long serialVersionUID = 568576145565611258L;
	private final int MONTHS_IN_YEAR = 12;
	private String[] months = new DateFormatSymbols().getShortMonths();
	
	@Override
	protected String buildSql() throws Exception {
		int operatorID = permissions.getAccountId();
		Date now = new Date();
		StringBuffer sql = new StringBuffer();
		for(int month = 0; month < MONTHS_IN_YEAR; month++) {
			Calendar firstOfMonth = Calendar.getInstance();
			firstOfMonth.setTime(DateBean.getFirstofMonth(now, -month));
			String monthName = months[firstOfMonth.get(Calendar.MONTH)];
			sql.append(getOperatorFlagHistorySQL(firstOfMonth.getTime(), getText("Month.Short."+monthName), operatorID));
			if ((month+1) < MONTHS_IN_YEAR) {
				sql.append(" UNION ");
			}
		}
		return sql.toString();
	}
	
	@Override
	protected void populateChartProperties() {
		super.populateChartProperties();
		populateChartCategoriesProperty();
	}

	/*
	 * 	We need a custom comparator to properly sort the month names
	 */
	private void populateChartCategoriesProperty() {
		CalendarMonthAsStringComparator calendarMonthAsStringComparator = comparator();
		Map<String, Category> categories = new TreeMap<String, Category>(calendarMonthAsStringComparator);
		chart.setCategories(categories);
	}

	/*
	 * 	We're presenting a 12-month rolling window. We want the current month to be the
	 * 	farthest to the left, representing the latest data. Since this is a rolling 12-month,
	 * 	that means the month after this one should be the previous month and go back in time
	 * 	as we go right. "Next Month" in month order will actually be the oldest, one year ago.
	 * 	The query will take care of making it last year's data.
	 * 
	 * 	Example: [May, Apr, Mar, Feb, Jan, Dec, Nov, Oct, Sep, Aug, Jul, Jun]
	 */
	private CalendarMonthAsStringComparator comparator() {
		Calendar nextMonthAYearAgo = Calendar.getInstance();
		nextMonthAYearAgo.add(Calendar.MONTH, 1);
		String startMonth = months[nextMonthAYearAgo.get(Calendar.MONTH)];
		CalendarMonthAsStringComparator calendarMonthAsStringComparator = new CalendarMonthAsStringComparator();
		calendarMonthAsStringComparator.setFirstMonth(startMonth);
		calendarMonthAsStringComparator.setUseShortMonthNames(true);
		calendarMonthAsStringComparator.setReverseSort(true);
		return calendarMonthAsStringComparator;
	}
	
}
