package com.picsauditing.model.i18n;

import org.junit.Test;

public class LlewellynTranslatableStringTest {

	@Test
	public void testName() throws Exception {
		System.out.println(new LlewellynTranslatableString("Country.US").toTranslatedString());
	}

}
