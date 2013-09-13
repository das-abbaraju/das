package com.picsauditing.util.comparators;

import static org.junit.Assert.*;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.picsauditing.util.chart.Category;

import edu.emory.mathcs.backport.java.util.Arrays;


public class CalendarMonthAsStringComparatorTest {
	CalendarMonthAsStringComparator calendarMonthAsStringComparator;
	
	@Before
	public void setUp() throws Exception {
		calendarMonthAsStringComparator = new CalendarMonthAsStringComparator();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testReorderMonthListByFirstMonth_LongMonthNameMarchFirstInOrder() throws Exception {
		List<String> months = new ArrayList<String>();
		months.addAll(Arrays.asList((Object[]) Whitebox.invokeMethod(calendarMonthAsStringComparator, "months")));
				
		calendarMonthAsStringComparator.setFirstMonth("March");
		List<String> newMonthOrder = calendarMonthAsStringComparator.reorderMonthListByFirstMonthIfSet(months);
		assertEquals("March", newMonthOrder.get(0));
		assertEquals("April", newMonthOrder.get(1));
		assertEquals("May", newMonthOrder.get(2));
		assertEquals("June", newMonthOrder.get(3));
		assertEquals("July", newMonthOrder.get(4));
		assertEquals("August", newMonthOrder.get(5));
		assertEquals("September", newMonthOrder.get(6));
		assertEquals("October", newMonthOrder.get(7));
		assertEquals("November", newMonthOrder.get(8));
		assertEquals("December", newMonthOrder.get(9));
		assertEquals("January", newMonthOrder.get(10));
		assertEquals("February", newMonthOrder.get(11));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testReorderMonthListByFirstMonth_LongMonthNameJuneFirstInOrder() throws Exception {
		List<String> months = new ArrayList<String>();
		months.addAll(Arrays.asList((Object[]) Whitebox.invokeMethod(calendarMonthAsStringComparator, "months")));
				
		calendarMonthAsStringComparator.setFirstMonth("June");
		List<String> newMonthOrder = calendarMonthAsStringComparator.reorderMonthListByFirstMonthIfSet(months);
		assertEquals("June", newMonthOrder.get(0));
		assertEquals("July", newMonthOrder.get(1));
		assertEquals("August", newMonthOrder.get(2));
		assertEquals("September", newMonthOrder.get(3));
		assertEquals("October", newMonthOrder.get(4));
		assertEquals("November", newMonthOrder.get(5));
		assertEquals("December", newMonthOrder.get(6));
		assertEquals("January", newMonthOrder.get(7));
		assertEquals("February", newMonthOrder.get(8));
		assertEquals("March", newMonthOrder.get(9));
		assertEquals("April", newMonthOrder.get(10));
		assertEquals("May", newMonthOrder.get(11));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReorderMonthListByFirstMonth_ShortMonthNameSepFirstInOrder() throws Exception {
		List<String> months = new ArrayList<String>();
		months.addAll(Arrays.asList((Object[]) Whitebox.invokeMethod(calendarMonthAsStringComparator, "shortMonths")));
				
		calendarMonthAsStringComparator.setFirstMonth("Sep");
		List<String> newMonthOrder = calendarMonthAsStringComparator.reorderMonthListByFirstMonthIfSet(months);
		assertEquals("Sep", newMonthOrder.get(0));
		assertEquals("Oct", newMonthOrder.get(1));
		assertEquals("Nov", newMonthOrder.get(2));
		assertEquals("Dec", newMonthOrder.get(3));
		assertEquals("Jan", newMonthOrder.get(4));
		assertEquals("Feb", newMonthOrder.get(5));
		assertEquals("Mar", newMonthOrder.get(6));
		assertEquals("Apr", newMonthOrder.get(7));
		assertEquals("May", newMonthOrder.get(8));
		assertEquals("Jun", newMonthOrder.get(9));
		assertEquals("Jul", newMonthOrder.get(10));
		assertEquals("Aug", newMonthOrder.get(11));

	}
	
	@Test
	public void testCompare_TestAsComparatorForTreeMap() throws Exception {
		calendarMonthAsStringComparator.setFirstMonth("June");

		Map<String, String> categories = new TreeMap<String, String>(calendarMonthAsStringComparator);
		
		categories.put("August", "foo");
		categories.put("January", "foo");
		categories.put("May", "foo");
		categories.put("June", "foo");
		categories.put("October", "foo");
		categories.put("April", "foo");
		categories.put("September", "foo");
		categories.put("February", "foo");
		categories.put("July", "foo");
		categories.put("March", "foo");
		categories.put("December", "foo");
		categories.put("November", "foo");

		String[] expected = {"June", "July", "August", "September", "October", "November", "December", "January", "February", "March", "April", "May"};

		int i = 0;
		for(String month : categories.keySet()) {
			assertEquals(expected[i], month);
			i++;
		}
	}

	@Test
	public void testCompare_TestAsComparatorForTreeMap_ReverseSort_JuneFirstMonth() throws Exception {
		calendarMonthAsStringComparator.setFirstMonth("June");
		calendarMonthAsStringComparator.setReverseSort(true);
		
		Map<String, String> categories = new TreeMap<String, String>(calendarMonthAsStringComparator);
		
		categories.put("August", "foo");
		categories.put("January", "foo");
		categories.put("May", "foo");
		categories.put("June", "foo");
		categories.put("October", "foo");
		categories.put("April", "foo");
		categories.put("September", "foo");
		categories.put("February", "foo");
		categories.put("July", "foo");
		categories.put("March", "foo");
		categories.put("December", "foo");
		categories.put("November", "foo");

		String[] expected = {"May", "April", "March", "February", "January", "December", "November", "October", "September", "August", "July", "June"};

		int i = 0;
		for(String month : categories.keySet()) {
			assertEquals(expected[i], month);
			i++;
		}
	}

	
	@Test
	public void testCompare_TestAsComparatorForTreeMap_ReverseSort_MayFirstMonth() throws Exception {
		calendarMonthAsStringComparator.setFirstMonth("May");
		calendarMonthAsStringComparator.setReverseSort(true);

		Map<String, String> categories = new TreeMap<String, String>(calendarMonthAsStringComparator);
		
		categories.put("August", "foo");
		categories.put("January", "foo");
		categories.put("May", "foo");
		categories.put("June", "foo");
		categories.put("October", "foo");
		categories.put("April", "foo");
		categories.put("September", "foo");
		categories.put("February", "foo");
		categories.put("July", "foo");
		categories.put("March", "foo");
		categories.put("December", "foo");
		categories.put("November", "foo");

		String[] foo = {"April", "March", "February", "January", "December", "November", "October", "September", "August", "July", "June", "May"};

		int i = 0;
		for(String month : categories.keySet()) {
			assertTrue(month.equals(foo[i]));
			i++;
		}
	}
	
	@Test
	public void testCompare_MayFirstMonthReversSort() throws Exception {
		calendarMonthAsStringComparator.setFirstMonth("May");
		calendarMonthAsStringComparator.setReverseSort(true);
		
		assertEquals(0, calendarMonthAsStringComparator.compare("May", "May"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "June"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "July"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "August"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "September"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "October"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "November"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "December"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "January"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "February"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "March"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "April"));
		
		assertEquals(1, calendarMonthAsStringComparator.compare("June", "July"));
	}

	
	@Test
	public void testCompare_MayFirstMonth() throws Exception {
		calendarMonthAsStringComparator.setFirstMonth("May");
		
		assertEquals(0, calendarMonthAsStringComparator.compare("May", "May"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "June"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "July"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "August"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "September"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "October"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "November"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "December"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "January"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "February"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "March"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "April"));
	}
	
	@Test
	public void testCompare_Jan() throws Exception {
		calendarMonthAsStringComparator.setUseShortMonthNames(true);
		
		assertEquals(0, calendarMonthAsStringComparator.compare("Jan", "Jan"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("Jan", "Feb"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("Jan", "Mar"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("Jan", "Apr"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("Jan", "Jun"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("Jan", "Jul"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("Jan", "Aug"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("Jan", "Sep"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("Jan", "Oct"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("Jan", "Nov"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("Jan", "Dec"));
	}
	
	@Test
	public void testCompare_January() throws Exception {
		assertEquals(0, calendarMonthAsStringComparator.compare("January", "January"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("January", "February"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("January", "March"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("January", "April"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("January", "June"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("January", "July"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("January", "August"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("January", "September"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("January", "October"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("January", "November"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("January", "December"));
	}
	
	@Test
	public void testCompare_JanuaryReverse() throws Exception {
		calendarMonthAsStringComparator.setReverseSort(true);
		
		assertEquals(0, calendarMonthAsStringComparator.compare("January", "January"));
		assertEquals(1, calendarMonthAsStringComparator.compare("January", "February"));
		assertEquals(1, calendarMonthAsStringComparator.compare("January", "March"));
		assertEquals(1, calendarMonthAsStringComparator.compare("January", "April"));
		assertEquals(1, calendarMonthAsStringComparator.compare("January", "June"));
		assertEquals(1, calendarMonthAsStringComparator.compare("January", "July"));
		assertEquals(1, calendarMonthAsStringComparator.compare("January", "August"));
		assertEquals(1, calendarMonthAsStringComparator.compare("January", "September"));
		assertEquals(1, calendarMonthAsStringComparator.compare("January", "October"));
		assertEquals(1, calendarMonthAsStringComparator.compare("January", "November"));
		assertEquals(1, calendarMonthAsStringComparator.compare("January", "December"));
	}
	
	@Test
	public void testCompare_February() throws Exception {
		assertEquals(1, calendarMonthAsStringComparator.compare("February", "January"));
		assertEquals(0, calendarMonthAsStringComparator.compare("February", "February"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("February", "March"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("February", "April"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("February", "June"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("February", "July"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("February", "August"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("February", "September"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("February", "October"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("February", "November"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("February", "December"));
	}
	
	@Test
	public void testCompare_March() throws Exception {
		assertEquals(1, calendarMonthAsStringComparator.compare("March", "January"));
		assertEquals(1, calendarMonthAsStringComparator.compare("March", "February"));
		assertEquals(0, calendarMonthAsStringComparator.compare("March", "March"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("March", "April"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("March", "June"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("March", "July"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("March", "August"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("March", "September"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("March", "October"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("March", "November"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("March", "December"));
	}
	
	@Test
	public void testCompare_May_Short() throws Exception {
		calendarMonthAsStringComparator.setUseShortMonthNames(true);
		
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "Jan"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "Feb"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "Mar"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "Apr"));
		assertEquals(0, calendarMonthAsStringComparator.compare("May", "May"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "Jun"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "Jul"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "Aug"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "Sep"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "Oct"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "Nov"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "Dec"));
	}
	
	@Test
	public void testCompare_May_Long() throws Exception {
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "January"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "February"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "March"));
		assertEquals(1, calendarMonthAsStringComparator.compare("May", "April"));
		assertEquals(0, calendarMonthAsStringComparator.compare("May", "May"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "June"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "July"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "August"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "September"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "October"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "November"));
		assertEquals(-1, calendarMonthAsStringComparator.compare("May", "December"));
	}
}
