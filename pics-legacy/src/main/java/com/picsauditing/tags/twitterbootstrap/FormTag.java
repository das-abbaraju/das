package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormTag extends ElementComponentTagSupport {

	private static final long serialVersionUID = 8229586023730388489L;

	private String formName;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Form(stack);
	}

	@Override
	protected void populateParams() {
		super.populateParams();

		Form form = (Form) component;

		form.setFieldName(formName);

		form.setFormName(formName);

		form.setDynamicAttributes(dynamicAttributes);
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

}
