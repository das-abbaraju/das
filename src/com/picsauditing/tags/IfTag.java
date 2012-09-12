package com.picsauditing.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.If;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

/**
 * A tag that acts like an if/then/else for application feature toggles.
 */
public class IfTag extends ComponentTagSupport {
	private static final long serialVersionUID = 4448870162549923833L;
	private String name = null;

	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new If(stack);
	}

	protected void populateParams() {
		((If) getComponent()).setTest(isToggle(name) + "");
	}

	private boolean isToggle(String name) {
		FeatureToggle featureToggle = SpringUtils.getBean("FeatureToggle");
		return featureToggle.isFeatureEnabled(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
