package com.picsauditing.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * usage: <pics:help page="Logging In" icon="false" message="How to Login" />
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class FieldHelp extends TagSupport {

	private String title = null;

	@Override
	public int doStartTag() throws JspException {
		try {
			pageContext.getOut().print(
					"<div class=\"fieldhelp\"><h3>" + ((title == null) ? "Field Help" : title) + "</h3>");
		} catch (Exception e) {
			throw new JspTagException("FieldHelp: " + e.getMessage());
		}
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().print("</div>");
		} catch (IOException e) {
			throw new JspTagException("FieldHelp: " + e.getMessage());
		}
		return EVAL_PAGE;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}