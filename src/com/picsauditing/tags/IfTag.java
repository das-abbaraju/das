package com.picsauditing.tags;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.If;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.access.BetaPool;
import com.picsauditing.access.Permissions;

/**
 * A tag that acts like an if/then/else.
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
		HttpSession session = pageContext.getSession();
		Permissions permissions = (Permissions) session.getAttribute("permissions");
		// TODO For a guest user (not logged in) there is no permissions object in session state, thus no ready access to the toggles list.  So, we need to look it up another way.
		if (permissions == null) {
			// TODO In the mean time, we'll assume that the toggle in question is never turned on for a guest user.
			return false;
		}
		
		Map<String, String> toggles = permissions.getToggles();

		if (toggles.containsKey("Toggle." + name)) {
			BetaPool betaPool = BetaPool.getBetaPoolByBetaLevel(NumberUtils.toInt(toggles.get("Toggle." + name), 0));
			return BetaPool.isUserBetaTester(permissions, betaPool);
		} else
			return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
