package com.picsauditing.employeeguard.validators.skill;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.forms.contractor.SkillForm;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;
import com.picsauditing.employeeguard.validators.AbstractBasicValidator;

public class SkillFormValidator extends AbstractBasicAndDuplicateValidator<SkillForm> {

	public static final String SKILL_FORM = "skillForm";

	@Override
	public void doFormValidation(SkillForm skillForm) {
		if (!SkillValidationUtil.valid(skillForm, SkillValidationUtil.SkillField.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(SKILL_FORM, "name"), "Skill name is missing");
		}

		if (!SkillValidationUtil.valid(skillForm, SkillValidationUtil.SkillField.TYPE)) {
			addFieldErrorIfMessage(fieldKeyBuilder(SKILL_FORM, "skillType"), "Type is missing");
		}

		if (!SkillValidationUtil.valid(skillForm, SkillValidationUtil.SkillField.EXPIRES)) {
			addFieldErrorIfMessage(fieldKeyBuilder(SKILL_FORM, "intervalPeriod"),
					"Expiration is invalid");
		}
	}

	@Override
	protected SkillForm getFormFromValueStack(final ValueStack valueStack) {
		return (SkillForm) valueStack.findValue(SKILL_FORM, SkillForm.class);
	}


    @Override
    protected String getDuplicateErrorMessage() {
        return "Name, Type";
    }
}
