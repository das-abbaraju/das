package com.picsauditing.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
		return Arrays.asList(new Object[][] {
                {null, null},
                {"", ""},
                {"The quick brown fox jumps over the lazy dog.", "The quick brown fox jumps over the lazy dog."},
                {"<h4>HEADING</h4>", "<h4>HEADING</h4>"},
                {"I said \"hello\" to him.", "I said \"hello\" to him."},
                {"\"four score and seven years ago.\"", "\"four score and seven years ago.\""},
                {TranslationUtil.PAIR_DOUBLE_QUOTE, TranslationUtil.LONE_DOUBLE_QUOTE},
                {"\"<h4>STARTS \"\"QUOTE\"\"</h4>", "<h4>STARTS \"QUOTE\"</h4>"},
                {"<h4>ENDS \"\"QUOTE\"\"</h4>\"", "<h4>ENDS \"QUOTE\"</h4>"},
                {"\"<h4>STARTS AND ENDS \"\"QUOTE\"\"</h4>\"", "<h4>STARTS AND ENDS \"QUOTE\"</h4>"}
        });
	}

	@Test
	public void testTranslationUtil() {
		assertEquals(expectedResult, TranslationUtil.scrubValue(input));
	}
}
