package com.picsauditing.flagcalculator.service;

import com.picsauditing.flagcalculator.entities.Facility;
import com.picsauditing.flagcalculator.entities.OperatorAccount;

public class AccountService {
    public static boolean isOrIsDescendantOf(OperatorAccount currentOperator, int id) {
        if (currentOperator.getId() == id) {
            return true;
        }
        return isDescendantOf(currentOperator, id);
    }

    public static boolean isDescendantOf(OperatorAccount currentOperator, int id) {
        if (currentOperator.getParent() == null) {
            // No parent exists
            return false;
        }
        if (currentOperator.getParent().getId() == id) {
            // Yes, the parent matches
            return true;
        }
        // Maybe the grandparent is a descendant of id
        return isDescendantOf(currentOperator.getParent(), id);
    }

    public static boolean isApplicableFlagOperator(OperatorAccount currentOperator, OperatorAccount flagOperator) {
        boolean isApplicable = false;

        isApplicable = isOrIsDescendantOf(currentOperator, flagOperator.getId());

        if (!isApplicable) {
            for (Facility facility : flagOperator.getOperatorFacilities()) {
                if (facility.getOperator().equals(currentOperator)) {
                    isApplicable = true;
                    break;
                }
            }
        }

        return isApplicable;
    }
}
