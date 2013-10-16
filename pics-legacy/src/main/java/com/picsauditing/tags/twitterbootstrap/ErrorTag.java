package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorTag extends ElementComponentTagSupport {

	private static final long serialVersionUID = 404768842811995609L;

	private String errorName;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Error(stack);
	}

	@Override
	protected void populateParams() {
		super.populateParams();

		Error error = (Error) component;

		error.setFieldName(errorName);

		error.setErrorName(errorName);

		error.setDynamicAttributes(dynamicAttributes);
	}

	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}
}