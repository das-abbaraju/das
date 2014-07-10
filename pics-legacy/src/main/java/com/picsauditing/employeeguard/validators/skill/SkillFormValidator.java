package com.picsauditing.employeeguard.validators.skill;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.SkillForm;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;

public class SkillFormValidator extends AbstractBasicAndDuplicateValidator<SkillForm> {

	public static final String SKILL_FORM = "skillForm";

	@Override
	public void doFormValidation(SkillForm skillForm) {
		if (!SkillValidationUtil.valid(skillForm, SkillValidationUtil.SkillField.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(SKILL_FORM, "name"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.SKILL.NAME"));
		}

		if (!SkillValidationUtil.valid(skillForm, SkillValidationUtil.SkillField.TYPE)) {
			addFieldErrorIfMessage(fieldKeyBuilder(SKILL_FORM, "skillType"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.SKILL.TYPE"));
		}

		if (!SkillValidationUtil.valid(skillForm, SkillValidationUtil.SkillField.EXPIRES)) {
			addFieldErrorIfMessage(fieldKeyBuilder(SKILL_FORM, "intervalPeriod"),
							EGI18n.getTextFromResourceBundle("VALIDATION.INVALID.SKILL.EXPIRATION"));
		}
	}

	@Override
	protected SkillForm getFormFromValueStack(final ValueStack valueStack) {
		return (SkillForm) valueStack.findValue(SKILL_FORM, SkillForm.class);
	}


    @Override
    protected String getDuplicateErrorMessage() {
        return EGI18n.getTextFromResourceBundle("VALIDATION.INVALID.SKILL.DUPLICATE");
    }
}
