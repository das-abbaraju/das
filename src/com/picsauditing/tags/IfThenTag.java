package com.picsauditing.tags;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * The then part of an if tag.
 */
@SuppressWarnings("serial")
public class IfThenTag extends BodyTagSupport {
	public int doStartTag() throws JspTagException {
		IfTag parent = (IfTag) findAncestorWithClass(this, IfTag.class);
		if (parent == null) {
			throw new JspTagException("then not inside if");
		} else if (!parent.hasCondition()) {
			String warning = "condition tag must come before then tag";
			throw new JspTagException(warning);
		}
		return (EVAL_BODY_TAG);
	}

	public int doAfterBody() {
		IfTag parent = (IfTag) findAncestorWithClass(this, IfTag.class);
		if (parent.getCondition()) {
			try {
				BodyContent body = getBodyContent();
				JspWriter out = body.getEnclosingWriter();
				out.print(body.getString());
			} catch (IOException ioe) {
				System.out.println("Error in IfThenTag: " + ioe);
			}
		}
		return (SKIP_BODY);
	}
}
