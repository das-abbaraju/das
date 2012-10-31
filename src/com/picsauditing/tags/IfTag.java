package com.picsauditing.tags;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.If;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

/**
 * A tag that acts like an if/then/else for application feature toggles.
 */
public class IfTag extends ComponentTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 4448870162549923833L;
	private String name = null;
	private Map<String, Object> tagAttributes = new HashMap<String, Object>();

	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new If(stack);
	}

	protected void populateParams() {
		((If) getComponent()).setTest(isToggle(name) + "");
	}

	private boolean isToggle(String name) {
		FeatureToggle featureToggle = SpringUtils.getBean("FeatureToggle");
		for (String key : tagAttributes.keySet()) {
			featureToggle.addToggleVariable(key, tagAttributes.get(key));
		}
		return featureToggle.isFeatureEnabled(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		Object valueStackObject = findValue(value.toString());
		tagAttributes.put(localName, valueStackObject);
	}

}
