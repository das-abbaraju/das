package com.picsauditing.employeeguard.validators.group;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.operator.OperatorJobRoleForm;
import com.picsauditing.employeeguard.forms.operator.RoleNameSkillsForm;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;

public class RoleFormValidator extends AbstractBasicAndDuplicateValidator<RoleNameSkillsForm> {

	public static final String ROLE_FORM = "roleForm";
	public static final String ROLE_NAME_SKILLS_FORM = "roleNameSkillsForm";

	@Override
	public void doFormValidation(RoleNameSkillsForm roleForm) {
		if (!GroupValidationUtil.valid(roleForm.getName(), GroupValidationUtil.Field.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(ROLE_FORM, "name"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.ROLE.NAME"));
			addFieldErrorIfMessage(fieldKeyBuilder(ROLE_NAME_SKILLS_FORM, "name"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.ROLE.NAME"));
		}
	}

	@Override
	protected RoleNameSkillsForm getFormFromValueStack(ValueStack valueStack) {
		RoleNameSkillsForm roleNameSkillsForm = (RoleNameSkillsForm) valueStack.findValue(ROLE_NAME_SKILLS_FORM, RoleNameSkillsForm.class);
		if (roleNameSkillsForm != null) {
			return roleNameSkillsForm;
		}

		return (OperatorJobRoleForm) valueStack.findValue(ROLE_FORM, OperatorJobRoleForm.class);
	}

	@Override
	protected String getDuplicateErrorMessage() {
		return EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.ROLE.DUPLICATE");
	}
}
