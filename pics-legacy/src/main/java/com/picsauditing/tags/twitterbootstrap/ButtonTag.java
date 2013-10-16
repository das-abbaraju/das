package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ButtonTag extends ElementComponentTagSupport {

	private static final long serialVersionUID = -54732812149479709L;

	private String buttonName;

	private String disabled;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Button(stack);
	}

	@Override
	protected void populateParams() {
		super.populateParams();

		Button button = (Button) component;

		button.setFieldName(buttonName);

		button.setButtonName(buttonName);

		button.setDisabled(disabled);

		button.setDynamicAttributes(dynamicAttributes);
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

}