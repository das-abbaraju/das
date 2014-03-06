package com.picsauditing.billing;

import com.opensymphony.xwork2.interceptor.annotations.After;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PaymentServiceFactoryTest {
    @Mock
    private FeatureToggle featureToggle;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private BrainTree brainTreePaymentService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        SpringUtils springUtils = new SpringUtils();
        springUtils.setApplicationContext(applicationContext);
        when(applicationContext.getBean(SpringUtils.BrainTree)).thenReturn(brainTreePaymentService);

        Whitebox.setInternalState(PaymentServiceFactory.class, "featureToggle", featureToggle);
    }

    @After
    public void tearDown() throws Exception {
        Whitebox.setInternalState(PaymentServiceFactory.class, "featureToggle", null);
        SpringUtils springUtils = new SpringUtils();
        springUtils.setApplicationContext(null);
    }

    @Test
    public void testPaymentService_FeatureOnReturnsMockPaymentService() throws Exception {
        when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_MOCK_PAYMENT_SERVICE)).thenReturn(true);

        PaymentService result = PaymentServiceFactory.paymentService();

        assertTrue(result instanceof MockPaymentService);
    }

    @Test
    public void testPaymentService_FeatureOffReturnsBrainTreeBean() throws Exception {
        when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_MOCK_PAYMENT_SERVICE)).thenReturn(false);

        PaymentService result = PaymentServiceFactory.paymentService();

        assertTrue(result instanceof BrainTree);
    }

}
