package com.picsauditing.tags;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.math.NumberUtils;

import com.picsauditing.access.BetaPool;
import com.picsauditing.access.Permissions;

/**
 * The condition part of an if tag.
 */
@SuppressWarnings("serial")
public class IfConditionTag extends BodyTagSupport {
	private String toggleName = null;
	
	public int doStartTag() throws JspTagException {
		IfTag parent = (IfTag) findAncestorWithClass(this, IfTag.class);	
		parent.setCondition(isToggle(toggleName));	
		return (EVAL_BODY_TAG);
	}
	private boolean isToggle(String toggleName) {
		HttpSession session = pageContext.getSession();
		Permissions permissions = (Permissions) session.getAttribute("permissions");

		Map<String, String> toggles = permissions.getToggles();
		
		if (toggles.containsKey("Toggle." + toggleName)) {
			BetaPool betaPool = BetaPool.getBetaPoolByBetaLevel(NumberUtils.toInt(toggles.get("Toggle." + toggleName), 0));
			return BetaPool.isUserBetaTester(permissions, betaPool);
		} else
			return false;
	}
	public int doAfterBody() {		
		return (SKIP_BODY);
	}
	public String getToggleName() {
		return toggleName;
	}

	public void setToggleName(String toggleName) {
		this.toggleName = toggleName;
	}
}
