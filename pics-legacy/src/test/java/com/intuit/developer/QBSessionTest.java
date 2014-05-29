package com.intuit.developer;

import com.picsauditing.jpa.entities.Currency;
import org.junit.Test;

public class QBSessionTest {

    @Test
    public void testIsCHF_inputIsCHF() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.CHF.name());
        assert(qbSession.isCHF());
    }

    @Test
    public void testIsCHF_inputIsNotCHF() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.CAD.name());
        assert(!qbSession.isCHF());
    }
}