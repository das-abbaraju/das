package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.TranslatableString.Translation;

public class TranslatableStringTest {
	@Test
	public void testFallback_FallbackFromAmericanToGeneralEnglish() throws Exception {
		TranslatableString translatableString = EntityFactory.makeEnglishString("button.color", "Color");
		translatableString.putTranslation("en_GB", "Colour", false);
		translatableString.putTranslation("en_CA", "Colour", false);
		assertEquals("Color", translatableString.toString());
		assertEquals("Color", translatableString.toString(Locale.US));
		assertEquals("Colour", translatableString.toString(Locale.UK));
		assertEquals("Colour", translatableString.toString(Locale.CANADA));
	}
	@Test
	public void testFallback_FallbackFromFrenchToEnglish() throws Exception {
		TranslatableString translatableString = EntityFactory.makeEnglishString("button.color", "Color");
		assertEquals("Color", translatableString.toString(Locale.FRANCE));
		assertEquals("Color", translatableString.toString(Locale.FRENCH));
	}
	@Test
	public void testFallback_FallbackFromAnythingToTheOnlyAvailableTranslation() throws Exception {
		TranslatableString translatableString = new TranslatableString();
		translatableString.setKey("button.color");
		translatableString.putTranslation("zz", "Zyxxyz", false);
		assertEquals("Zyxxyz", translatableString.toString(Locale.US));
		assertEquals("Zyxxyz", translatableString.toString(Locale.UK));
		assertEquals("Zyxxyz", translatableString.toString(Locale.FRANCE));
		assertEquals("Zyxxyz", translatableString.toString(Locale.FRENCH));
		assertEquals("Zyxxyz", translatableString.toString(Locale.CANADA));
		assertEquals("Zyxxyz", translatableString.toString(Locale.CANADA_FRENCH));
	}
	@Test
	public void testFallback_FallbackFromAnythingToTranslationKey() throws Exception {
		TranslatableString translatableString = new TranslatableString();
		translatableString.setKey("button.color");
		// No translations defined at all
		assertEquals("button.color", translatableString.toString(Locale.US));
		assertEquals("button.color", translatableString.toString(Locale.UK));
		assertEquals("button.color", translatableString.toString(Locale.FRANCE));
		assertEquals("button.color", translatableString.toString(Locale.FRENCH));
		assertEquals("button.color", translatableString.toString(Locale.CANADA));
		assertEquals("button.color", translatableString.toString(Locale.CANADA_FRENCH));
	}
	
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
		return EntityFactory.makeEnglishString("hello.world", "Hello World");
	}
}
