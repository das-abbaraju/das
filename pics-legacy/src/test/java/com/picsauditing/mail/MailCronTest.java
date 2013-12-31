package com.picsauditing.mail;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.mail.subscription.SubscriptionValidationException;
import com.picsauditing.service.mail.MailCronService;
import org.hibernate.JDBCException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static junit.framework.Assert.*;

public class MailCronTest {
    public static final String EXCEPTION_MESSAGE = "The MySQL server is running with the --read-only option so it cannot execute this statement";
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
    public void testSubscription_Exception() throws SubscriptionValidationException {
        doThrow(new RuntimeException(EXCEPTION_MESSAGE)).when(service).processEmailSubscription(anyInt());

        cron.subscription();
        assertEquals(EXCEPTION_MESSAGE, cron.getActionErrors().toArray()[0]);
    }

    @Test
    public void testSend() throws Exception {
        String result = cron.send();

        assertEquals(1, cron.getActionMessages().size());
        assertEquals(PicsActionSupport.ACTION_MESSAGES, result);
    }

    @Test
    public void testSend_Exception() throws SubscriptionValidationException {
        doThrow(new RuntimeException(EXCEPTION_MESSAGE)).when(service).processPendingEmails();

        cron.send();
        assertEquals(EXCEPTION_MESSAGE, cron.getActionErrors().toArray()[0]);
    }

}
