
package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TextareaTag extends ElementComponentTagSupport {

	private static final long serialVersionUID = -54732812149479709L;

	private String textareaName;

	private String disabled;
	private String readonly;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Textarea(stack);
	}

	@Override
	protected void populateParams() {
		super.populateParams();

		Textarea textarea = (Textarea) component;

		textarea.setFieldName(textareaName);

		textarea.setTextareaName(textareaName);

		textarea.setDisabled(disabled);

		textarea.setReadonly(readonly);

		textarea.setDynamicAttributes(dynamicAttributes);
	}

	public void setTextareaName(String textareaName) {
		this.textareaName = textareaName;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}
}