package com.picsauditing.mail;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.service.mail.MailCronService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;
import static junit.framework.Assert.*;

public class MailCronTest {
    @Mock
    MailCronService service;
    private MailCron cron;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cron = new MailCron();

        cron.mailCronService = service;
    }

    @Test
    public void testList() throws Exception {
        when(service.getSubscriptionIdsToSendAsCommaDelimited()).thenReturn("1,2,3");

        String result = cron.listAjax();

        assertEquals(PicsActionSupport.PLAIN_TEXT, result);
        assertEquals("1,2,3", cron.getOutput());
    }

    @Test
    public void testSubscription() throws Exception {
        String result = cron.subscription();

        assertEquals(0, cron.getActionMessages().size());
        assertEquals(PicsActionSupport.ACTION_MESSAGES, result);
    }

    @Test
    public void testSend() throws Exception {
        String result = cron.send();

        assertEquals(1, cron.getActionMessages().size());
        assertEquals(PicsActionSupport.ACTION_MESSAGES, result);
    }

}
