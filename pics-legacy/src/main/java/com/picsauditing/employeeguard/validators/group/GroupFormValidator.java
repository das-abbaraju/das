package com.picsauditing.employeeguard.validators.group;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.GroupForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.validators.AbstractValidator;

public class GroupFormValidator extends AbstractValidator<GroupNameSkillsForm> {

	public static final String GROUP_FORM = "groupForm";
	public static final String GROUP_NAME_SKILLS_FORM = "groupNameSkillsForm";

	@Override
	public void performValidation(GroupNameSkillsForm groupForm) {
		if (!GroupValidationUtil.valid(groupForm.getName(), GroupValidationUtil.Field.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(GROUP_FORM, "name"), "Name is missing");
			addFieldErrorIfMessage(fieldKeyBuilder(GROUP_NAME_SKILLS_FORM, "name"), "Name is missing");
		}
	}

	@Override
	protected GroupNameSkillsForm getFormFromValueStack(ValueStack valueStack) {
		GroupNameSkillsForm groupNameSkillsForm = (GroupNameSkillsForm) valueStack.findValue(GROUP_NAME_SKILLS_FORM, GroupNameSkillsForm.class);
		if (groupNameSkillsForm != null) {
			return groupNameSkillsForm;
		}

		return (GroupForm) valueStack.findValue(GROUP_FORM, GroupForm.class);
	}
}
