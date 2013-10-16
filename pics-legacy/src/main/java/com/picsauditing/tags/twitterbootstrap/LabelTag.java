package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LabelTag extends ElementComponentTagSupport {

	private static final long serialVersionUID = -54732812149479709L;

	private String labelName;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Label(stack);
	}

	@Override
	protected void populateParams() {
		super.populateParams();

		Label label = (Label) component;

		label.setFieldName(labelName);

		label.setLabelName(labelName);

		label.setDynamicAttributes(dynamicAttributes);
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

}