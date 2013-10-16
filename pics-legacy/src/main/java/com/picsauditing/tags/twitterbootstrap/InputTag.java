package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InputTag extends ElementComponentTagSupport {

	private static final long serialVersionUID = -54732812149479709L;

	private String inputName;

	private String checked;
	private String disabled;
	private String readonly;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Input(stack);
	}

	@Override
	protected void populateParams() {
		super.populateParams();

		Input input = (Input) component;

		input.setFieldName(inputName);

		input.setInputName(inputName);

		input.setChecked(checked);

		input.setDisabled(disabled);

		input.setReadonly(readonly);

		input.setDynamicAttributes(dynamicAttributes);
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}
}