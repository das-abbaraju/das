package com.picsauditing.actions.cron;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CronToggleTest {
    @Test
    public void testIsCronTaskTextEnabled_default() throws Exception {
        assertTrue(CronToggle.isCronTaskTextEnabled("foo"));
    }

    @Test
    public void testIsCronTaskTextEnabled_null() throws Exception {
        assertTrue(CronToggle.isCronTaskTextEnabled(null));
    }

    @Test
    public void testIsCronTaskTextEnabled_0() throws Exception {
        assertFalse(CronToggle.isCronTaskTextEnabled("0"));
    }

    @Test
    public void testIsCronTaskTextEnabled_false() throws Exception {
        assertFalse(CronToggle.isCronTaskTextEnabled("false"));
    }
}
