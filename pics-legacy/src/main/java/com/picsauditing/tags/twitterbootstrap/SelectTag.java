package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SelectTag extends ElementComponentTagSupport {

	private static final long serialVersionUID = -54732812149479709L;

	private String selectName;

	private String disabled;
	private String multiple;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Select(stack);
	}

	@Override
	protected void populateParams() {
		super.populateParams();

		Select select = (Select) component;

		select.setFieldName(selectName);

		select.setSelectName(selectName);

		select.setDisabled(disabled);

		select.setMultiple(multiple);

		select.setDynamicAttributes(dynamicAttributes);
	}

	public void setSelectName(String selectName) {
		this.selectName = selectName;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}
}