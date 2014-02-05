package com.picsauditing.employeeguard.validators.project;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.operator.ProjectForm;
import com.picsauditing.employeeguard.forms.operator.ProjectNameLocationForm;
import com.picsauditing.employeeguard.forms.operator.ProjectNameSkillsForm;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;

public class ProjectFormValidator extends AbstractBasicAndDuplicateValidator<ProjectNameLocationForm> {
	public static final String PROJECT_FORM = "projectForm";
	public static final String PROJECT_NAME_SKILLS_FORM = "projectNameSkillsForm";

	private String formName = PROJECT_FORM;

	@Override
	public void doFormValidation(final ProjectNameLocationForm projectNameLocationForm) {
		if (!ProjectValidationUtil.valid(projectNameLocationForm, ProjectValidationUtil.ProjectField.NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(formName, "name"), "Project name is missing");
		}
	}

	@Override
	protected ProjectNameLocationForm getFormFromValueStack(ValueStack valueStack) {
		if (valueStack.findValue(PROJECT_NAME_SKILLS_FORM, ProjectNameSkillsForm.class) != null) {
			formName = PROJECT_NAME_SKILLS_FORM;
			return (ProjectNameSkillsForm) valueStack.findValue(PROJECT_NAME_SKILLS_FORM, ProjectNameSkillsForm.class);
		}

		return (ProjectForm) valueStack.findValue(PROJECT_FORM, ProjectForm.class);
	}

    @Override
    protected String getDuplicateErrorMessage() {
        return "Name";
    }
}
