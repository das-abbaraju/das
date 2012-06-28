package com.picsauditing.tags;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.BetaPool;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;

/**
 * usage: <pics:toggle name="SwitchUserServer"/>
 * 
 * @author Lei Wang
 * 
 */
@SuppressWarnings("serial")
public class ToggleFeature extends TagSupport {
	
	private String toggleName = null;
	private int userID = 0;
	private String toggleTitle = null;

	private static final Logger logger = LoggerFactory.getLogger(ToggleFeature.class);
	
	@Override
	public int doStartTag() throws JspException {
		try {
			if(isToggle(toggleName)){
				if (userID > 0)
					pageContext.getOut().print("<li><a class=\"btn\" href=\"UsersManage!switchUserToDifferentServer.action?user="+userID+"\">"+getButtonTitle(toggleTitle)+"</li></a>");					
			}				
			
		} catch (Exception e) {
			logger.error("Exception throw during processing of toggle feature: ", e);
			throw new JspTagException("Toggle feature: " + e.getMessage());
		}
		//return SKIP_BODY;
		return EVAL_BODY_INCLUDE;
	}
	private String getButtonTitle(String toggleTitle){
		TranslationActionSupport tas = new TranslationActionSupport();		
		return tas.getText(toggleTitle);
	}
	private boolean isToggle(String toggleName) {
		HttpSession session = pageContext.getSession();
		Permissions permissions = (Permissions) session.getAttribute("permissions");

		Map<String, String> toggles = permissions.getToggles();

		if (toggles.containsKey("Toggle." + toggleName)) {
			BetaPool betaPool = BetaPool.getBetaPoolByBetaLevel(NumberUtils.toInt(toggles.get("Toggle." + toggleName),
					0));
			return BetaPool.isUserBetaTester(permissions, betaPool);
		} else
			return false;
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public String getToggleName() {
		return toggleName;
	}

	public void setToggleName(String toggleName) {
		this.toggleName = toggleName;
	}
	public int getUserID(){
		return userID;
	}
	public void setUserID(int userID){
		this.userID = userID;
	}
	public String getToggleTitle(){
		return toggleTitle;
	}
	public void setToggleTitle(String toggleTitle){
		this.toggleTitle = toggleTitle;
	}
}
