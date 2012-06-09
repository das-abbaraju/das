package com.picsauditing.util;

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
		System.out.print(locale.getDisplayName());
//		form = NumberFormat.getInstance(locale);
		form = (DecimalFormat)NumberFormat.getIntegerInstance(locale);
		Number number = form.parse(myNumberStr,pp);
		
		System.out.println(" -> " + number);
		System.out.print(" -> " + form.format(number) + " parse position: " + pp.getIndex());
		
	}

}
