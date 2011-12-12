package com.picsauditing.util;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.MultiYearScope;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * A utility class that simply helps to keep track of a list of years (e.g. for
 * determining the last three years that contribute to a three year average,
 * which may not be contiguous)
 */
public class YearList {
	List<Integer> yearList;
	
	public YearList() {
		super();
		
		yearList = new ArrayList<Integer>();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void add(Integer year) {
		yearList.add(year);
		
	}
	public void add(String year) {
		yearList.add(new Integer(year));
		
	}
	public void reduceToThreeHighest() {
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
	public int getYearForScope(MultiYearScope scope) {
		int yearOffset = 9999;
		if (scope == MultiYearScope.LastYearOnly) {
			yearOffset = 1;
		} else if (scope == MultiYearScope.TwoYearsAgo) { 
			yearOffset = 2;
		} else if (scope == MultiYearScope.ThreeYearsAgo) { 
			yearOffset = 3;
		}
		
		int yearIndex = yearList.size()-yearOffset;
		if (yearIndex >= 0) {
			return yearList.get(yearIndex);
		}
		return 0;
	}
	public int size() {
		return yearList.size();
	}
	public Integer get(int i) {
		return (Integer)yearList.get(i);
	}



}
