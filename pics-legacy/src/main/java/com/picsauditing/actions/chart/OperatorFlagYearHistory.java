package com.picsauditing.actions.chart;

import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.util.chart.Category;
import com.picsauditing.util.comparators.CalendarMonthAsStringComparator;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class OperatorFlagYearHistory extends OperatorFlagHistory {
	private static final long serialVersionUID = 568576145565611258L;
	private static final String[] months = new DateFormatSymbols().getShortMonths();

	@Override
	protected String buildSql() throws Exception {
        String statusWhereClause = "";
        if (permissions.getAccountStatus() != AccountStatus.Demo) {
			statusWhereClause = "and a%1$d.status <> '" + AccountStatus.Demo + "'\n";
        }

		StringBuilder sql = new StringBuilder(
				"select DATE_FORMAT(STR_TO_DATE(CONCAT(tmp.label, '-01'), '%Y-%m-%d'), '%b') as 'label',\n"
				+ "fa.flag as 'series', COUNT(tmp.conID) as 'value'\n"
				+ "from (\n");

		// collects data for 12 nearest months
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);

		Date monthCondition;
		for (int i = 0; i < 12; i++) {
			monthCondition = cal.getTime();
			cal.add(Calendar.MONTH, -1);

			// don't count contractors after it was deleted
			sql.append(String.format(
					"select fa%1$d.conID, '%2$tY-%2$tm' as 'label', MAX(fa%1$d.creationDate) as maxDate\n"
					+ "from flag_archive fa%1$d inner join accounts a%1$d on a%1$d.id = fa%1$d.conID\n"
					+ "where fa%1$d.opID = %4$d\n"
					+ statusWhereClause
					+ "and fa%1$d.flag in ('%5$s','%6$s','%7$s')\n"
					+ "and fa%1$d.creationDate < '%3$tY-%3$tm-01'\n"
					+ "and ((a%1$d.status <> '%8$s' and a%1$d.status <> '%9$s') or IFNULL(a%1$d.deactivationDate, a%1$d.updateDate) >= '%2$tY-%2$tm-01')\n"
					+ "group by fa%1$d.conID\n"
					+ "union all\n",
					i, cal.getTime(), monthCondition,
					permissions.getAccountId(), FlagColor.Green, FlagColor.Amber, FlagColor.Red,
					AccountStatus.Deleted, AccountStatus.Deactivated
			));
		}

		sql.setLength(sql.length() - 10);
		sql.append(") as tmp, flag_archive fa\n"
				+ "where fa.opID = " + permissions.getAccountId() + " and fa.conID = tmp.conID and fa.creationDate = tmp.maxDate\n"
				+ "group by tmp.label, fa.flag\n"
				+ "order by tmp.label desc");

		//System.out.println(sql);
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
