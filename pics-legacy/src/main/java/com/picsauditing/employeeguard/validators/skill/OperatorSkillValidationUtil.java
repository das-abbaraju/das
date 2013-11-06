package com.picsauditing.employeeguard.validators.skill;

import com.picsauditing.employeeguard.forms.operator.OperatorSkillForm;
import com.picsauditing.util.Strings;

public class OperatorSkillValidationUtil {

    public enum OperatorSkillField {
        NAME, TYPE, EXPIRES
    }

    public static boolean valid(OperatorSkillForm skillForm, OperatorSkillField field) {
        switch (field) {
            case NAME:
                return validateName(skillForm);
            case TYPE:
                return validateType(skillForm);
            case EXPIRES:
                return validateExpiration(skillForm);
            default:
                throw new IllegalArgumentException("You have not set up validation for that field: " + field);
        }
    }

	private static boolean validateExpiration(OperatorSkillForm skillForm) {
        if (skillForm.getSkillType() != null && skillForm.getSkillType().isTraining()) {
            return (skillForm.isDoesNotExpire() ||
                    (skillForm.getIntervalType().isApplicableExpiration() && skillForm.getIntervalPeriod() > 0));
        }

        return true;
    }

    private static boolean validateName(OperatorSkillForm skillForm) {
        return Strings.isNotEmpty(skillForm.getName());
    }

    private static boolean validateType(OperatorSkillForm skillForm) {
        return skillForm.getSkillType() != null;
    }
}
