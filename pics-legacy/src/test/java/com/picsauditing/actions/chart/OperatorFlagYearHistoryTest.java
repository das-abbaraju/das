package com.picsauditing.actions.chart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.util.comparators.CalendarMonthAsStringComparator;

public class OperatorFlagYearHistoryTest extends PicsTest {
	private OperatorFlagYearHistory operatorFlagYearHistory;	
	private static final int OPERATOR_ACCOUNT_ID = 1206;
	private static final int MONTHS_IN_YEAR = 12;
	
	@Mock private Permissions permissions;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		super.setUp();
		
		mockTranslationServiceForEnglishMonthNames();
		
		when(permissions.getAccountId()).thenReturn(OPERATOR_ACCOUNT_ID);
		
		operatorFlagYearHistory = new OperatorFlagYearHistory();
		Whitebox.setInternalState(operatorFlagYearHistory, "permissions", permissions);
	}

	/*
	 * Testing the comparator we're going to use in the widget by 
	 * 1. getting that comparator 
	 * 2. using it as the comparator in our own TreeMap
	 * 3. populating it with short name months in arbitrary order
	 * 4. creating our own collection that will be the proper sort order for this month according
	 * 	  to the business rules for this widget
	 * 5. compare them
	 */
	@Test
	public void testComparator() throws Exception {
		CalendarMonthAsStringComparator comparator = Whitebox.invokeMethod(operatorFlagYearHistory, "comparator");
		String[] months = new DateFormatSymbols().getShortMonths();
		
		Map<String, String> categories = new TreeMap<String, String>(comparator);
		categories.put("Aug", "foo");
		categories.put("Jan", "foo");
		categories.put("May", "foo");
		categories.put("Jun", "foo");
		categories.put("Oct", "foo");
		categories.put("Apr", "foo");
		categories.put("Sep", "foo");
		categories.put("Feb", "foo");
		categories.put("Jul", "foo");
		categories.put("Mar", "foo");
		categories.put("Dec", "foo");
		categories.put("Nov", "foo");

		Calendar nextMonthAYearAgo = Calendar.getInstance();
		nextMonthAYearAgo.add(Calendar.MONTH, 1);
		List<String> expected = new ArrayList<String>();
		for(int i = 0, j = nextMonthAYearAgo.get(Calendar.MONTH); i < MONTHS_IN_YEAR; i++, j++) {
			if (j >= MONTHS_IN_YEAR) j = 0;
			expected.add(months[j]);
		}
		Collections.reverse(expected);

		Iterator<String> monthsExpected = categories.keySet().iterator();
		for(String monthExpected : expected) {
			String monthReturned = monthsExpected.next();
			assertEquals(monthExpected, monthReturned);
		}
	}

}
