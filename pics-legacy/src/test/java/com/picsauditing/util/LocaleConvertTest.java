package com.picsauditing.util;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import org.junit.Test;

public class LocaleConvertTest {

	@Test
	public void testNumber() throws ParseException {
		String myNumberStr = "-1,20345x";
		DecimalFormat form;
		ParsePosition pp = new ParsePosition(0);

		Locale locale = Locale.US;
		assertEquals("English (United States)", locale.getDisplayName());
		form = (DecimalFormat) NumberFormat.getIntegerInstance(locale);
		Number number = form.parse(myNumberStr, pp);

		assertEquals(new Long(-120345), number);
		assertEquals("-120,345", form.format(number));
		assertEquals("Parse position", 8, pp.getIndex());
	}

}
