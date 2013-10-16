package com.picsauditing.tags.twitterbootstrap;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import java.util.HashMap;
import java.util.Map;

public abstract class ElementComponentTagSupport extends ComponentTagSupport implements DynamicAttributes {

	private static final long serialVersionUID = 309520469152488363L;

	// dynamic attributes
	protected Map<String, Object> dynamicAttributes = new HashMap<String, Object>();

	@Override
	protected void populateParams() {
		super.populateParams();
	}

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		boolean isOgnlParsible = value != null && value instanceof String && !value.toString().contains("-");

		if (value != null && value instanceof String && isOgnlParsible) {
			dynamicAttributes.put(localName, String.valueOf(ObjectUtils.defaultIfNull(findValue(value.toString()), value)));
		} else {
			dynamicAttributes.put(localName, value);
		}
	}

}
