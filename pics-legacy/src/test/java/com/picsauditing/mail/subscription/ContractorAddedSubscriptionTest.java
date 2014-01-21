package com.picsauditing.mail.subscription;

import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.search.Report;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContractorAddedSubscriptionTest extends PicsTest {
    ContractorAddedSubscription builder;

    @Mock
    private DynamicReportsSubscription dynamicReportsSubscription;
    @Mock
    private ReportDAO reportDAO;
    @Mock
    private EmailSubscription subscription;
    @Mock
    private OperatorAccount operator;
    @Mock
    private User user;
    @Mock
    private Report report;

    protected List<BasicDynaBean> data = new ArrayList<BasicDynaBean>();
    protected Map<String, Object> defaultTokens = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        builder = new ContractorAddedSubscription();

        Whitebox.setInternalState(builder, "dynamicReports", dynamicReportsSubscription);
        Whitebox.setInternalState(builder, "reportDAO", reportDAO);

        defaultTokens.put("state","SUCCESS");
    }

    @Test
    public void testProcess_Daily() throws Exception {
        when(subscription.getTimePeriod()).thenReturn(SubscriptionTimePeriod.Daily);
        when(dynamicReportsSubscription.process((EmailSubscription) any())).thenReturn(defaultTokens);
        Map<String, Object> tokens = builder.process(subscription);
        assertTrue(tokens.size() == 1);
    }

    @Test
    public void testProcess_Weekly() throws Exception {
        when(subscription.getTimePeriod()).thenReturn(SubscriptionTimePeriod.Weekly);
        when(dynamicReportsSubscription.process((EmailSubscription) any())).thenReturn(defaultTokens);
        Map<String, Object> tokens = builder.process(subscription);
        assertTrue(tokens.size() == 1);
    }

    @Test
    public void testProcess_Monthly() throws Exception {
        when(subscription.getTimePeriod()).thenReturn(SubscriptionTimePeriod.Monthly);
        when(dynamicReportsSubscription.process((EmailSubscription) any())).thenReturn(defaultTokens);
        Map<String, Object> tokens = builder.process(subscription);
        assertTrue(tokens.size() == 1);
    }
}
