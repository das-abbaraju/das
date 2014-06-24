package com.picsauditing.actions.billing;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class MockBrainTreeTransactTest extends PicsActionTest {
    private MockBrainTreeTransact mockBrainTreeTransact;
    private String redirect = "/TestRedirect.action?processPayment=true";
    private Integer customer_vault_id = 123;
    private String time = "987654321";
    private String hash = "8dca57da883529238d891c1372456ae8";
    private String EXPECTED_URL = "/TestRedirect.action?processPayment=true&response=1&responsetext=Customer+Added&authcode=&transactionid=&avsresponse=&cvvresponse=&orderid=&type=&response_code=100&customer_vault_id=123&username=1884502&time=987654321&amount=&hash=8dca57da883529238d891c1372456ae8";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockBrainTreeTransact = new MockBrainTreeTransact();
        super.setUp(mockBrainTreeTransact);

        mockBrainTreeTransact.setRedirect(redirect);
        mockBrainTreeTransact.setCustomer_vault_id(customer_vault_id);
        mockBrainTreeTransact.setTime(time);
        mockBrainTreeTransact.setHash(hash);
    }

    @Test
    public void testCompleteRegistration_SetsExpectedUrl() throws Exception {
        String result = mockBrainTreeTransact.completeRegistration();
        String url = mockBrainTreeTransact.getUrl();

        assertEquals(PicsActionSupport.REDIRECT, result);
        assertEquals(EXPECTED_URL, url);
    }

    @Test
     public void testGetCreditCardService() throws Exception {
         //FIXME
     }

}
