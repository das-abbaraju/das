package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;

import org.junit.Test;

import com.picsauditing.jpa.entities.MultiYearScope;

public class YearListTest {

	
	private YearList yearList = new YearList();

	private void setBaseDate(int year, int month) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 1);
		yearList.setToday(cal.getTime());
	}

	@Test
	public void testMostStraightForwardUseCase() throws Exception {
		setBaseDate(2010, Calendar.JULY);
		yearList.add(2009);
		yearList.add(2008);
		yearList.add(2007);

		assertEquals(2009, yearList.getYearForScope(MultiYearScope.LastYearOnly).intValue());
		assertEquals(2008, yearList.getYearForScope(MultiYearScope.TwoYearsAgo).intValue());
		assertEquals(2007, yearList.getYearForScope(MultiYearScope.ThreeYearsAgo).intValue());
	}

	@Test
	public void testTwoYearBiggerGap() throws Exception {
		setBaseDate(2010, Calendar.JULY);
		yearList.add(2009);
		yearList.add(2006);

		assertEquals(2009, yearList.getYearForScope(MultiYearScope.LastYearOnly).intValue());
		assertNull(yearList.getYearForScope(MultiYearScope.TwoYearsAgo));
		assertNull(yearList.getYearForScope(MultiYearScope.ThreeYearsAgo));
	}

	@Test
	public void testSingleYearRelativeTo2010July() throws Exception {
		setBaseDate(2010, Calendar.JULY);
		yearList.add(2008);

		assertNull(yearList.getYearForScope(MultiYearScope.LastYearOnly));
		assertEquals(2008, yearList.getYearForScope(MultiYearScope.TwoYearsAgo).intValue());
		assertNull(yearList.getYearForScope(MultiYearScope.ThreeYearsAgo));
	}

	@Test
	public void testDuringQ1BeforeSubmission() throws Exception {
		setBaseDate(2012, Calendar.JANUARY);
		yearList.add(2010);
		yearList.add(2009);
		yearList.add(2008);

		assertEquals(2010, yearList.getYearForScope(MultiYearScope.LastYearOnly).intValue());
		assertEquals(2009, yearList.getYearForScope(MultiYearScope.TwoYearsAgo).intValue());
		assertEquals(2008, yearList.getYearForScope(MultiYearScope.ThreeYearsAgo).intValue());
	}

	@Test
	public void testDuringQ1AfterSubmission_4years() throws Exception {
		setBaseDate(2012, Calendar.FEBRUARY);
		yearList.add(2011);
		yearList.add(2010);
		yearList.add(2009);
		yearList.add(2008);

		assertEquals(2011, yearList.getYearForScope(MultiYearScope.LastYearOnly).intValue());
		assertEquals(2010, yearList.getYearForScope(MultiYearScope.TwoYearsAgo).intValue());
		assertEquals(2009, yearList.getYearForScope(MultiYearScope.ThreeYearsAgo).intValue());
	}

	@Test
	public void testDuringQ1AfterSubmission_WithGap() throws Exception {
		setBaseDate(2012, Calendar.FEBRUARY);
		yearList.add(2011);
		yearList.add(2010);
		yearList.add(2008);

		assertEquals(2011, yearList.getYearForScope(MultiYearScope.LastYearOnly).intValue());
		assertEquals(2010, yearList.getYearForScope(MultiYearScope.TwoYearsAgo).intValue());
		assertNull(yearList.getYearForScope(MultiYearScope.ThreeYearsAgo));
	}
	@Test
	public void testDuringQ2NeverSubmitted() throws Exception {
		setBaseDate(2012, Calendar.APRIL);
		yearList.add(2010);
		yearList.add(2009);

		assertNull(yearList.getYearForScope(MultiYearScope.LastYearOnly));
		assertEquals(2010, yearList.getYearForScope(MultiYearScope.TwoYearsAgo).intValue());
		assertEquals(2009, yearList.getYearForScope(MultiYearScope.ThreeYearsAgo).intValue());
	}
	@Test
	public void testDuringQ2AfterSubmission_WithGap() throws Exception {
		setBaseDate(2012, Calendar.APRIL);
		yearList.add(2011);
		yearList.add(2009);

		assertEquals(2011, yearList.getYearForScope(MultiYearScope.LastYearOnly).intValue());
		assertNull(yearList.getYearForScope(MultiYearScope.TwoYearsAgo));
		assertEquals(2009, yearList.getYearForScope(MultiYearScope.ThreeYearsAgo).intValue());
	}

}
