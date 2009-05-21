package com.picsauditing.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.picsauditing.util.Strings;

/**
 * usage: <pics:help page="Logging In" icon="false" message="How to Login" />
 * 
 * @author Trevor
 * 
 */
public class HelpLink extends TagSupport {
	private String page = "Help_Center";
	private boolean icon = true;
	private String message = null;

	@Override
	public int doStartTag() throws JspException {
		try {
			String link = "target=\"_BLANK\" href=\"http://help.picsauditing.com/wiki/" + page.replace(' ', '_') + "\"";
			
			String html = null;
			if (icon) {
				if (Strings.isEmpty(message)) {
					html = "<a " + link + "><img width='12' height='12' src='images/help.gif'>";
				} else {
					// Since we have an icon and a message, let's put the icon in the background
					html = "<a style=\"background: url('images/help.gif') no-repeat left center; padding-left: 17px; margin-left: 2px;\" " + 
					link + ">" +			message + "";
				}
			} else {
				// No Icon
				if (Strings.isEmpty(message))
					html = "<a " + link + ">" + page.replace('_', ' ');
				else
					html = "<a " + link + ">" + message;
			}
			
			html += "</a>";
			pageContext.getOut().print(html.toString());
		} catch (Exception ex) {
			throw new JspTagException("HelpLink: " + ex.getMessage());
		}
		return SKIP_BODY;
	}

	public int doEndTag() {
		return EVAL_PAGE;
	}

	/***** GETTERS/SETTER *****/

	public String getMessage() {
		return message;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		if (page == null)
			return;
		this.page = page.trim();
	}

	public boolean isIcon() {
		return icon;
	}

	public void setIcon(boolean icon) {
		this.icon = icon;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
