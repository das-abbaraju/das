package com.picsauditing.employeeguard.validators.group;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.GroupForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;

public class GroupFormValidator extends AbstractBasicAndDuplicateValidator<GroupNameSkillsForm> {

	public static final String GROUP_FORM = "groupForm";
	public static final String GROUP_NAME_SKILLS_FORM = "groupNameSkillsForm";

	@Override
	public void doFormValidation(GroupNameSkillsForm groupForm) {
		if (!GroupValidationUtil.valid(groupForm.getName(), GroupValidationUtil.Field.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(GROUP_FORM, "name"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.GROUP.NAME"));
			addFieldErrorIfMessage(fieldKeyBuilder(GROUP_NAME_SKILLS_FORM, "name"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.GROUP.NAME"));
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

    @Override
    protected String getDuplicateErrorMessage() {
        return EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.GROUP.DUPLICATE");
    }
}
