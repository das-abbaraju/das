package com.picsauditing.billing;

import com.picsauditing.braintree.CreditCard;
import com.picsauditing.jpa.entities.*;

import java.math.BigDecimal;

public interface PaymentService {
    CreditCard getCreditCard(ContractorAccount account) throws Exception;

    void deleteCreditCard(ContractorAccount account) throws Exception;

    String getTransactionCondition(String transactionID) throws Exception;

    boolean processPayment(Payment payment, Invoice invoice) throws Exception;

    boolean processRefund(String transactionID, BigDecimal amount) throws Exception;

    boolean processCancelation(String transactionID) throws Exception;

    boolean voidTransaction(String transactionID) throws Exception;

    String getPaymentUrl();
}
