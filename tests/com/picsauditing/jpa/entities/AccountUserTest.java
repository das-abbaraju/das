package com.picsauditing.jpa.entities;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class AccountUserTest {
    @Test
    public void CloneTest() throws Exception {
        AccountUser original = makeUpAccountUser();
        AccountUser copy = (AccountUser)original.clone();
        assertNotNull(copy);
        assertNotSame(copy, original);
        assertEquals(copy,original);
        assertEquals(original,copy);
        assertEquals(0,copy.getId());
        assertEquals(3333,original.getId());
    }
    @Test
    public void CloneTest_bothMissingUser() throws Exception {
        AccountUser original = makeUpAccountUser();
        original.setUser(null);
        AccountUser copy = (AccountUser)original.clone();
        assertNotNull(copy);
        assertNotSame(copy, original);
        assertFalse(copy.equals(original));
        assertFalse(original.equals(copy));
    }
    @Test
    public void CloneTest_bothMissingAccount() throws Exception {
        AccountUser original = makeUpAccountUser();
        original.setAccount(null);
        AccountUser copy = (AccountUser)original.clone();
        assertNotNull(copy);
        assertNotSame(copy, original);
        assertFalse(copy.equals(original));
        assertFalse(original.equals(copy));
    }

    private AccountUser makeUpAccountUser() {
        AccountUser original = new AccountUser();
        Account account = new Account();
        account.setId(2727);
        original.setAccount(account);
        original.setId(3333);
        original.setOwnerPercent(50);
        original.setStartDate(new Date("1/1/2013"));
        original.setEndDate(new Date("12/31/2013"));
        original.setRole(UserAccountRole.PICSAccountRep);
        original.setUser(new User(4444));
        return original;
    }
}
