package com.picsauditing.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.picsauditing.jpa.entities.MultiYearScope;

/**
 * A utility class that simply helps to keep track of a list of years (e.g. for
 * determining the last three years that contribute to a three year average,
 * which may not be contiguous)
 */
public class YearList {
	List<Integer> yearList;
	Date referenceDate = new Date();

	public YearList() {
		super();

		yearList = new ArrayList<Integer>();
	}

	public void setToday(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void add(Integer year) {
		yearList.add(year);
		reduceToThreeHighest();
	}

	public void add(String year) {
		add(new Integer(year));
	}

	private void reduceToThreeHighest() {
		Collections.sort(yearList);
		if (yearList.size() > 3) {
			yearList = yearList.subList(yearList.size() - 3, yearList.size());
		}
	}

	public boolean contains(Integer year) {
		return yearList.contains(year);
	}

	public boolean contains(String year) {
		return yearList.contains(new Integer(year));
	}

	public Integer getYearForScope(MultiYearScope scope) {
		int lastYear = referenceYear() - 1;

		if (!yearList.contains(lastYear) && isGracePeriod()) {
				lastYear--;
		}

		if (scope == MultiYearScope.LastYearOnly) {
			return (yearList.contains(lastYear)) ? lastYear : null;
		}
		if (scope == MultiYearScope.TwoYearsAgo) {
			return (yearList.contains(lastYear - 1)) ? lastYear - 1 : null;
		}
		if (scope == MultiYearScope.ThreeYearsAgo) {
			return (yearList.contains(lastYear - 2)) ? lastYear - 2 : null;
		}

		return null;
	}

	private boolean isGracePeriod() {
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(referenceDate);
		boolean isGracePeriod = cal2.get(Calendar.MONTH) < Calendar.APRIL;
		return isGracePeriod;
	}

	private int referenceYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(referenceDate);
		int referenceYear = cal.get(Calendar.YEAR);
		return referenceYear;
	}

	public int size() {
		return yearList.size();
	}

}
