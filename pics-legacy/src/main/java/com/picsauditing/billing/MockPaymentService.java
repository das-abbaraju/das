package com.picsauditing.billing;

import com.picsauditing.braintree.CreditCard;
import com.picsauditing.jpa.entities.*;

import java.math.BigDecimal;

public class MockPaymentService implements PaymentService {
    private final static String TEST_CREDIT_CARD_NUMBER = "4111111111111111";
    private final static String PAYMENT_URL = "/MockBrainTreeTransact.action";

    @Override
    public String getPaymentUrl() {
        return PAYMENT_URL;
    }

    @Override
    public CreditCard getCreditCard(ContractorAccount account) throws Exception {
        return new CreditCard(TEST_CREDIT_CARD_NUMBER);
    }

    @Override
    public void deleteCreditCard(ContractorAccount account) throws Exception {

    }

    @Override
    public String getTransactionCondition(String transactionID) throws Exception {
        return null;
    }

    @Override
    public boolean processPayment(Payment payment, Invoice invoice) throws Exception {
        return false;
    }

    @Override
    public boolean processRefund(String transactionID, BigDecimal amount) throws Exception {
        return false;
    }

    @Override
    public boolean processCancelation(String transactionID) throws Exception {
        return false;
    }

    @Override
    public boolean voidTransaction(String transactionID) throws Exception {
        return false;
    }
}
