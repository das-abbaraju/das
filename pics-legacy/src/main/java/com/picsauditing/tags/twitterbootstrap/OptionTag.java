package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OptionTag extends ElementComponentTagSupport {

	private static final long serialVersionUID = -54732812149479709L;

	private String selected;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Option(stack);
	}

	@Override
	protected void populateParams() {
		super.populateParams();

		Option option = (Option) component;

		option.setSelected(selected);

		option.setDynamicAttributes(dynamicAttributes);
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
}