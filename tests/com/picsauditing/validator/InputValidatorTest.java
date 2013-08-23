package com.picsauditing.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.model.i18n.KeyValue;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.util.Strings;

public class InputValidatorTest {

	private InputValidator inputValidator;

	@Mock
	ContractorAccountDAO contractorAccountDao;
	@Mock
	LanguageModel supportedLanguages;

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
	public void testValidateCompanyName_LessThanTwoCharacters_ReturnsNotEmpty() {
		String errorMessageKey = inputValidator.validateCompanyName("Q");

		assertNotSame(InputValidator.NO_ERROR, errorMessageKey);
	}

	@Test
	public void testValidateCompanyName_ChecksIfCompanyNameExists() {
		InputValidator inputValidatorSpy = spy(inputValidator);
		String companyName = "ABC Company";

		inputValidatorSpy.validateCompanyName(companyName);

		verify(inputValidatorSpy).isCompanyNameTaken(companyName);
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
		String string100CharactersLong = "0123456789" + // 1
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
		String string101CharactersLong = "0123456789" + // 1
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
		String[] invalidChars = { "`", "~", "!", "#", "$", "%", "^", "&", "*", "(", ")", "=", "[", "]", "{", "}", "\\",
				"|", ":", ";", "'", "\"", ",", "<", ">", "?", "/" };

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
	public void testIsLanguageValid_WhenLanguageIsNull_ThenReturnFalse() {
		String language = null;

		boolean result = inputValidator.isLanguageValid(language, supportedLanguages);

		assertFalse(result);
	}

	@Test
	public void testIsLanguageValid_WhenLanguageIsEmpty_ThenReturnFalse() {
		String language = Strings.EMPTY_STRING;

		boolean result = inputValidator.isLanguageValid(language, supportedLanguages);

		assertFalse(result);
	}

	@Test
	public void testIsLanguageValid_WhenSupportedLanguagesIsNull_ThenReturnFalse() {
		String language = Locale.ENGLISH.getLanguage();

		boolean result = inputValidator.isLanguageValid(language, null);

		assertFalse(result);
	}

	@Test
	public void testIsLanguageValid_WhenStableLanguagesAreNull_ThenReturnFalse() {
		String language = Locale.ENGLISH.getLanguage();
		when(supportedLanguages.getVisibleLanguagesSansDialect()).thenReturn(null);

		boolean result = inputValidator.isLanguageValid(language, supportedLanguages);

		assertFalse(result);
	}

	@Test
	public void testIsLanguageValid_WhenNoStableLanguages_ThenReturnFalse() {
		String language = Locale.ENGLISH.getLanguage();
		when(supportedLanguages.getVisibleLanguagesSansDialect()).thenReturn(new ArrayList<KeyValue<String, String>>());

		boolean result = inputValidator.isLanguageValid(language, supportedLanguages);

		assertFalse(result);
	}

	@Test
	public void testIsLanguageValid_WhenLanguageIsInStableLanguages_ThenReturnTrue() {
		String language = Locale.ENGLISH.getLanguage();
		List<KeyValue<String, String>> stableLanguages = new ArrayList<>();
		stableLanguages.add(new KeyValue<String, String>(language, Strings.EMPTY_STRING));
		when(supportedLanguages.getVisibleLanguagesSansDialect()).thenReturn(stableLanguages);

		boolean result = inputValidator.isLanguageValid(language, supportedLanguages);

		assertTrue(result);
	}

	@Test
	public void testIsLanguageValid_WhenLanguageIsNotInStableLanguages_ThenReturnFalse() {
		String languageEnglish = Locale.ENGLISH.getLanguage();
		String languageFrench = Locale.FRENCH.getLanguage();
		List<KeyValue<String, String>> stableLanguages = new ArrayList<>();
		stableLanguages.add(new KeyValue<String, String>(languageFrench, Strings.EMPTY_STRING));
		when(supportedLanguages.getVisibleLanguagesSansDialect()).thenReturn(stableLanguages);

		boolean result = inputValidator.isLanguageValid(languageEnglish, supportedLanguages);

		assertFalse(result);
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

	@Test
	public void testValidatePhoneNumber_ExtentionOnly() {
		String phoneNumber = "1234";

		String result = inputValidator.validatePicsCSRPhoneNumber(phoneNumber);

		assertEquals(InputValidator.NO_ERROR, result);

//		System.out.println(phoneNumber.matches("^[0-9]{4}$"));

	}

	@Test
	public void testValidatePhoneNumber_FullNumber() {
		String phoneNumber = "949-936-4512 x 1234";

		String result = inputValidator.validatePicsCSRPhoneNumber(phoneNumber);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePhoneNumber_Invalid() {
		String phoneNumber = "123";

		String result = inputValidator.validatePicsCSRPhoneNumber(phoneNumber);

		assertEquals(InputValidator.INVALID_PHONE_FORMAT_KEY, result);
	}

	@Test
	public void testValidateLastName_OneCharacterLastNameIsValid() {
		String errorMessageKey = inputValidator.validateLastName("x");

		assertEquals(InputValidator.NO_ERROR, errorMessageKey);
	}

	@SuppressWarnings("deprecation")
	private Date buildValidDate() {
		Date date = new Date();
		date.setDate(1);
		date.setMonth(0);
		date.setYear(2000);

		return date;
	}

	@Test
	public void testValidatePostCodeForUK_Format1Successful() {
		String result = inputValidator.validateUkPostcode("M2 5BQ", false, true, true);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePostCodeForUK_Format2Successful() {
		String result = inputValidator.validateUkPostcode("M34 4AB", false, true, true);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePostCodeForUK_Format3Successful() {
		String result = inputValidator.validateUkPostcode("CR0 2YR", false, true, true);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePostCodeForUK_Format4Successful() {
		String result = inputValidator.validateUkPostcode("DN16 9AA", false, true, true);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePostCodeForUK_Format5Successful() {
		String result = inputValidator.validateUkPostcode("W1A 4ZZ", false, true, true);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePostCodeForUK_Format6Successful() {
		String result = inputValidator.validateUkPostcode("EC1A 1HQ", false, true, true);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePostCodeForUK_SpecialCasesForTerritoriesSuccessful() {
		String result = inputValidator.validateUkPostcode("ASCN 1ZZ", false, true, true);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePostCodeForUK_SpecialCasesForBritishForces() {
		String result = inputValidator.validateUkPostcode("BF1 	 4TT", false, true, true);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePostCodeForUK_SpecialCasesForCrownDependencies() {
		String result = inputValidator.validateUkPostcode(" IM1  1JB ", false, true, true);

		assertEquals(InputValidator.NO_ERROR, result);
	}

	@Test
	public void testValidatePostCodeForUK_Error() {
		String result = inputValidator.validateUkPostcode("123", false, true, true);

		assertEquals(InputValidator.INVALID_UK_POST_CODE_KEY, result);
	}

}
