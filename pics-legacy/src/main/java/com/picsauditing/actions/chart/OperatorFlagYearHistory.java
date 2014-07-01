package com.picsauditing.actions.chart;

import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.util.chart.Category;
import com.picsauditing.util.comparators.CalendarMonthAsStringComparator;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;


public class OperatorFlagYearHistory extends OperatorFlagHistory {
	private static final long serialVersionUID = 568576145565611258L;
	private String[] months = new DateFormatSymbols().getShortMonths();
	
	@Override
	protected String buildSql() throws Exception {
        String statusWhereClause = "";
        if (permissions.getAccountStatus() != AccountStatus.Demo) {
            statusWhereClause = "and a.status <> 'Demo'\n";
        }

        return "select DATE_FORMAT(tmp.maxDate, '%b') as 'label', tmp.flag as 'series', count(tmp.conID) as 'value'\n" +
                "from (select fa.conID, fa.flag, DATE_FORMAT(fa.creationDate, '%b') as 'label', max(fa.creationDate) as maxDate\n" +
                "from flag_archive fa\n" +
                "join accounts a on a.id = fa.conID\n" +
                "where fa.opID = " + permissions.getAccountId() + "\n" +
                statusWhereClause +
                "and fa.flag in ('Green', 'Amber', 'Red')\n" +
                "group by fa.conID, label\n" +
                ") as tmp\n" +
                "group by label, series\n" +
                "order by DATE_FORMAT(tmp.maxDate, '%y') desc, DATE_FORMAT(tmp.maxDate, '%m') desc\n" +
                "limit 36";
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
