package com.picsauditing.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.junit.Test;

public class LocaleConvertTest {
	@Test
	public void testNumber() throws ParseException {
		String myNumberStr = "00123453210,623";
		NumberFormat form;
		Locale locale = Locale.GERMANY;
		System.out.print(locale.getDisplayName());
		form = NumberFormat.getInstance(locale);
//		form = NumberFormat.getIntegerInstance(locale);
//		form = NumberFormat.getCurrencyInstance(locale);
//		if (form instanceof DecimalFormat) {
//			System.out.print(": " + ((DecimalFormat) form).toPattern());
//		}
		try {
			Number number = form.parse(myNumberStr);
			
			System.out.println(" -> " + number);
			System.out.print(" -> " + form.format(number));
		} catch (ParseException e) {
			System.out.println("Parse Exception");
		}
		
	}

}
