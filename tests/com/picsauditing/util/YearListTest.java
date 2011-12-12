package com.picsauditing.util;

import com.picsauditing.jpa.entities.MultiYearScope;

import junit.framework.TestCase;

public class YearListTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testReduceToThreeHighest_5() throws Exception {
		YearList yearList = new YearList();
		yearList.add(2004);
		yearList.add(2001);
		yearList.add(2000);
		yearList.add(2011);
		yearList.add(2010);
		
		yearList.reduceToThreeHighest();
		assertEquals(3,yearList.size());
		assertEquals(2004,yearList.get(0).intValue());
		assertEquals(2010,yearList.get(1).intValue());
		assertEquals(2011,yearList.get(2).intValue());
		
	}
	public void testReduceToThreeHighest_3() throws Exception {
		YearList yearList = new YearList();
		yearList.add(2004);
		yearList.add(2011);
		yearList.add(2010);
		
		yearList.reduceToThreeHighest();
		assertEquals(3,yearList.size());
		assertEquals(2004,yearList.get(0).intValue());
		assertEquals(2010,yearList.get(1).intValue());
		assertEquals(2011,yearList.get(2).intValue());
		
	}
	public void testReduceToThreeHighest_2() throws Exception {
		YearList yearList = new YearList();
		yearList.add(2004);
		yearList.add(2010);
		
		yearList.reduceToThreeHighest();
		assertEquals(2,yearList.size());
		assertEquals(2004,yearList.get(0).intValue());
		assertEquals(2010,yearList.get(1).intValue());
		
	}
	public void testReduceToThreeHighest_1() throws Exception {
		YearList yearList = new YearList();
		yearList.add(2004);
		
		yearList.reduceToThreeHighest();
		assertEquals(1,yearList.size());
		assertEquals(2004,yearList.get(0).intValue());
		
	}
	public void testReduceToThreeHighest_Empty() throws Exception {
		YearList yearList = new YearList();
		
		yearList.reduceToThreeHighest();
		assertEquals(0,yearList.size());
	}
	public void testGetYearForScope_OverThreeYears() throws Exception {
		YearList yearList = new YearList();
		yearList.add(2004);
		yearList.add(2011);
		yearList.add(2010);
		
		yearList.reduceToThreeHighest();
		assertEquals(3,yearList.size());
		assertEquals(2011,yearList.getYearForScope(MultiYearScope.LastYearOnly));
		assertEquals(2010,yearList.getYearForScope(MultiYearScope.TwoYearsAgo));
		assertEquals(2004,yearList.getYearForScope(MultiYearScope.ThreeYearsAgo));
		assertEquals(0,yearList.getYearForScope(MultiYearScope.ThreeYearAverage));
	}
	public void testGetYearForScope_OverTwoYears() throws Exception {
		YearList yearList = new YearList();
		yearList.add(2004);
		yearList.add(2010);
		
		yearList.reduceToThreeHighest();
		assertEquals(2,yearList.size());
		assertEquals(2010,yearList.getYearForScope(MultiYearScope.LastYearOnly));
		assertEquals(2004,yearList.getYearForScope(MultiYearScope.TwoYearsAgo));
		assertEquals(0,yearList.getYearForScope(MultiYearScope.ThreeYearsAgo));
		assertEquals(0,yearList.getYearForScope(MultiYearScope.ThreeYearAverage));
	}
	public void testGetYearForScope_OverOneYear() throws Exception {
		YearList yearList = new YearList();
		yearList.add(2010);
		
		yearList.reduceToThreeHighest();
		assertEquals(1,yearList.size());
		assertEquals(2010,yearList.getYearForScope(MultiYearScope.LastYearOnly));
		assertEquals(0,yearList.getYearForScope(MultiYearScope.TwoYearsAgo));
		assertEquals(0,yearList.getYearForScope(MultiYearScope.ThreeYearsAgo));
		assertEquals(0,yearList.getYearForScope(MultiYearScope.ThreeYearAverage));
	}

}
