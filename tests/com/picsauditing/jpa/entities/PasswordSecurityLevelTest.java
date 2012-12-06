package com.picsauditing.jpa.entities;

import org.junit.Test;

import static junit.framework.Assert.*;

public class PasswordSecurityLevelTest {

    @Test
    public void testValues() throws Exception {
        assertTrue(PasswordSecurityLevel.values().length == 3);

        assertTrue(PasswordSecurityLevel.Normal.dbValue == 0);
        assertTrue(PasswordSecurityLevel.Normal.minLength == 5);
        assertTrue(PasswordSecurityLevel.Normal.enforceMixedCase == false);
        assertTrue(PasswordSecurityLevel.Normal.enforceSpecialCharacter == false);
        assertTrue(PasswordSecurityLevel.Normal.expirationMonths == 0);
        assertTrue(PasswordSecurityLevel.Normal.monthsOfHistoryToDisallow == 0);
        assertTrue(PasswordSecurityLevel.Normal.entriesOfHistoryToDisallow == 0);

        assertTrue(PasswordSecurityLevel.High.dbValue == 1);
        assertTrue(PasswordSecurityLevel.High.minLength == 7);
        assertTrue(PasswordSecurityLevel.High.enforceMixedCase == false);
        assertTrue(PasswordSecurityLevel.High.enforceSpecialCharacter == true);
        assertTrue(PasswordSecurityLevel.High.expirationMonths == 3);
        assertTrue(PasswordSecurityLevel.High.monthsOfHistoryToDisallow == 0);
        assertTrue(PasswordSecurityLevel.High.entriesOfHistoryToDisallow == 4);

        assertTrue(PasswordSecurityLevel.Maximum.dbValue == 2);
        assertTrue(PasswordSecurityLevel.Maximum.minLength == 8);
        assertTrue(PasswordSecurityLevel.Maximum.enforceMixedCase == true);
        assertTrue(PasswordSecurityLevel.Maximum.enforceSpecialCharacter == true);
        assertTrue(PasswordSecurityLevel.Maximum.expirationMonths == 3);
        assertTrue(PasswordSecurityLevel.Maximum.monthsOfHistoryToDisallow == 12);
        assertTrue(PasswordSecurityLevel.Maximum.entriesOfHistoryToDisallow == 0);
    }

	@Test
	public void testEnforceMonthsOfHistory() throws Exception {
		assertFalse(PasswordSecurityLevel.Normal.enforceMonthsOfHistory());
		assertFalse(PasswordSecurityLevel.High.enforceMonthsOfHistory());
		assertTrue(PasswordSecurityLevel.Maximum.enforceMonthsOfHistory());
	}

    @Test
    public void testEnforceEntriesOfHistory() throws Exception {
        assertFalse(PasswordSecurityLevel.Normal.enforceEntriesOfHistory());
	    assertTrue(PasswordSecurityLevel.High.enforceEntriesOfHistory());
	    assertFalse(PasswordSecurityLevel.Maximum.enforceEntriesOfHistory());
    }

	@Test
	public void testEnforceHistory() throws Exception {
		assertFalse(PasswordSecurityLevel.Normal.enforceHistory());
		assertTrue(PasswordSecurityLevel.High.enforceHistory());
		assertTrue(PasswordSecurityLevel.Maximum.enforceHistory());
	}

    @Test
    public void testFromDbValue_found() throws Exception {
        assertEquals(PasswordSecurityLevel.Normal, PasswordSecurityLevel.fromDbValue(0));
        assertEquals(PasswordSecurityLevel.High, PasswordSecurityLevel.fromDbValue(1));
        assertEquals(PasswordSecurityLevel.Maximum, PasswordSecurityLevel.fromDbValue(2));
    }

    @Test
    public void testFromDbValue_notFoundShouldReturnDefault() throws Exception {
        assertEquals(PasswordSecurityLevel.Normal, PasswordSecurityLevel.fromDbValue(-100));
        assertEquals(PasswordSecurityLevel.Normal, PasswordSecurityLevel.fromDbValue(-1));
        assertEquals(PasswordSecurityLevel.Normal, PasswordSecurityLevel.fromDbValue(100));
    }
}
