package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.picsauditing.jpa.entities.TranslatableString.Translation;

public class TranslatableStringTest {
	@Test
	public void testSaveInsertedTranslation() throws Exception {
		TranslatableString test = setUpTranslatableString();

		for (Translation translation : test.getTranslations()) {
			assertTrue(translation.isInsert());
		}
	}

	@Test
	public void testSaveModifiedTranslation() throws Exception {
		TranslatableString test = setUpTranslatableString();
		test.handleTranslation(Locale.ENGLISH, "Hello Galaxy");

		for (Translation translation : test.getTranslations()) {
			assertTrue(translation.isModified());
		}
	}

	@Test
	public void testDeleteTranslation() throws Exception {
		TranslatableString test = setUpTranslatableString();
		test.handleTranslation(Locale.ENGLISH, "");

		for (Translation translation : test.getTranslations()) {
			assertTrue(translation.isDelete());
		}
	}

	private TranslatableString setUpTranslatableString() {
		TranslatableString translatableString = new TranslatableString();
		translatableString.putTranslation("en", "Hello World", true);
		return translatableString;
	}
}
