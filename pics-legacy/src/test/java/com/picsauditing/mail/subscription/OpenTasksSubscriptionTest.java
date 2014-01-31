package com.picsauditing.mail.subscription;

import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.Subscription;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.invokeMethod;

public class OpenTasksSubscriptionTest {
    private OpenTasksSubscription openTaskSub;

    @Before
    public void setUp() throws Exception {
        openTaskSub = new OpenTasksSubscription();
    }

    @Test
    public void testSendSubscription_NoTokens() throws Exception {
        EmailSubscription subscription = new EmailSubscription();
        openTaskSub.sendSubscription(subscription);
        assertNull(subscription.getLastSent());
    }
}
