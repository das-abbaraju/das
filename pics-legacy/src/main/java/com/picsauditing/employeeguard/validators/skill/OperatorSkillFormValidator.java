package com.picsauditing.employeeguard.validators.skill;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.operator.OperatorSkillForm;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;
import com.picsauditing.employeeguard.validators.AbstractBasicValidator;

public class OperatorSkillFormValidator extends AbstractBasicAndDuplicateValidator<OperatorSkillForm> {
	public static final String OPERATOR_SKILL_FORM = "operatorSkillForm";

	@Override
	public void doFormValidation(OperatorSkillForm operatorSkillForm) {
		if (!OperatorSkillValidationUtil.valid(operatorSkillForm, OperatorSkillValidationUtil.OperatorSkillField.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(OPERATOR_SKILL_FORM, "name"), "Skill name is missing");
		}

		if (!OperatorSkillValidationUtil.valid(operatorSkillForm, OperatorSkillValidationUtil.OperatorSkillField.TYPE)) {
			addFieldErrorIfMessage(fieldKeyBuilder(OPERATOR_SKILL_FORM, "skillType"), "Type is missing");
		}

		if (!OperatorSkillValidationUtil.valid(operatorSkillForm, OperatorSkillValidationUtil.OperatorSkillField.EXPIRES)) {
			addFieldErrorIfMessage(fieldKeyBuilder(OPERATOR_SKILL_FORM, "intervalPeriod"),
					"Expiration is invalid");
		}
	}

	@Override
	protected OperatorSkillForm getFormFromValueStack(ValueStack valueStack) {
		return (OperatorSkillForm) valueStack.findValue(OPERATOR_SKILL_FORM, OperatorSkillForm.class);
	}

    @Override
    protected String getDuplicateErrorMessage() {
        return "Name, Type";
    }
}
