package com.picsauditing.service;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.mail.Subscription;
import com.picsauditing.service.mail.MailCronService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class AppPropertyServiceTest {
    private static final String TEST_PROPERTY = "test.property";
    @Mock
    private AppPropertyDAO appPropertyDao;
    private AppPropertyService service;

    @Before
    public void setUp() throws Exception {
        service = new AppPropertyService();
        MockitoAnnotations.initMocks(this);
        service.appPropertyDao = appPropertyDao;
    }

    @Test
    public void testIsEnabled_1() throws Exception {
        when(appPropertyDao.find(TEST_PROPERTY)).thenReturn(new AppProperty(TEST_PROPERTY, "1"));
        assertEquals(true, service.isEnabled(TEST_PROPERTY, true));
    }

    @Test
    public void testIsEnabled_true() throws Exception {
        when(appPropertyDao.find(TEST_PROPERTY)).thenReturn(new AppProperty(TEST_PROPERTY, "true"));
        assertEquals(true, service.isEnabled(TEST_PROPERTY, false));
    }

    @Test
    public void testIsEnabled_null() throws Exception {
        when(appPropertyDao.find(TEST_PROPERTY)).thenReturn(null);
        assertEquals(true, service.isEnabled(TEST_PROPERTY, true));
    }

    @Test
    public void testGetPropertyString_null() throws Exception {
        when(appPropertyDao.find(TEST_PROPERTY)).thenReturn(null);
        assertEquals("default", service.getPropertyString(TEST_PROPERTY, "default"));
    }

    @Test
    public void testGetPropertyInt() throws Exception {
        when(appPropertyDao.find(TEST_PROPERTY)).thenReturn(new AppProperty(TEST_PROPERTY, "1"));
        assertEquals(1, service.getPropertyInt(TEST_PROPERTY, 1));
    }
}
