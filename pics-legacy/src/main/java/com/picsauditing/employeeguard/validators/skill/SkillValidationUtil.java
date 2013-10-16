package com.picsauditing.employeeguard.validators.skill;

import com.picsauditing.employeeguard.forms.contractor.SkillForm;
import com.picsauditing.util.Strings;

public class SkillValidationUtil {

    public enum SkillField {
        NAME, TYPE, EXPIRES
    }

    public static boolean valid(SkillForm skillForm, SkillField field) {
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

    private static boolean validateExpiration(SkillForm skillForm) {
        if (skillForm.getSkillType().isTraining()) {
            return (skillForm.isDoesNotExpire() ||
                    (skillForm.getIntervalType().isApplicableExpiration() && skillForm.getIntervalPeriod() > 0));
        }

        return true;
    }

    private static boolean validateName(SkillForm skillForm) {
        return Strings.isNotEmpty(skillForm.getName());
    }

    private static boolean validateType(SkillForm skillForm) {
        return skillForm.getSkillType() != null;
    }

}
