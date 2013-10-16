package com.picsauditing.employeeguard.validators.skill;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.SkillForm;
import com.picsauditing.employeeguard.validators.AbstractValidator;

public class SkillFormValidator extends AbstractValidator<SkillForm> {

	public static final String SKILL_FORM = "skillForm";

	// private Map<String, String> errors = new HashMap<>();

	@Override
	public void performValidation(SkillForm skillForm) {
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
	protected SkillForm getFormFromValueStack(ValueStack valueStack) {
		return (SkillForm) valueStack.findValue(SKILL_FORM, SkillForm.class);
	}
}
