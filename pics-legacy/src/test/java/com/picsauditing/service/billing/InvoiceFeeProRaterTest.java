package com.picsauditing.service.billing;

import com.picsauditing.jpa.entities.InvoiceFee;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;

public class InvoiceFeeProRaterTest {

    @Test
    public void testCalculateProRatedInvoiceFees() throws Exception {
        runTest(100, 200, 272, 75);
    }

    @Test
    public void testCalculateProRatedInvoiceFees_NoProRating() throws Exception {
        runTest(100, 200, 0, 0);
    }

    @Test
    public void testCalculateProRatedInvoiceFees_MaximumProRating() throws Exception {
        runTest(100, 200, 365, 100);
    }

    private void runTest(int previousLevelAmount, int newLevelAmount, int daysUntilExpiration, int expectedResult) {
        InvoiceFee previousLevel = InvoiceFee.builder().amount(new BigDecimal(previousLevelAmount)).build();
        InvoiceFee newLevel = InvoiceFee.builder().amount(new BigDecimal(newLevelAmount)).build();

        assertEquals(new BigDecimal(expectedResult),
                InvoiceFeeProRater.calculateProRatedInvoiceFees(previousLevel, newLevel, daysUntilExpiration));
    }
}
