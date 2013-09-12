package com.picsauditing.jpa.entities;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class AccountTest {
    public static final String PIPE_CHARACTER = "\\|";
    public static final String NEWLINE_CHARACTER = "\n";
    public static final String RETURN_TYPE = "account";
    Account account;

    @Mock
    private Country country;

    public static final Calendar END_OF_TIME = GregorianCalendar.getInstance();
    public static final Calendar WEEK_AGO = GregorianCalendar.getInstance();
    public static final Calendar TODAY = GregorianCalendar.getInstance();

    static {
        TODAY.setTime(new Date());
        TODAY.set(Calendar.MINUTE, 0);
        TODAY.set(Calendar.SECOND, 0);
        TODAY.set(Calendar.MILLISECOND, 0);

        WEEK_AGO.setTime(new Date());
        WEEK_AGO.add(Calendar.DAY_OF_MONTH, -7);
        WEEK_AGO.set(Calendar.MINUTE, 0);
        WEEK_AGO.set(Calendar.SECOND, 0);
        WEEK_AGO.set(Calendar.MILLISECOND, 0);

        END_OF_TIME.setTime(new Date());
        END_OF_TIME.set(Calendar.YEAR, 4000);
        END_OF_TIME.set(Calendar.MONTH, 4000);
        END_OF_TIME.set(Calendar.DAY_OF_MONTH, 4000);
        END_OF_TIME.set(Calendar.MINUTE, 0);
        END_OF_TIME.set(Calendar.SECOND, 0);
        END_OF_TIME.set(Calendar.MILLISECOND, 0);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

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

    @Test
    public void testGetPasswordSecurityLevel() throws Exception {

        account.setPasswordSecurityLevelId(-1);
        assertEquals(PasswordSecurityLevel.Normal, account.getPasswordSecurityLevel());

        account.setPasswordSecurityLevelId(0);
        assertEquals(PasswordSecurityLevel.Normal, account.getPasswordSecurityLevel());

        account.setPasswordSecurityLevelId(1);
        assertEquals(PasswordSecurityLevel.High, account.getPasswordSecurityLevel());

        account.setPasswordSecurityLevelId(2);
        assertEquals(PasswordSecurityLevel.Maximum, account.getPasswordSecurityLevel());

        account.setPasswordSecurityLevelId(3);
        assertEquals(PasswordSecurityLevel.IOL, account.getPasswordSecurityLevel());

        account.setPasswordSecurityLevelId(99);
        assertEquals(PasswordSecurityLevel.Normal, account.getPasswordSecurityLevel());
    }

    @Test
    public void testIsContractor() {
	    account.setType("Contractor");
	    assertTrue(account.isContractor());

	    account.setType("Admin");
	    assertFalse(account.isContractor());
    }

    @Test
    public void testGetCurrentAccountUserOfRole_OneExpiredOneCurrent() {
        List<AccountUser> accountUserList = new ArrayList<>();
        accountUserList.add(AccountUser.builder()
                .user(User.builder().id(1).build())
                .startDate(WEEK_AGO.getTime())
                .endDate(TODAY.getTime())
                .role(UserAccountRole.PICSInsideSalesRep)
                .build());
        accountUserList.add(AccountUser.builder()
                .user(User.builder().id(2).build())
                .startDate(TODAY.getTime())
                .endDate(END_OF_TIME.getTime())
                .role(UserAccountRole.PICSInsideSalesRep)
                .build());

        account.setAccountUsers(accountUserList);

        assertEquals(2, account.getCurrentAccountUserOfRole(UserAccountRole.PICSInsideSalesRep).getUser().getId());
    }

    @Test
    public void testGetCurrentAccountUserOfRole_NoRoles() {
        List<AccountUser> accountUserList = new ArrayList<>();
        accountUserList.add(AccountUser.builder()
                .user(User.builder().id(1).build())
                .startDate(WEEK_AGO.getTime())
                .endDate(TODAY.getTime())
                .role(UserAccountRole.PICSInsideSalesRep)
                .build());

        account.setAccountUsers(accountUserList);

        assertEquals(null, account.getCurrentAccountUserOfRole(UserAccountRole.PICSInsideSalesRep));
    }

    @Test
    public void testGetCurrentAccountUserOfRole_NoCurrentRoles() {
        List<AccountUser> accountUserList = new ArrayList<>();
        account.setAccountUsers(accountUserList);

        assertEquals(null, account.getCurrentAccountUserOfRole(UserAccountRole.PICSInsideSalesRep));
    }

	private void setupTestAccount(String type, int id, String name, String city, CountrySubdivision countrySubdivision,
                                  AccountStatus status) {
        account.setType(type);
        account.setId(id);
        account.setName(name);
        account.setCity(city);
        account.setCountry(country);
        account.setCountrySubdivision(countrySubdivision);
        account.setStatus(status);

        when(country.isHasCountrySubdivisions()).thenReturn(true);
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
            return searchTextArray[searchTextArray.length - 2];
        }
        return null;
    }

    private String getLastElement(String searchText) {
        if (searchText != null) {
            String[] searchTextArray = searchText.split(PIPE_CHARACTER);
            return searchTextArray[searchTextArray.length - 1];
        }
        return null;
    }
}
