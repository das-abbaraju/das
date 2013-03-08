package com.picsauditing.validator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.validator.InputValidator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InputValidatorTest {

	private InputValidator inputValidator;

	@Mock
	ContractorAccountDAO contractorAccountDao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		inputValidator = new InputValidator();

		Whitebox.setInternalState(inputValidator, "contractorAccountDao", contractorAccountDao);
	}

	@Test
	public void testIsCompanyNameTaken_NullResultReturnsFalse() {
		when(contractorAccountDao.findByCompanyName(anyString())).thenReturn(null);

		boolean result = inputValidator.isCompanyNameTaken("MY COMPANY");

		assertFalse(result);
	}

	@Test
	public void testIsCompanyNameTaken_EmptyResultReturnsFalse() {
		List<ContractorAccount> emptyAccounts = new ArrayList<ContractorAccount>();
		when(contractorAccountDao.findByCompanyName(anyString())).thenReturn(emptyAccounts);

		boolean result = inputValidator.isCompanyNameTaken("MY COMPANY");

		assertFalse(result);
	}

	@Test
	public void testIsCompanyNameTaken_NonEmptyResultReturnsTrue() {
		List<ContractorAccount> accounts = new ArrayList<ContractorAccount>();
		accounts.add(new ContractorAccount());
		when(contractorAccountDao.findByCompanyName(anyString())).thenReturn(accounts);

		boolean result = inputValidator.isCompanyNameTaken("MY COMPANY");

		assertTrue(result);
	}

	@Test
	public void testContainsOnlySafeCharacters_NullReturnsFalse() {
		boolean result = inputValidator.containsOnlySafeCharacters(null);

		assertFalse(result);
	}

	@Test
	public void testContainsOnlySafeCharacters_EmptyStringReturnsTrue() {
		boolean result = inputValidator.containsOnlySafeCharacters("");

		assertTrue(result);
	}

	@Test
	public void testContainsOnlySafeCharacters_LeftAngleBracketReturnsFalse() {
		boolean result = inputValidator.containsOnlySafeCharacters("<");

		assertFalse(result);
	}

	@Test
	public void testContainsOnlySafeCharacters_RightAngleBracketReturnsFalse() {
		boolean result = inputValidator.containsOnlySafeCharacters(">");

		assertFalse(result);
	}

	@Test
	public void testContainsOnlySafeCharacters_BacktickReturnsFalse() {
		boolean result = inputValidator.containsOnlySafeCharacters("`");

		assertFalse(result);
	}

	@Test
	public void testContainsOnlySafeCharacters_DoubleQuoteReturnsFalse() {
		boolean result = inputValidator.containsOnlySafeCharacters("\"");

		assertFalse(result);
	}

	@Test
	public void testContainsOnlySafeCharacters_semicolonReturnsFalse() {
		assertFalse(inputValidator.containsOnlySafeCharacters("foo;bar"));
	}

	@Test
	public void testValidateUsername_Null_ReturnsNotEmpty() {
		String errorMessageKey = inputValidator.validateUsername(null);

		assertNotSame(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateUsername_EmptyString_ReturnsNotEmpty() {
		String errorMessageKey = inputValidator.validateUsername("");

		assertNotSame(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateUsername_EmailAddress_ReturnsEmpty() {
		String errorMessageKey = inputValidator.validateUsername("me@here.com");

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateUsername_UnderscoreDashAndPlusAreValid() {
		String errorMessageKey = inputValidator.validateUsername("abc-_+");

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateUsername_SingleCharacter_ReturnsEmpty() {
		String errorMessageKey = inputValidator.validateUsername("1");

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateUsername_FiveCharacters_ReturnsEmpty() {
		String errorMessageKey = inputValidator.validateUsername("12345");

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateUsername_OneHundredCharacters_ReturnsEmpty() {
		String string100CharactersLong =
				"0123456789" + // 1
				"0123456789" + // 2
				"0123456789" + // 3
				"0123456789" + // 4
				"0123456789" + // 5
				"0123456789" + // 6
				"0123456789" + // 7
				"0123456789" + // 8
				"0123456789" + // 9
				"0123456789"; // 10

		String errorMessageKey = inputValidator.validateUsername(string100CharactersLong);

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateUsername_OverOneHundredCharacters_ReturnsNotEmpty() {
		String string101CharactersLong =
				"0123456789" + // 1
				"0123456789" + // 2
				"0123456789" + // 3
				"0123456789" + // 4
				"0123456789" + // 5
				"0123456789" + // 6
				"0123456789" + // 7
				"0123456789" + // 8
				"0123456789" + // 9
				"01234567890"; // 10

		String errorMessageKey = inputValidator.validateUsername(string101CharactersLong);

		assertNotSame(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateUsername_InvalidCharactersReturnFalse() {
		String[] invalidChars = {
				"`", "~", "!", "#", "$", "%", "^", "&", "*", "(", ")",
				"=", "[", "]", "{", "}", "\\", "|", ":", ";", "'", "\"",
				",", "<", ">", "?", "/"
		};

		for (String invalidChar : invalidChars) {
			String errorMessageKey = inputValidator.validateUsername("abcd" + invalidChar);
			assertNotSame(invalidChar + " should be an invalid character for a username.", "", errorMessageKey);
		}
	}

	@Test
	public void testValidateDate_NullDateNotRequired_ReturnsNoError() {
		Date date = null;

		String errorMessageKey = inputValidator.validateDate(date, false);

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateDate_NullDateRequiredByDefault_ReturnsError() {
		Date date = null;

		String errorMessageKey = inputValidator.validateDate(date);

		assertNotSame(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateDate_Day1_ReturnsNoError() {
		Date date = buildValidDate();
		date.setDate(1);

		String errorMessageKey = inputValidator.validateDate(date);

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateDate_Day31_ReturnsNoError() {
		Date date = buildValidDate();
		date.setDate(31);

		String errorMessageKey = inputValidator.validateDate(date);

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateDate_Month0_ReturnsNoError() {
		Date date = buildValidDate();
		date.setMonth(0);

		String errorMessageKey = inputValidator.validateDate(date);

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateDate_Month11_ReturnsNoError() {
		Date date = buildValidDate();
		date.setMonth(11);

		String errorMessageKey = inputValidator.validateDate(date);

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateDate_Year999_ReturnsError() {
		Date date = buildValidDate();
		date.setYear(999 - 1900);

		String errorMessageKey = inputValidator.validateDate(date);

		assertNotSame(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateDate_Year1000_ReturnsNoError() {
		Date date = buildValidDate();
		date.setYear(1000 - 1900);

		String errorMessageKey = inputValidator.validateDate(date);

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateDate_Year9999_ReturnsNoError() {
		Date date = buildValidDate();
		date.setYear(9999 - 1900);

		String errorMessageKey = inputValidator.validateDate(date);

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateDate_Year10000_ReturnsError() {
		Date date = buildValidDate();
		date.setYear(10000 - 1900);

		String errorMessageKey = inputValidator.validateDate(date);

		assertNotSame(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateLocale_NullLocaleNotRequired_ReturnsNoError() {
		Locale locale = null;

		String result = inputValidator.validateLocale(locale, false);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidateLocale_NullLocale_ReturnsRequiredKey() {
		Locale locale = null;

		String result = inputValidator.validateLocale(locale);

		assertEquals(InputValidator.REQUIRED_KEY, result);
	}

	@Test
	public void testValidateLocale_BlankLocale_ReturnsRequiredKey() {
		Locale locale = new Locale("");

		String result = inputValidator.validateLocale(locale);

		assertEquals(InputValidator.REQUIRED_KEY, result);
	}

	@Test
	public void testValidateLocale_EnglishLocale_ReturnsNoError() {
		Locale locale = Locale.ENGLISH;

		String result = inputValidator.validateLocale(locale);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePhoneNumber_ExtensionWorks() {
		String phoneNumber = "1-800-123-1234 x1234";

		String result = inputValidator.validatePhoneNumber(phoneNumber);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	// FIXME This is a test to verify a kludge for PICS-8838
	@Test
	public void testValidatePhoneNumber_AsteriskOk() {
		String phoneNumber = "1-800-123-1234 *1234";

		String result = inputValidator.validatePhoneNumber(phoneNumber);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	private Date buildValidDate() {
		Date date = new Date();
		date.setDate(1);
		date.setMonth(0);
		date.setYear(2000);

		return date;
	}

}
