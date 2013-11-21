package com.picsauditing.i18n.model;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertTrue;

public class DefaultUsageContextTest {
    private DefaultUsageContext defaultUsageContext;
    private String UNKNOWN_CONTEXT;

    @Before
    public void setup() throws Exception {
        defaultUsageContext = new DefaultUsageContext();
        UNKNOWN_CONTEXT = Whitebox.getInternalState(DefaultUsageContext.class, "UNKNOWN_CONTEXT");
    }

    @Test
    public void testEnvironment() throws Exception {
        assertTrue(UNKNOWN_CONTEXT.equals(defaultUsageContext.environment()));
    }

    @Test
    public void testPageName() throws Exception {
        assertTrue(UNKNOWN_CONTEXT.equals(defaultUsageContext.pageName()));
    }

    @Test
    public void testLocale() throws Exception {
        assertTrue(UNKNOWN_CONTEXT.toLowerCase().equals(defaultUsageContext.locale().toString()));
    }

}
