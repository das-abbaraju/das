package com.picsauditing.util.comparators;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

public class CalendarMonthAsStringComparator implements Comparator<String> {
	private String firstMonth = null;
	private boolean reverseSort = false;
	private boolean useShortMonthNames = false;
	
	public void setReverseSort(boolean reverseSort) {
		this.reverseSort = reverseSort;
	}
	
	public void setFirstMonth(String firstMonth) {
		this.firstMonth = firstMonth;
	}

	public void setUseShortMonthNames(boolean useShortMonthNames) {
		this.useShortMonthNames = useShortMonthNames;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int compare(String month0, String month1) {
		List<String> months = new ArrayList<String>();
		if (useShortMonthNames) {
			months.addAll((List<String>)Arrays.asList(shortMonths()));
		} else {
			months.addAll((List<String>)Arrays.asList(months()));
		}
		months = reorderMonthListByFirstMonthIfSet(months);
		
		if (months.indexOf(month0) < months.indexOf(month1)) {
			return (reverseSort) ? 1 : -1;
		} else if (months.indexOf(month0) > months.indexOf(month1)) {
			return (reverseSort) ? -1 : 1;
		}
		return 0;
	}

	private String[] shortMonths() {
		return removeEmptyValueAtEndOfList(new DateFormatSymbols().getShortMonths());
	}

	private String[] months() {
		return removeEmptyValueAtEndOfList(new DateFormatSymbols().getMonths());
	}

    protected String[] removeEmptyValueAtEndOfList(String[] months) {
        int lastIndex = months.length - 1;
        if (months[lastIndex] == null || months[lastIndex].length() <= 0) { 
            String[] monthStrings = new String[lastIndex];
            System.arraycopy(months, 0, monthStrings, 0, lastIndex);
            return monthStrings;
        } else { 
            return months;
        }
    }
	
	protected List<String> reorderMonthListByFirstMonthIfSet(List<String> months) {
		if (firstMonth == null) return months;
		
		List<String> before = new ArrayList<String>();
		List<String> after = new ArrayList<String>();
		boolean isBefore = true;
		for(String month : months) {
			if (firstMonth.equals(month)) {
				isBefore = false;
			}
			if (isBefore) { 
				before.add(month);
			} else {
				after.add(month);
			}
		}
		after.addAll(before);
		return after;
	}
	
}
