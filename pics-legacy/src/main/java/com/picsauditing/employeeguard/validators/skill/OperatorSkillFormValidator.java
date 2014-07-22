package com.picsauditing.employeeguard.validators.skill;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.operator.OperatorSkillForm;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;

public class OperatorSkillFormValidator extends AbstractBasicAndDuplicateValidator<OperatorSkillForm> {
	public static final String OPERATOR_SKILL_FORM = "operatorSkillForm";

	@Override
	public void doFormValidation(OperatorSkillForm operatorSkillForm) {
		if (!OperatorSkillValidationUtil.valid(operatorSkillForm, OperatorSkillValidationUtil.OperatorSkillField.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(OPERATOR_SKILL_FORM, "name"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.SKILL.NAME"));
		}

		if (!OperatorSkillValidationUtil.valid(operatorSkillForm, OperatorSkillValidationUtil.OperatorSkillField.TYPE)) {
			addFieldErrorIfMessage(fieldKeyBuilder(OPERATOR_SKILL_FORM, "skillType"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.SKILL.TYPE"));
		}

		if (!OperatorSkillValidationUtil.valid(operatorSkillForm, OperatorSkillValidationUtil.OperatorSkillField.EXPIRES)) {
			addFieldErrorIfMessage(fieldKeyBuilder(OPERATOR_SKILL_FORM, "intervalPeriod"),
							EGI18n.getTextFromResourceBundle("VALIDATION.INVALID.SKILL.EXPIRATION"));
		}
	}

	@Override
	protected OperatorSkillForm getFormFromValueStack(ValueStack valueStack) {
		return (OperatorSkillForm) valueStack.findValue(OPERATOR_SKILL_FORM, OperatorSkillForm.class);
	}

    @Override
    protected String getDuplicateErrorMessage() {
        return EGI18n.getTextFromResourceBundle("VALIDATION.INVALID.SKILL.DUPLICATE");
    }
}
