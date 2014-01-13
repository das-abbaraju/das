package com.picsauditing.mail.subscription;

import java.util.ArrayList;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Report;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ForcedFlagsSubscriptionTest extends PicsTest {
    ForcedFlagsSubscription  builder;

    @Mock
    private EmailSubscription subscription;
    @Mock
    private OperatorAccount operator;
    @Mock
    private User user;
    @Mock
    private Report report;

    protected List<BasicDynaBean> data = new ArrayList<BasicDynaBean>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        builder = new ForcedFlagsSubscription();

        when(subscription.getUser()).thenReturn(user);
        when(user.getAccount()).thenReturn(operator);
        when(operator.getId()).thenReturn(100);
        when(operator.isOperatorCorporate()).thenReturn(false);
        when(report.getPage(false)).thenReturn(data);
        PicsTestUtil.forceSetPrivateField(builder, "report", report);
    }

    @Test
    public void testProcess_FilterForcedFlags() throws Exception {
        BasicDynaBean bean;

        bean = mock(BasicDynaBean.class);
        when(bean.get("forcedBy")).thenReturn("Michael Do");
        data.add(bean);

        bean = mock(BasicDynaBean.class);
        when(bean.get("forcedBy")).thenReturn("John Do");
        data.add(bean);

        Map<String, Object> tokens = builder.process(subscription);
        assertTrue(tokens.size() == 1);
    }
}
