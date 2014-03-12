package com.picsauditing.service.billing;

import com.picsauditing.jpa.entities.InvoiceFee;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InvoiceFeeProRater {

    public static final int DAYS_IN_YEAR = 365;
    public static final int DECIMAL_PLACES = 0;

    public static BigDecimal calculateProRatedInvoiceFees(InvoiceFee previousLevel, InvoiceFee newLevel, double daysUntilExpiration) {
        BigDecimal previousLevelAmount = previousLevel.getAmount();
        BigDecimal newLevelAmount = newLevel.getAmount();

        BigDecimal proratedAmount = calculateProRatedInvoiceFees(previousLevelAmount, newLevelAmount, daysUntilExpiration);
        return proratedAmount;
    }

    public static BigDecimal calculateProRatedInvoiceFees(BigDecimal previousLevelAmount, BigDecimal newLevelAmount, double daysUntilExpiration) {
        BigDecimal differenceOfLevels = newLevelAmount.subtract(previousLevelAmount);

        BigDecimal proratedAmount = new BigDecimal(daysUntilExpiration).multiply(differenceOfLevels).
                divide(new BigDecimal(DAYS_IN_YEAR), DECIMAL_PLACES, RoundingMode.HALF_UP);
        return proratedAmount;
    }
}