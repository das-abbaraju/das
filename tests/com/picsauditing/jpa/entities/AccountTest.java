
package com.picsauditing.jpa.entities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class AccountTest {

    public static final String PIPE_CHARACTER = "\\|";
    public static final String NEWLINE_CHARACTER = "\n";
    public static final String RETURN_TYPE = "account";
    Account account;

    @Before
    public void setUp() throws Exception {
        account = new Account();
    }

    @After
    public void tearDown() throws Exception {
        account = null;
    }

    @Test
    public void testGetSearchText_uninitializedAccount() throws Exception {
        String searchText = account.getSearchText();

        assertNotNull(searchText);
        assertEquals(RETURN_TYPE, getReturnType(searchText));
        assertEquals("null", getType(searchText));
        assertEquals("0", getId(searchText));
        assertEquals("null", getName(searchText));
        assertEquals("", searchText.split(PIPE_CHARACTER)[4]);
        assertEquals(AccountStatus.Pending.toString(), getStatus(searchText));
        assertEquals(NEWLINE_CHARACTER, getLastElement(searchText));
    }

    @Test
    public void testGetSearchText_basicAccount() throws Exception {

        setupTestAccount("fooType", 123, "Acme Company", "Irvine", new CountrySubdivision("CA"), AccountStatus.Demo);

        String searchText = account.getSearchText();

        assertNotNull(searchText);
        assertEquals(RETURN_TYPE, getReturnType(searchText));
        assertEquals("fooType", getType(searchText));
        assertEquals("123", getId(searchText));
        assertEquals("Acme Company", getName(searchText));
        assertEquals("Irvine, CA", getCityAndCountrySubdivision(searchText));
        assertEquals(AccountStatus.Demo.toString(), getStatus(searchText));
        assertEquals(NEWLINE_CHARACTER, getLastElement(searchText));
    }

    @Test
    public void testGetSearchText_subdivisionWithoutCity() throws Exception {

        setupTestAccount("fooType", 123, "Acme Company", null, new CountrySubdivision("CA"), AccountStatus.Demo);

        String searchText = account.getSearchText();

        assertNotNull(searchText);
        assertEquals(RETURN_TYPE, getReturnType(searchText));
        assertEquals("fooType", getType(searchText));
        assertEquals("123", getId(searchText));
        assertEquals("Acme Company", getName(searchText));
        assertEquals("CA", getCityAndCountrySubdivision(searchText));
        assertEquals(AccountStatus.Demo.toString(), getStatus(searchText));
        assertEquals(NEWLINE_CHARACTER, getLastElement(searchText));
    }

    private void setupTestAccount(String type, int id, String name, String city, CountrySubdivision countrySubdivision, AccountStatus status) {
        account.setType(type);
        account.setId(id);
        account.setName(name);
        account.setCity(city);
        account.setCountrySubdivision(countrySubdivision);
        account.setStatus(status);
    }

    private String getReturnType(String searchText) {
        if (searchText != null) {
            return searchText.split(PIPE_CHARACTER)[0];
        }
        return null;
    }

    private String getType(String searchText) {
        if (searchText != null) {
            return searchText.split(PIPE_CHARACTER)[1];
        }
        return null;
    }

    private String getId(String searchText) {
        if (searchText != null) {
            return searchText.split(PIPE_CHARACTER)[2];
        }
        return null;
    }

    private String getName(String searchText) {
        if (searchText != null) {
            return searchText.split(PIPE_CHARACTER)[3];
        }
        return null;
    }

    private String getCityAndCountrySubdivision(String searchText) {
        if (searchText != null) {
            return searchText.split(PIPE_CHARACTER)[4];
        }
        return null;
    }

    private String getStatus(String searchText) {
        if (searchText != null) {
            String[] searchTextArray = searchText.split(PIPE_CHARACTER);
            return searchTextArray[searchTextArray.length -2];
        }
        return null;
    }

    private String getLastElement(String searchText) {
        if (searchText != null) {
            String[] searchTextArray = searchText.split(PIPE_CHARACTER);
            return searchTextArray[searchTextArray.length -1];
        }
        return null;
    }
}