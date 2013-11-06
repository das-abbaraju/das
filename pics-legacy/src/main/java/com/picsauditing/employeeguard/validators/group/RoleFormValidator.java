package com.picsauditing.employeeguard.validators.group;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.forms.operator.OperatorJobRoleForm;
import com.picsauditing.employeeguard.validators.AbstractValidator;

public class RoleFormValidator extends AbstractValidator<GroupNameSkillsForm> {

	public static final String ROLE_FORM = "roleForm";
	public static final String ROLE_NAME_SKILLS_FORM = "roleNameSkillsForm";

	@Override
	public void performValidation(GroupNameSkillsForm groupForm) {
		if (!GroupValidationUtil.valid(groupForm.getName(), GroupValidationUtil.Field.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(ROLE_FORM, "name"), "Name is missing");
			addFieldErrorIfMessage(fieldKeyBuilder(ROLE_NAME_SKILLS_FORM, "name"), "Name is missing");
		}
	}

	@Override
	protected GroupNameSkillsForm getFormFromValueStack(ValueStack valueStack) {
		GroupNameSkillsForm groupNameSkillsForm = (GroupNameSkillsForm) valueStack.findValue(ROLE_NAME_SKILLS_FORM, GroupNameSkillsForm.class);
		if (groupNameSkillsForm != null) {
			return groupNameSkillsForm;
		}

		return (OperatorJobRoleForm) valueStack.findValue(ROLE_FORM, OperatorJobRoleForm.class);
	}
}
