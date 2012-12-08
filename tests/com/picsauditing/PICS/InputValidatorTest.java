package com.picsauditing.PICS;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

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
    public void testContainsOnlySafeCharacters_AmpersandReturnsFalse() {
        boolean result = inputValidator.containsOnlySafeCharacters("&");

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
    public void testIsUsernameValid_NullReturnsFalse() {
        boolean result = inputValidator.isUsernameValid(null);

        assertFalse(result);
    }

    @Test
    public void testIsUsernameValid_EmptyStringReturnsFalse() {
        boolean result = inputValidator.isUsernameValid("");

        assertFalse(result);
    }

    @Test
    public void testIsUsernameValid_EmailAddressReturnsTrue() {
        boolean result = inputValidator.isUsernameValid("me@here.com");

        assertTrue(result);
    }

    @Test
    public void testIsUsernameValid_UnderscoreDashAndPlusAreValid() {
        boolean result = inputValidator.isUsernameValid("abc-_+");

        assertTrue(result);
    }

    @Test
    public void testIsUsernameValid_UnderFiveCharactersReturnsFalse() {
        boolean result = inputValidator.isUsernameValid("1234");

        assertFalse(result);
    }

    @Test
    public void testIsUsernameValid_FiveCharactersReturnsTrue() {
        boolean result = inputValidator.isUsernameValid("12345");

        assertTrue(result);
    }

    @Test
    public void testIsUsernameValid_OneHundredCharactersReturnsTrue() {
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

        boolean result = inputValidator.isUsernameValid(string100CharactersLong);

        assertTrue(result);
    }

    @Test
    public void testIsUsernameValid_OverOneHundredCharactersReturnsFalse() {
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

        boolean result = inputValidator.isUsernameValid(string101CharactersLong);

        assertFalse(result);
    }

    @Test
    public void testIsUsernameValid_InvalidCharactersReturnFalse() {
        String[] invalidChars = {
                "`", "~", "!", "#", "$", "%", "^", "&", "*", "(", ")",
                "=", "[", "]", "{", "}", "\\", "|", ":", ";", "'", "\"",
                ",", "<", ">", "?", "/"
        };

        for (String invalidChar : invalidChars) {
            boolean result = inputValidator.isUsernameValid("abcd" + invalidChar);
            assertFalse(result);
        }
    }

}
