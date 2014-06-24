package com.picsauditing.util;

import com.picsauditing.jpa.entities.Currency;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EmailAddressUtilsTest {

    @Test
    public void testGetBillingEmailForEUR() throws Exception {
        String euBillingMailId = EmailAddressUtils.getBillingEmail(Currency.EUR);

        assertEquals(euBillingMailId, EmailAddressUtils.PICS_BILLING_MAIL_ID_EU);
    }

    @Test
    public void testGetBillingEmailForGBP() throws Exception {
        String euBillingMailId = EmailAddressUtils.getBillingEmail(Currency.GBP);

        assertEquals(euBillingMailId, EmailAddressUtils.PICS_BILLING_MAIL_ID_EU);
    }

    @Test
    public void testGetBillingEmailForCHF() throws Exception {
        String euBillingMailId = EmailAddressUtils.getBillingEmail(Currency.CHF);

        assertEquals(euBillingMailId, EmailAddressUtils.PICS_BILLING_MAIL_ID_EU);
    }

    @Test
    public void testGetBillingEmailForPLN() throws Exception {
        String euBillingMailId = EmailAddressUtils.getBillingEmail(Currency.PLN);

        assertEquals(euBillingMailId, EmailAddressUtils.PICS_BILLING_MAIL_ID_EU);
    }

    @Test
    public void testGetBillingEmailForOthers() throws Exception {
        String euBillingMailId = EmailAddressUtils.getBillingEmail(Currency.USD);

        assertEquals(euBillingMailId, EmailAddressUtils.PICS_BILLING_MAIL_ID_DEFAULT);
    }

    @Test
    public void testGetBillingEmailForAUD() throws Exception {
        String euBillingMailId = EmailAddressUtils.getBillingEmail(Currency.AUD);

        assertEquals(euBillingMailId, EmailAddressUtils.PICS_BILLING_MAIL_ID_DEFAULT);
    }

    @Test
    public void testGetBillingEmailForNullCurrency() throws Exception {
        String euBillingMailId = EmailAddressUtils.getBillingEmail(null);

        assertEquals(euBillingMailId, EmailAddressUtils.PICS_BILLING_MAIL_ID_DEFAULT);
    }
}
