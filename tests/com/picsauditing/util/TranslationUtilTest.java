package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.picsauditing.jpa.entities.Country;

@RunWith(Parameterized.class)
public class TranslationUtilTest {

	private String input;
	private String expectedResult;

	public TranslationUtilTest(String input, String expectedResult) {
		this.input = input;
		this.expectedResult = expectedResult;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> testScrubValueData() {
		return Arrays.asList(new Object[][] { { null, null }, { "", "" },
				{ "The quick brown fox jumps over the lazy dog.", "The quick brown fox jumps over the lazy dog." },
				{ "<h4>HEADING</h4>", "<h4>HEADING</h4>" }, { "I said \"hello\" to him.", "I said \"hello\" to him." },
				{ "\"four score and seven years ago.\"", "\"four score and seven years ago.\"" },
				{ TranslationUtil.PAIR_DOUBLE_QUOTE, TranslationUtil.LONE_DOUBLE_QUOTE },
				{ "\"<h4>STARTS \"\"QUOTE\"\"</h4>", "<h4>STARTS \"QUOTE\"</h4>" },
				{ "<h4>ENDS \"\"QUOTE\"\"</h4>\"", "<h4>ENDS \"QUOTE\"</h4>" },
				{ "\"<h4>STARTS AND ENDS \"\"QUOTE\"\"</h4>\"", "<h4>STARTS AND ENDS \"QUOTE\"</h4>" } });
	}

	@Test
	public void testTranslationUtil() {
		assertEquals(expectedResult, TranslationUtil.scrubValue(input));
	}

	@Test
	public void testIsTranslation_KeyAndTranslationMatch() {
		assertFalse(TranslationUtil.isTranslation(new Country("US"), "name", "Country.US"));
	}

	@Test
	public void testIsTranslation_KeyAndTranslationDoNotMatch() {
		assertTrue(TranslationUtil.isTranslation(new Country("US"), "name", "United States"));
	}

}
